-- Invoices --

 CREATE TABLE invoices (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id BIGINT NOT NULL,
  invoice_number VARCHAR(50) NOT NULL,
  issue_date DATE NOT NULL,
  due_date DATE NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  tax_amount DECIMAL(15,2) DEFAULT 0.00,
  total_amount DECIMAL(15,2) NOT NULL,
  status ENUM('DRAFT', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED') DEFAULT 'DRAFT',
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
 );

 -- Invoice Items --

 CREATE TABLE invoice_items (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   invoice_id BIGINT NOT NULL,
   description TEXT NOT NULL,
   quantity DECIMAL(10,2) NOT NULL,
   unit_price DECIMAL(15,2) NOT NULL,
   amount DECIMAL(15,2) NOT NULL,
   tax_percentage DECIMAL(5,2) DEFAULT 0.00,
   tax_amount DECIMAL(15,2) DEFAULT 0.00,
   total_amount DECIMAL(15,2) NOT NULL,
   project_id BIGINT,
   task_id BIGINT,
   FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
   FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
   FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
  );