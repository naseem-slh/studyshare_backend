-- Bei Änderung muss der Container gelöscht werden
-- Create a Departments table to store department information
/*CREATE TABLE Departments (
  department_id SERIAL PRIMARY KEY,
  department_name VARCHAR(50) NOT NULL
);*/

-- Create an Employees table to store employee information
/*CREATE TABLE Employees (
  employee_id SERIAL PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(100) UNIQUE,
  hire_date DATE,
  department_id INT,
  FOREIGN KEY (department_id) REFERENCES Departments(department_id)
);
*/

-- DROP TABLE Employees;
-- DROP TABLE Departments;

