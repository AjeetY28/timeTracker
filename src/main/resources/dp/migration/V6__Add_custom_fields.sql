-- Custom Fields --

CREATE TABLE custom_fields (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  group_id BIGINT NOT NULL,
  entity_type ENUM('TIME_ENTRY', 'PROJECT', 'TASK', 'USER', 'CLIENT', 'INVOICE') NOT NULL,
  name VARCHAR(255) NOT NULL,
  label VARCHAR(255) NOT NULL,
  type ENUM('TEXT', 'NUMBER', 'DATE', 'DROPDOWN', 'CHECKBOX') NOT NULL,
  required BOOLEAN DEFAULT FALSE,
  options TEXT, -- For dropdown fields, stores JSON array of options
  default_value TEXT,
  display_order INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
  UNIQUE KEY unique_field_name (group_id, entity_type, name)
 );

 -- Custom Field Values --

CREATE TABLE custom_field_values (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   custom_field_id BIGINT NOT NULL,
   entity_id BIGINT NOT NULL,
   value TEXT,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (custom_field_id) REFERENCES custom_fields(id) ON DELETE CASCADE,
   UNIQUE KEY unique_field_value (custom_field_id, entity_id)
  );