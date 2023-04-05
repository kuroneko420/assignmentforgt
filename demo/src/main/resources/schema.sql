CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  salary DECIMAL(10, 2) NOT NULL
);

DELETE FROM users;

INSERT INTO users (name, salary) VALUES ('Alice', 2700.00);
INSERT INTO users (name, salary) VALUES ('Bob', 3200.00);
INSERT INTO users (name, salary) VALUES ('Carol', 2800.00);
INSERT INTO users (name, salary) VALUES ('David', 3600.00);
INSERT INTO users (name, salary) VALUES ('Eva', 2900.00);
INSERT INTO users (name, salary) VALUES ('Frank', 3300.00);
INSERT INTO users (name, salary) VALUES ('Grace', 4000.00);
INSERT INTO users (name, salary) VALUES ('Hannah', 3100.00);
INSERT INTO users (name, salary) VALUES ('Ivan', 3000.00);
INSERT INTO users (name, salary) VALUES ('Jack', 2500.00);
INSERT INTO users (name, salary) VALUES ('Kate', 3500.00);
INSERT INTO users (name, salary) VALUES ('Leo', 3800.00);
INSERT INTO users (name, salary) VALUES ('Mona', 2600.00);
INSERT INTO users (name, salary) VALUES ('Nina', 3400.00);
INSERT INTO users (name, salary) VALUES ('Oliver', 3700.00);