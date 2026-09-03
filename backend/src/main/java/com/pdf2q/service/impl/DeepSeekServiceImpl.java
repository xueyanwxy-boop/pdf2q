package com.pdf2q.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdf2q.config.DeepSeekProperties;
import com.pdf2q.domain.QuestionType;
import com.pdf2q.dto.GenerateResponse.QuestionDto;
import com.pdf2q.service.DeepSeekService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * DeepSeek 出题服务实现。
 */
@Service
public class DeepSeekServiceImpl implements DeepSeekService {

  private static final Set<String> ABCD = Set.of("A", "B", "C", "D");
  private static final Set<String> AB = Set.of("A", "B");

  private final DeepSeekProperties properties;
  private final RestClient.Builder restClientBuilder;
  private final ObjectMapper objectMapper;

  public DeepSeekServiceImpl(
      DeepSeekProperties properties,
      RestClient.Builder restClientBuilder,
      ObjectMapper objectMapper) {
    this.properties = properties;
    this.restClientBuilder = restClientBuilder;
    this.objectMapper = objectMapper;
  }

  /**
   * 按各题型数量分别出题后合并（顺序：单选 → 多选 → 判断）。
   * 每种 0–20，且至少一种 &gt; 0。
   */
  @Override
  public List<QuestionDto> generateQuestions(
      String rawText, int singleCount, int multipleCount, int judgeCount) {
    int single = clamp(singleCount);
    int multiple = clamp(multipleCount);
    int judge = clamp(judgeCount);
    if (single + multiple + judge <= 0) {
      throw new ResponseStatusException(BAD_REQUEST, "至少选择一种题型且数量大于 0");
    }

    String text = prepareText(rawText);
    List<QuestionDto> all = new ArrayList<>();
    if (single > 0) {
      all.addAll(generateByType(text, QuestionType.single, single));
    }
    if (multiple > 0) {
      all.addAll(generateByType(text, QuestionType.multiple, multiple));
    }
    if (judge > 0) {
      all.addAll(generateByType(text, QuestionType.judge, judge));
    }
    return all;
  }

  /** 生成单一题型的若干道题。 */
  private List<QuestionDto> generateByType(String text, QuestionType type, int count) {
    ensureApiKey();
    String baseUrl = properties.getBaseUrl().replaceAll("/$", "");
    Map<String, Object> body = buildRequestBody(text, type, count);

    String content;
    try {
      JsonNode response = restClientBuilder
          .build()
          .post()
          .uri(baseUrl + "/v1/chat/completions")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + properties.getApiKey())
          .body(body)
          .retrieve()
          .body(JsonNode.class);
      content = response.path("choices").path(0).path("message").path("content").asText(null);
    } catch (Exception ex) {
      throw new ResponseStatusException(
          BAD_GATEWAY,
          "DeepSeek API error (" + type + "): " + truncate(ex.getMessage(), 300),
          ex);
    }

