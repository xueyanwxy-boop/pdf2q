package com.pdf2q.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdf2q.dao.QuestionDao;
import com.pdf2q.dao.QuizProgressDao;
import com.pdf2q.dao.QuizSetDao;
import com.pdf2q.domain.Question;
import com.pdf2q.domain.QuizProgress;
import com.pdf2q.domain.QuizSet;
import com.pdf2q.dto.CreateQuizSetRequest;
import com.pdf2q.dto.GenerateResponse.QuestionDto;
import com.pdf2q.dto.QuizDtos;
import com.pdf2q.dto.QuizDtos.ProgressDto;
import com.pdf2q.dto.QuizDtos.QuizSetDetail;
import com.pdf2q.dto.QuizDtos.QuizSetSummary;
import com.pdf2q.dto.SaveProgressRequest;
import com.pdf2q.dto.SaveProgressRequest.AnswerItem;
import com.pdf2q.service.DeepSeekService;
import com.pdf2q.service.QuizSetService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** 题库业务实现。 */
@Service
public class QuizSetServiceImpl implements QuizSetService {

  private final QuizSetDao quizSetDao;
  private final QuestionDao questionDao;
  private final QuizProgressDao quizProgressDao;
  private final DeepSeekService deepSeekService;
  private final ObjectMapper objectMapper;

