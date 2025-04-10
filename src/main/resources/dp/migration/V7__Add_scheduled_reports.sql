CREATE TABLE scheduled_reports (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  report_type VARCHAR(50) NOT NULL,
  parameters JSON NOT NULL, -- Stores report configuration
  recipients TEXT NOT NULL, -- Comma-separated email addresses
  schedule_type ENUM('DAILY', 'WEEKLY', 'MONTHLY') NOT NULL,
  schedule_day INT, -- Day of week (1-7) or day of month (1-31)
  schedule_time TIME NOT NULL,
  last_run_at TIMESTAMP NULL,
  status ENUM('ACTIVE', 'PAUSED') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
 );