    if (!StringUtils.hasText(content)) {
      throw new ResponseStatusException(BAD_GATEWAY, "Empty response from DeepSeek for " + type);
    }
    return normalizeQuestions(extractJson(content), type);
  }

  private void ensureApiKey() {
    if (!StringUtils.hasText(properties.getApiKey())) {
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "DEEPSEEK_API_KEY is not set. Copy application-local.yml.example to application-local.yml");
    }
  }

  private String prepareText(String rawText) {
    String text = rawText == null ? "" : rawText.trim();
    if (!StringUtils.hasText(text)) {
      throw new ResponseStatusException(BAD_REQUEST, "text is required");
    }
    int maxChars = properties.getMaxTextChars();
    return text.length() > maxChars ? text.substring(0, maxChars) : text;
  }

  private static int clamp(int value) {
    return Math.min(Math.max(value, 0), 20);
  }

  /** 组装发给 DeepSeek 的 chat/completions 请求体。 */
  private Map<String, Object> buildRequestBody(String text, QuestionType type, int count) {
    Map<String, Object> system = Map.of(
        "role", "system",
        "content", "You generate quiz questions. Reply with JSON only.");
    Map<String, Object> user = Map.of(
        "role", "user",
        "content", buildPrompt(text, type, count));

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("model", properties.getModel());
    body.put("temperature", 0.3);
    body.put("messages", List.of(system, user));
    return body;
  }

  /** 按题型构造 Prompt。 */
  private String buildPrompt(String text, QuestionType type, int count) {
    String typeRules = switch (type) {
      case single -> """
          题型：单选题。
          - 每题 4 个选项 A/B/C/D，有且仅有 1 个正确答案。
          - answer 字段填单个字母，如 "B"。
          """;
      case multiple -> """
          题型：多选题。
          - 每题 4 个选项 A/B/C/D，正确答案至少 2 个。
          - answer 字段填多个字母，用英文逗号分隔且按字母排序，如 "A,C"。
          """;
      case judge -> """
          题型：判断题。
          - 选项固定为 A=对、B=错（不要改选项文字）。
          - answer 只能是 "A" 或 "B"。
          """;
    };

    return """
        你是出题助手。根据下面的学习材料，生成 %d 道题目。

        %s
        通用要求：
        1. 每题包含 question、options、answer、explanation。
        2. 解析需尽量依据原文，不得编造原文没有的事实。
        3. 只输出 JSON，不要 Markdown。格式：
        {
          "questions": [
            {
              "question": "题干",
              "options": { "A": "...", "B": "...", "C": "...", "D": "..." },
              "answer": "A",
              "explanation": "解析"
            }
          ]
        }
        （判断题 options 只需 A、B，值为「对」「错」。）

        学习材料：
        """.formatted(count, typeRules)
        + "\"\"\"\n"
        + text
        + "\n\"\"\"";
  }

  /** 从模型回复中提取 JSON。 */
  private JsonNode extractJson(String content) {
    String trimmed = content.trim();
    try {
      return objectMapper.readTree(trimmed);
    } catch (Exception ignored) {
      int start = trimmed.indexOf('{');
      int end = trimmed.lastIndexOf('}');
      if (start >= 0 && end > start) {
        try {
          return objectMapper.readTree(trimmed.substring(start, end + 1));
        } catch (Exception ex) {
          throw new ResponseStatusException(BAD_GATEWAY, "Model response is not valid JSON", ex);
        }
      }
      throw new ResponseStatusException(BAD_GATEWAY, "Model response is not valid JSON");
    }
  }

  /** 校验并规范化某题型的题目列表。 */
  private List<QuestionDto> normalizeQuestions(JsonNode payload, QuestionType type) {
    JsonNode array = payload.get("questions");
    if (array == null || !array.isArray()) {
      throw new ResponseStatusException(BAD_GATEWAY, "Model JSON missing questions array");
    }

    List<QuestionDto> result = new ArrayList<>();
    int i = 0;
    for (JsonNode q : array) {
      String question = textOrEmpty(q.get("question"));
      JsonNode optionsNode = q.get("options");
      if (!StringUtils.hasText(question) || optionsNode == null) {
        throw new ResponseStatusException(BAD_GATEWAY, "Invalid question at index " + i);
      }

      Map<String, String> options = new LinkedHashMap<>();
      if (type == QuestionType.judge) {
        options.put("A", firstNonBlank(textOrEmpty(optionsNode.get("A")), "对"));
        options.put("B", firstNonBlank(textOrEmpty(optionsNode.get("B")), "错"));
      } else {
        for (String key : List.of("A", "B", "C", "D")) {
          String value = textOrEmpty(optionsNode.get(key));
          if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(
                BAD_GATEWAY, "Question " + (i + 1) + " missing option " + key);
          }
          options.put(key, value);
        }
      }

      String answer = normalizeAnswer(textOrEmpty(q.get("answer")), type);
      QuestionDto dto = new QuestionDto();
      dto.setType(type);
      dto.setQuestion(question);
      dto.setOptions(options);
      dto.setAnswer(answer);
      dto.setExplanation(textOrEmpty(q.get("explanation")));
      result.add(dto);
      i++;
    }
    return result;
  }

  /** 规范化答案：单选/判断单字母；多选排序去重为 A,C。 */
  private String normalizeAnswer(String raw, QuestionType type) {
    String cleaned = raw.toUpperCase(Locale.ROOT).replace(" ", "");
    if (type == QuestionType.multiple) {
      Set<String> letters = Arrays.stream(cleaned.split("[,|、;/]+"))
          .map(String::trim)
          .filter(ABCD::contains)
          .collect(Collectors.toCollection(java.util.TreeSet::new));
      if (letters.size() < 2) {
        throw new ResponseStatusException(BAD_GATEWAY, "Multiple-choice answer needs at least 2 options");
      }
      return String.join(",", letters);
    }

    // 兼容模型直接返回「对/错」
    if (type == QuestionType.judge) {
      if (cleaned.contains("对") || cleaned.equals("TRUE") || cleaned.equals("T") || cleaned.equals("YES")) {
        return "A";
      }
      if (cleaned.contains("错") || cleaned.equals("FALSE") || cleaned.equals("F") || cleaned.equals("NO")) {
        return "B";
      }
    }

    String letter = cleaned.length() >= 1 ? cleaned.substring(0, 1) : "";
    Set<String> allowed = type == QuestionType.judge ? AB : ABCD;
    if (!allowed.contains(letter)) {
      throw new ResponseStatusException(BAD_GATEWAY, "Invalid answer: " + raw);
    }
    return letter;
  }

  private static String firstNonBlank(String value, String fallback) {
    return StringUtils.hasText(value) ? value : fallback;
  }

  private static String textOrEmpty(JsonNode node) {
    return node == null || node.isNull() ? "" : node.asText("").trim();
  }

  private static String truncate(String value, int max) {
    if (value == null) {
      return "";
    }
    return value.length() <= max ? value : value.substring(0, max);
  }
}
