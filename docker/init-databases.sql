-- Création de toutes les bases de données
CREATE DATABASE auth_db;
CREATE DATABASE customer_db;
CREATE DATABASE account_db;
CREATE DATABASE transaction_db;
CREATE DATABASE loan_db;
CREATE DATABASE operator_db;

-- Droits
GRANT ALL PRIVILEGES ON DATABASE auth_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE customer_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE account_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE transaction_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE loan_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE operator_db TO postgres;
