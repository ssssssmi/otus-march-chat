CREATE TABLE user_data (
	id serial4 NOT NULL,
	nickname varchar NOT NULL,
	login varchar NOT NULL,
	"password" varchar NOT NULL,
	"role" varchar NOT NULL,
	CONSTRAINT user_data_pk PRIMARY KEY (id)
);

INSERT INTO user_data (nickname,login,"password","role") VALUES
	 ('user1','name1','123','ADMIN'),
	 ('user2','name2','123','USER'),
	 ('user3','name3','123','USER'),
	 ('user4','123','name4','ADMIN'),
	 ('user4','name4','123','ADMIN'),
	 ('1','3','2','ADMIN');