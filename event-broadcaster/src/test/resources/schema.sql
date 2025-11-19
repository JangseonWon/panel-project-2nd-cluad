create table public."user"
(
    id                varchar(64)                 not null   primary key,
    department        varchar(64),
    name              varchar(64),
    password          varchar(64),
    email             varchar(128),
    key               uuid,
    serial            varchar(8),
    state             varchar(10),
    config            jsonb,
    role              "char"  default 'U'::"char" not null,
    pw_fail_cnt       integer default 0           not null,
    department_code   varchar(16),
    department_detail varchar(64)
);