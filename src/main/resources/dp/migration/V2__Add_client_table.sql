CREATE TABLE clients (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  group_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  address TEXT,
  contact_person VARCHAR(255),
  email VARCHAR(255),
  phone VARCHAR(50),
  tax_information VARCHAR(255),
  notes TEXT,
  status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
 );