BEGIN TRANSACTION;

DROP TABLE IF EXISTS transfer;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
	user_id serial NOT NULL,
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50),
	role varchar(20),
	CONSTRAINT pk_users PRIMARY KEY (user_id),
	CONSTRAINT uq_username UNIQUE (username)
);

CREATE TABLE account
(
    account_id serial NOT NULL,
    user_id int NOT NULL,
    balance numeric(12,2) NOT NULL,
    CONSTRAINT pk_account_id PRIMARY KEY (account_id),
	CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES users(user_id)
);

CREATE TABLE transfer
(
    transfer_id serial NOT NULL,
    transfer_type character varying(10) NOT NULL,
    transfer_status character varying(10) NOT NULL,
    user_from int NOT NULL,
    user_to int NOT NULL,
    amount numeric(12,2) NOT NULL,
    CONSTRAINT pk_transfer_id PRIMARY KEY (transfer_id),
	CONSTRAINT fk_user_from FOREIGN KEY(user_from) REFERENCES users(user_id),
	CONSTRAINT fk_user_to FOREIGN KEY(user_to) REFERENCES users(user_id)
);

INSERT INTO users (username,password_hash,role) VALUES ('user1','user1','ROLE_USER'); -- 1
INSERT INTO users (username,password_hash,role) VALUES ('user2','user2','ROLE_USER'); -- 2
INSERT INTO users (username,password_hash,role) VALUES ('user3','user3','ROLE_USER'); -- 3

INSERT INTO account (user_id, balance) VALUES (1, 1000.00); -- account_id 1
INSERT INTO account (user_id, balance) VALUES (2, 1000.00); -- account_id 2
INSERT INTO account (user_id, balance) VALUES (3, 1000.00); -- account_id 3

INSERT INTO transfer (transfer_type, transfer_status, user_from, user_to, amount)
VALUES ('Request', 'Rejected', 1, 2, 200.00);

INSERT INTO transfer (transfer_type, transfer_status, user_from, user_to, amount)
VALUES ('Send', 'Approved', 2, 1, 50.00);

INSERT INTO transfer (transfer_type, transfer_status, user_from, user_to, amount)
VALUES ('Request', 'Pending', 1, 3, 100.00);

INSERT INTO transfer (transfer_type, transfer_status, user_from, user_to, amount)
VALUES ('Send', 'Approved', 3, 2, 25.50);

INSERT INTO transfer (transfer_type, transfer_status, user_from, user_to, amount)
VALUES ('Request', 'Pending', 2, 3, 150.00);


COMMIT TRANSACTION;
