package com.pdf2q.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdf2q.config.DeepSeekProperties;
import com.pdf2q.dto.GenerateResponse;
import com.pdf2q.dto.GenerateResponse.QuestionDto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class DeepSeekService {

  private static final Set<String> VALID_ANSWERS = Set.of("A", "B", "C", "D");

  private final DeepSeekProperties properties;
  private final RestClient.Builder restClientBuilder;
  private final ObjectMapper objectMapper;

  public DeepSeekService(
      DeepSeekProperties properties,
      RestClient.Builder restClientBuilder,
      ObjectMapper objectMapper) {
    this.properties = properties;
    this.restClientBuilder = restClientBuilder;
    this.objectMapper = objectMapper;
  }

  public List<QuestionDto> generateQuestions(String rawText, int count) {
    if (!StringUtils.hasText(properties.getApiKey())) {
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "DEEPSEEK_API_KEY is not set. Copy application-local.yml.example to application-local.yml");
    }

    String text = rawText == null ? "" : rawText.trim();
    if (!StringUtils.hasText(text)) {
      throw new ResponseStatusException(BAD_REQUEST, "text is required");
    }

    int n = Math.min(Math.max(count, 1), 20);
    int maxChars = properties.getMaxTextChars();
    String clipped = text.length() > maxChars ? text.substring(0, maxChars) : text;

    String baseUrl = properties.getBaseUrl().replaceAll("/$", "");
    Map<String, Object> body = buildRequestBody(clipped, n);

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
          "DeepSeek API error: " + truncate(ex.getMessage(), 300),
          ex);
    }

    if (!StringUtils.hasText(content)) {
      throw new ResponseStatusException(BAD_GATEWAY, "Empty response from DeepSeek");
    }

    return normalizeQuestions(extractJson(content));
  }

  private Map<String, Object> buildRequestBody(String text, int count) {
    Map<String, Object> system = Map.of(
        "role", "system",
        "content", "You generate multiple-choice quizzes. Reply with JSON only.");
    Map<String, Object> user = Map.of(
        "role", "user",
        "content", buildPrompt(text, count));

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("model", properties.getModel());
    body.put("temperature", 0.3);
    body.put("messages", List.of(system, user));
    return body;
  }

  private String buildPrompt(String text, int count) {
    return """
        你是出题助手。根据下面的学习材料，生成 %d 道四选一选择题。

        要求：
        1. 每题必须有题干、A/B/C/D 四个选项、唯一正确答案、简短解析。
        2. 解析需尽量引用原文依据（可概括，但不得编造原文没有的事实）。
        3. 干扰项要合理，不要一眼假。
        4. 只输出 JSON，不要 Markdown，不要其它说明。JSON 格式：
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

        学习材料：
        """
        .formatted(count)
        + "\"\"\"\n"
        + text
        + "\n\"\"\"";
  }

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

  private List<QuestionDto> normalizeQuestions(JsonNode payload) {
    JsonNode array = payload.get("questions");
    if (array == null || !array.isArray()) {
      throw new ResponseStatusException(BAD_GATEWAY, "Model JSON missing questions array");
    }

    List<QuestionDto> result = new ArrayList<>();
    int i = 0;
    for (JsonNode q : array) {
      String question = textOrEmpty(q.get("question"));
      String answer = textOrEmpty(q.get("answer")).toUpperCase();
      JsonNode optionsNode = q.get("options");

      if (!StringUtils.hasText(question) || !VALID_ANSWERS.contains(answer) || optionsNode == null) {
        throw new ResponseStatusException(BAD_GATEWAY, "Invalid question at index " + i);
      }

      Map<String, String> options = new LinkedHashMap<>();
      for (String key : List.of("A", "B", "C", "D")) {
        String value = textOrEmpty(optionsNode.get(key));
        if (!StringUtils.hasText(value)) {
          throw new ResponseStatusException(
              BAD_GATEWAY, "Question " + (i + 1) + " missing option " + key);
        }
        options.put(key, value);
      }

      QuestionDto dto = new QuestionDto();
      dto.setQuestion(question);
      dto.setOptions(options);
      dto.setAnswer(answer);
      dto.setExplanation(textOrEmpty(q.get("explanation")));
      result.add(dto);
      i++;
    }
    return result;
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
