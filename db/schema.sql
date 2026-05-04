CREATE TABLE IF NOT EXISTS forms (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(500),
  display_order INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS form_fields (
  id BIGINT NOT NULL AUTO_INCREMENT,
  form_id BIGINT NOT NULL,
  label VARCHAR(255) NOT NULL,
  type VARCHAR(20) NOT NULL,
  display_order INT NOT NULL DEFAULT 0,
  required BIT NOT NULL,
  fields_order INT,
  PRIMARY KEY (id),
  CONSTRAINT fk_form_fields_form
    FOREIGN KEY (form_id) REFERENCES forms (id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS form_field_options (
  id BIGINT NOT NULL AUTO_INCREMENT,
  field_id BIGINT NOT NULL,
  label VARCHAR(255) NOT NULL,
  value VARCHAR(255) NOT NULL,
  display_order INT NOT NULL DEFAULT 0,
  options_order INT,
  PRIMARY KEY (id),
  CONSTRAINT fk_field_options_field
    FOREIGN KEY (field_id) REFERENCES form_fields (id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS form_submissions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  form_id BIGINT NOT NULL,
  submitted_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_form_submissions_form
    FOREIGN KEY (form_id) REFERENCES forms (id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS submission_values (
  id BIGINT NOT NULL AUTO_INCREMENT,
  submission_id BIGINT NOT NULL,
  field_id BIGINT NOT NULL,
  value VARCHAR(2000) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_submission_values_submission
    FOREIGN KEY (submission_id) REFERENCES form_submissions (id)
    ON DELETE CASCADE,
  CONSTRAINT fk_submission_values_field
    FOREIGN KEY (field_id) REFERENCES form_fields (id)
    ON DELETE CASCADE
) ENGINE=InnoDB;
