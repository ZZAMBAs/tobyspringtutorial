CREATE TABLE USERS(
    id varchar(10) PRIMARY KEY,
    username varchar(20) NOT NULL,
    password varchar(20) NOT NULL,
    level tinyint NOT NULL,
    login int NOT NULL,
    recommend int NOT NULL,
    email varchar(50)
);