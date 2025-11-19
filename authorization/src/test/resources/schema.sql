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
INSERT INTO public."user" (id, department, name, password, email, key, serial, state, config, role, pw_fail_cnt, department_code, department_detail) VALUES ('admin', 'admin', 'admin', null, null, null, null, 'ACTIVATE', null, 'A', 0, null, null);
INSERT INTO public."user" (id, department, name, password, email, key, serial, state, config, role, pw_fail_cnt, department_code, department_detail) VALUES ('inactive', 'dep1', 'inactive-manager', null, null, null, null, 'INACTIVATE', null, 'M', 0, null, null);
INSERT INTO public."user" (id, department, name, password, email, key, serial, state, config, role, pw_fail_cnt, department_code, department_detail) VALUES ('manager', 'dep1', 'active-manager', null, null, null, null, 'ACTIVATE', null, 'M', 0, null, null);
INSERT INTO public."user" (id, department, name, password, email, key, serial, state, config, role, pw_fail_cnt, department_code, department_detail) VALUES ('user', 'dep1', 'active-user', null, null, null, null, 'ACTIVATE', null, 'U', 0, null, null);
