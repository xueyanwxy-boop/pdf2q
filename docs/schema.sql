-- pdf2q 表结构（库名按实际账号权限，当前为 test001）

CREATE TABLE IF NOT EXISTS quiz_sets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_token VARCHAR(64) NOT NULL,
  title VARCHAR(200) NOT NULL,
  question_count INT NOT NULL,
  created_at DATETIME(6) NOT NULL,
  INDEX idx_quiz_sets_owner (owner_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS questions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  quiz_set_id BIGINT NOT NULL,
  seq INT NOT NULL,
  question_type VARCHAR(20) NOT NULL,
  stem TEXT NOT NULL,
  option_a VARCHAR(1000) NOT NULL,
  option_b VARCHAR(1000) NOT NULL,
  option_c VARCHAR(1000) NOT NULL,
  option_d VARCHAR(1000) NOT NULL,
  answer VARCHAR(32) NOT NULL,
  explanation TEXT NOT NULL,
  INDEX idx_questions_quiz_set (quiz_set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS quiz_progress (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_token VARCHAR(64) NOT NULL,
  quiz_set_id BIGINT NOT NULL,
  current_index INT NOT NULL,
  answers_json TEXT NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  UNIQUE KEY uk_owner_quiz (owner_token, quiz_set_id),
  INDEX idx_progress_quiz_set (quiz_set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 从「仅单选」旧表升级时执行：
-- ALTER TABLE questions ADD COLUMN question_type VARCHAR(20) NOT NULL DEFAULT 'single' AFTER seq;
-- ALTER TABLE questions MODIFY COLUMN answer VARCHAR(32) NOT NULL;
