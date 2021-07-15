create table if not exists users (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(254),
	username character varying(254) NOT NULL,
	password character varying(155) NOT NULL,
	phone character varying(140),
	genre_id bigint,
	uuid character varying(155),
	date_created bigint default 0
);

create table if not exists roles (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(155) NOT NULL UNIQUE
);

create table if not exists user_permissions(
	user_id bigint REFERENCES users(id),
	permission character varying(55)
);

create table if not exists user_roles(
	role_id bigint NOT NULL REFERENCES roles(id),
	user_id bigint NOT NULL REFERENCES users(id)
);

create table if not exists genres (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(251) NOT NULL,
	funk character varying (254) NOT NULL,
	constraint genre_name unique(name)
);

create table if not exists songs (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	title character varying(254) NOT NULL,
	uri text NOT NULL,
	duration bigint,
	release_date bigint,
	user_id bigint NOT NULL REFERENCES users(id),
	genre_id bigint REFERENCES genres(id)
);

create table if not exists song_likes (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	date_liked bigint,
	user_id bigint REFERENCES users(id),
	song_id bigint NOT NULL REFERENCES songs(id)
);

create table if not exists song_plays (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	date_played bigint,
	user_id bigint REFERENCES users(id),
	song_id bigint NOT NULL REFERENCES songs(id)
);

create table if not exists song_downloads (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	date_downloaded bigint,
	user_id bigint REFERENCES users(id),
	song_id bigint NOT NULL REFERENCES songs(id)
);