  public QuizSetServiceImpl(
      QuizSetDao quizSetDao,
      QuestionDao questionDao,
      QuizProgressDao quizProgressDao,
      DeepSeekService deepSeekService,
      ObjectMapper objectMapper) {
    this.quizSetDao = quizSetDao;
    this.questionDao = questionDao;
    this.quizProgressDao = quizProgressDao;
    this.deepSeekService = deepSeekService;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizSetSummary> list(String ownerToken) {
    List<QuizSet> sets = quizSetDao.selectByOwnerToken(ownerToken);
    if (sets.isEmpty()) {
      return List.of();
    }
    List<Long> ids = sets.stream().map(QuizSet::getId).toList();
    Map<Long, QuizProgress> progressMap = quizProgressDao
        .selectByOwnerAndQuizSetIds(ownerToken, ids)
        .stream()
        .collect(Collectors.toMap(QuizProgress::getQuizSetId, Function.identity(), (a, b) -> a));

    List<QuizSetSummary> result = new ArrayList<>();
    for (QuizSet set : sets) {
      QuizSetSummary summary = new QuizSetSummary();
      summary.setId(set.getId());
      summary.setTitle(set.getTitle());
      summary.setQuestionCount(set.getQuestionCount());
      summary.setCreatedAt(set.getCreatedAt());

      QuizProgress progress = progressMap.get(set.getId());
      List<AnswerItem> answers = progress == null ? List.of() : readAnswers(progress.getAnswersJson());
      int answered = answers.size();
      summary.setAnsweredCount(answered);
      summary.setCurrentIndex(progress == null ? 0 : progress.getCurrentIndex());
      summary.setFinished(answered >= set.getQuestionCount() && set.getQuestionCount() > 0);
      result.add(summary);
    }
    return result;
  }

  @Override
  @Transactional
  public QuizSetDetail create(String ownerToken, CreateQuizSetRequest request) {
    int single = request.getSingleCount() == null ? 0 : request.getSingleCount();
    int multiple = request.getMultipleCount() == null ? 0 : request.getMultipleCount();
    int judge = request.getJudgeCount() == null ? 0 : request.getJudgeCount();
    List<QuestionDto> generated = deepSeekService.generateQuestions(
        request.getText(), single, multiple, judge);

    QuizSet set = new QuizSet();
    set.setOwnerToken(ownerToken);
    set.setTitle(request.getTitle().trim());
    set.setQuestionCount(generated.size());
    set.setCreatedAt(Instant.now());
    quizSetDao.insert(set);

    List<Question> questions = new ArrayList<>();
    int seq = 0;
    for (QuestionDto q : generated) {
      Question entity = new Question();
      entity.setQuizSetId(set.getId());
      entity.setSeq(seq++);
      entity.setType(q.getType());
      entity.setStem(q.getQuestion());
      entity.setOptionA(q.getOptions().getOrDefault("A", ""));
      entity.setOptionB(q.getOptions().getOrDefault("B", ""));
      entity.setOptionC(q.getOptions().getOrDefault("C", ""));
      entity.setOptionD(q.getOptions().getOrDefault("D", ""));
      entity.setAnswer(q.getAnswer());
      entity.setExplanation(q.getExplanation() == null ? "" : q.getExplanation());
      questions.add(entity);
    }
    questionDao.insertBatch(questions);
    return toDetail(set, questions, emptyProgress());
  }

  @Override
  @Transactional(readOnly = true)
  public QuizSetDetail get(String ownerToken, Long id) {
    QuizSet set = requireOwned(ownerToken, id);
    List<Question> questions = questionDao.selectByQuizSetId(id);
    QuizProgress progress = quizProgressDao.selectByOwnerAndQuizSet(ownerToken, id);
    return toDetail(set, questions, toProgressDto(set.getQuestionCount(), progress));
  }

  @Override
  @Transactional
  public ProgressDto saveProgress(String ownerToken, Long id, SaveProgressRequest request) {
    QuizSet set = requireOwned(ownerToken, id);
    if (request.getCurrentIndex() >= set.getQuestionCount()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currentIndex out of range");
    }

    QuizProgress progress = quizProgressDao.selectByOwnerAndQuizSet(ownerToken, id);
    Instant now = Instant.now();
    if (progress == null) {
      progress = new QuizProgress();
      progress.setOwnerToken(ownerToken);
      progress.setQuizSetId(id);
      progress.setCurrentIndex(request.getCurrentIndex());
      progress.setAnswersJson(writeAnswers(request.getAnswers()));
      progress.setUpdatedAt(now);
      quizProgressDao.insert(progress);
    } else {
      progress.setCurrentIndex(request.getCurrentIndex());
      progress.setAnswersJson(writeAnswers(request.getAnswers()));
      progress.setUpdatedAt(now);
      quizProgressDao.update(progress);
    }
    return toProgressDto(set.getQuestionCount(), progress);
  }

  @Override
  @Transactional
  public ProgressDto resetProgress(String ownerToken, Long id) {
    requireOwned(ownerToken, id);
    quizProgressDao.deleteByOwnerAndQuizSet(ownerToken, id);
    return emptyProgress();
  }

  @Override
  @Transactional
  public void delete(String ownerToken, Long id) {
    requireOwned(ownerToken, id);
    quizProgressDao.deleteByQuizSetId(id);
    questionDao.deleteByQuizSetId(id);
    quizSetDao.deleteById(id);
  }

  private QuizSet requireOwned(String ownerToken, Long id) {
    QuizSet set = quizSetDao.selectByIdAndOwnerToken(id, ownerToken);
    if (set == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz set not found");
    }
    return set;
  }

  private QuizSetDetail toDetail(QuizSet set, List<Question> questions, ProgressDto progress) {
    QuizSetDetail detail = new QuizSetDetail();
    detail.setId(set.getId());
    detail.setTitle(set.getTitle());
    detail.setQuestionCount(set.getQuestionCount());
    detail.setQuestions(questions.stream().map(QuizDtos::toQuestionDto).toList());
    detail.setProgress(progress);
    return detail;
  }

  private ProgressDto toProgressDto(int questionCount, QuizProgress progress) {
    if (progress == null) {
      return emptyProgress();
    }
    ProgressDto dto = new ProgressDto();
    List<AnswerItem> answers = readAnswers(progress.getAnswersJson());
    dto.setAnswers(answers);
    dto.setCurrentIndex(progress.getCurrentIndex());
    dto.setFinished(answers.size() >= questionCount && questionCount > 0);
    return dto;
  }

  private ProgressDto emptyProgress() {
    ProgressDto dto = new ProgressDto();
    dto.setCurrentIndex(0);
    dto.setAnswers(Collections.emptyList());
    dto.setFinished(false);
    return dto;
  }

  private List<AnswerItem> readAnswers(String json) {
    try {
      List<AnswerItem> list = objectMapper.readValue(json, new TypeReference<>() {
      });
      return list == null ? List.of() : list;
    } catch (Exception ex) {
      return List.of();
    }
  }

  private String writeAnswers(List<AnswerItem> answers) {
    try {
      return objectMapper.writeValueAsString(answers == null ? List.of() : answers);
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to serialize answers");
    }
  }
}
