create table captcha_codes (
    id integer not null,
    code varchar(255) not null,
    secret_code varchar(255) not null,
    time datetime(6) not null,
    primary key (id)
    );

create table global_settings (
    id integer not null,
    code varchar(255) not null,
    name varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
    );

create table post_comments (
    id integer not null,
    parent_id integer,
    text text not null,
    time datetime(6) not null,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
    );

create table post_votes (
    id integer not null,
    time datetime(6) not null,
    value tinyint not null,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
    );


create table posts (
    id integer not null,
    is_active tinyint not null,
    moderation_status varchar(255) not null,
    text text not null,
    time datetime(6) not null,
    title varchar(255) not null,
    view_count integer not null,
    moderator_id integer,
    user_id integer not null,
    primary key (id)
    );

create table tag2post (
    id integer not null,
    post_id integer not null,
    tag_id integer not null,
    primary key (id)
    );

create table tags (
    id integer not null,
    name varchar(255) not null,
    primary key (id)
    );

create table users (
    id integer not null,
    code varchar(255),
    email varchar(255) not null,
    is_moderator tinyint not null,
    name varchar(255) not null,
    password varchar(255) not null,
    photo varchar(255),
    reg_time datetime(6) not null,
    primary key (id)
    );

create table hibernate_sequence (
    next_val bigint
    );

alter table post_comments
    add constraint post_comments_fk
    foreign key (post_id) references posts (id);

alter table post_comments
    add constraint post_comments_user_fk
    foreign key (user_id) references users (id);

alter table post_comments
    add constraint post_comments_parent_post_fk
    foreign key (parent_id) references post_comments (id);

alter table post_votes
    add constraint post_votes_fk
    foreign key (post_id) references posts (id);

alter table post_votes
    add constraint post_votes_user_fk
    foreign key (user_id) references users (id);

alter table posts
    add constraint posts_moderator_fk
    foreign key (moderator_id) references users (id);

alter table posts
    add constraint posts_user_fk
    foreign key (user_id) references users (id);

alter table tag2post
    add constraint tag2post_post_fk
    foreign key (post_id) references posts (id);

alter table tag2post
    add constraint tag2post_tags_fk
    foreign key (tag_id) references tags (id);