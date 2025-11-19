CREATE SCHEMA IF NOT EXISTS panel;
CREATE SCHEMA IF NOT EXISTS public;

create table public.patient
(
    id                 varchar(255) not null primary key,
    name               varchar(64),
    code               varchar(64),
    sex                varchar(2),
    birth              date,
    customer_code      varchar(8),
    customer_name      varchar(64),
    customer_code2     varchar(8),
    customer_name2     varchar(64),
    mrn                varchar(64),
    source             varchar(8) default 'LIS'::character varying,
    customer_dept_name varchar(64),
    physician          varchar(64),
    ward               varchar(64),
    customer_reg_no    varchar(64),
    age                smallint
);

INSERT INTO public.patient (id, name, code, sex, birth, customer_code, customer_name, customer_code2, customer_name2, mrn, source, customer_dept_name, physician, ward, customer_reg_no, age) VALUES ('A', '홍길동', '230101', 'M', '2023-01-01', 'G010000', 'Gclabs', 'L000000', '무슨병원', '1', 'A-LIS', null, null, null, 'AA', 0);
INSERT INTO public.patient (id, name, code, sex, birth, customer_code, customer_name, customer_code2, customer_name2, mrn, source, customer_dept_name, physician, ward, customer_reg_no, age) VALUES ('E', '홍길순', '200101', 'M', '2020-01-01', 'G010000', 'Gclabs', 'L000000', '무슨병원', '2', 'A-LIS', '무슨과', '슈바이처', '어디어디', 'BB', 1);
INSERT INTO public.patient (id, name, code, sex, birth, customer_code, customer_name, customer_code2, customer_name2, mrn, source, customer_dept_name, physician, ward, customer_reg_no, age) VALUES ('C', '홍길동', '990101', 'M', '1999-01-01', 'G000015', '무슨의원', null, null, '4', 'A-LIS', null, null, null, null, 2);
INSERT INTO public.patient (id, name, code, sex, birth, customer_code, customer_name, customer_code2, customer_name2, mrn, source, customer_dept_name, physician, ward, customer_reg_no, age) VALUES ('D', 'X', null, null, null, 'G000000', '무슨병원', null, null, '5', 'A-LIS', null, null, null, null, null);
INSERT INTO public.patient (id, name, code, sex, birth, customer_code, customer_name, customer_code2, customer_name2, mrn, source, customer_dept_name, physician, ward, customer_reg_no, age) VALUES ('B', '홍길순', '200101', 'F', '2020-01-01', 'G010000', 'Gclabs', 'L000000', '무슨병원', '3', 'A-LIS', '무슨과', null, '어디어디', 'CC', 3);

create index idxeurqpgm3ksjogkoa2xwl8iy8y on public.patient (customer_code);
create index idxl5v5c2vxlioat0tqi269vca4m on public.patient (customer_name);
create index idx39g9o1bydx07j3fvnscysg6dg on public.patient (customer_name2);
create index idxf90vlwlmyhb5s0d6ygqck23on on public.patient (mrn);
create index idxov1mjrglxi907dck4u1sixtqc on public.patient (name);
create index idxbi7mu8gdyajbma8seubqq3nyr on public.patient (customer_code2);

create table public.sample
(
    id           bigint not null primary key,
    patient      varchar(255),
    sample_type  varchar(128),
    sample_type2 varchar(128),
    sample_type3 varchar(128),
    barcode      bigint,
    remark       varchar(128),
    id_lis       bigint
);

create index idx7vs23qg7ac0pa0vtu7et2xmry on public.sample (patient);
create index idx50n3bcm9hagnwh4u6q32svscx on public.sample (barcode desc);
create index idxthd5p98v4v0qihrix3tkgieku on public.sample (remark);
grant select on public.sample to panel;

INSERT INTO public.sample (id, patient, sample_type, sample_type2, sample_type3, barcode, remark, id_lis) VALUES (202212211715066, 'A', ' WB', null, null, 83901295185, '202212211295185', 202212211295185);
INSERT INTO public.sample (id, patient, sample_type, sample_type2, sample_type3, barcode, remark, id_lis) VALUES (202212281715194, 'B', ' WB', null, null, 83971595093, '202212281595093', 202212281595093);
INSERT INTO public.sample (id, patient, sample_type, sample_type2, sample_type3, barcode, remark, id_lis) VALUES (202212309223511, 'C', ' WB', null, null, 83999223511, null, null);
INSERT INTO public.sample (id, patient, sample_type, sample_type2, sample_type3, barcode, remark, id_lis) VALUES (202301049713201, 'D', ' WB', null, null, 84049713201, null, null);
INSERT INTO public.sample (id, patient, sample_type, sample_type2, sample_type3, barcode, remark, id_lis) VALUES (202303211715096, 'E', ' WB', null, null, 84801175205, '202303211175205', 202303211175205);

create table public.request
(
    sample              bigint                not null references public.sample,
    service             varchar(12)           not null,
    date_request        timestamp,
    date_start          timestamp,
    date_due            timestamp,
    date_sampling       timestamp,
    tat                 bigint,
    sync_time           timestamp,
    register            boolean,
    cancel              boolean,
    delete              boolean,
    labemp              integer,
    custom              json,
    info                varchar(255),
    type                varchar(16),
    accounting_category varchar(8),
    accounting_code     varchar(4),
    date_reception      timestamp,
    date_due_publish    timestamp,
    resample            boolean default false not null,
    resample_origin     text,
    customer_dept_name  varchar(64),
    physician           varchar(64),
    ward                varchar(64),
    customer_reg_no     varchar(64),
    primary key (sample, service)
);

create index idx7528da8lmcmeti2c5losl6lh2 on public.request (sync_time);
create index idxj1iwckig7759l71r5x1dtps01 on public.request (sample);
create index idxs8wy1cp7uxjqfbwi7sxdef13o on public.request (date_request, register);
create index idxq9fg9pa0vrtkd21n5ews02sim on public.request (service);
create index idxa1oeyvs816mv7ivdmxlm3offw on public.request (accounting_code);
create index idx7cvwmil3nux3qnp2k1u8vtdt4 on public.request (accounting_category);
create index idxrqbx4kwvvcesc6olaehswuxoa on public.request (date_due_publish);
create index idx8u88soeelg78o6a0p3soa3dqg on public.request (date_reception);
grant select on public.request to panel;

INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202212211715066, 'N073', '2022-12-21 00:00:00.000000', '2022-12-21 00:00:00.000000', '2023-01-10 00:00:00.000000', null, 17, '2023-02-21 18:38:13.302932', true, false, false, null, null, '22122085467', null, null, null, '2022-12-22 04:49:30.841000', '2023-01-10 00:00:00.000000', false, null, null, null, null, '38100215');
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202212281715194, 'Z137', '2022-12-28 00:00:00.000000', '2022-12-28 00:00:00.000000', '2023-01-11 00:00:00.000000', null, 12, '2023-02-28 20:07:45.881169', true, false, false, null, null, '162050MG0', null, null, null, '2022-12-29 07:32:42.409000', '2023-01-11 00:00:00.000000', false, null, 'OBGY', null, 'OBGY', '11101270');
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202212281715194, 'Z138', '2022-12-28 00:00:00.000000', '2022-12-28 00:00:00.000000', '2023-01-11 00:00:00.000000', null, 12, '2023-02-28 20:07:46.466276', true, false, false, null, null, '162050MG0', null, null, null, '2022-12-29 07:32:42.409000', '2023-01-11 00:00:00.000000', false, null, 'OBGY', null, 'OBGY', '11101270');
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202212309223511, 'N090', '2022-12-30 00:00:00.000000', '2022-12-30 00:00:00.000000', '2023-01-14 00:00:00.000000', '2022-12-30 00:00:00.000000', 12, '2023-02-28 20:59:23.573092', true, false, false, null, null, null, null, null, null, '2022-12-31 03:03:46.608000', '2023-01-13 00:00:00.000000', false, null, null, null, null, null);
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202301049713201, 'G2200201', '2023-01-04 00:00:00.000000', '2023-01-04 00:00:00.000000', '2023-01-17 00:00:00.000000', '2023-01-04 00:00:00.000000', 12, '2023-03-04 18:25:18.550104', true, false, false, null, null, 'JPI-547-201', null, null, null, '2023-01-05 04:27:14.840000', '2023-01-18 00:00:00.000000', false, null, null, null, null, null);
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202303211715096, 'N057', '2023-03-21 00:00:00.000000', '2023-03-21 00:00:00.000000', '2023-04-14 00:00:00.000000', '2023-03-14 00:00:00.000000', 21, '2023-05-21 15:36:30.683746', true, false, false, null, null, 'I87TD0FP0', null, null, null, '2023-03-22 04:18:53.508000', '2023-04-14 00:00:00.000000', false, null, '소아과(PD)', '한지윤', '71 / 7115', '34100032');
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202303211715096, 'N058', '2023-03-21 00:00:00.000000', '2023-03-21 00:00:00.000000', '2023-04-14 00:00:00.000000', '2023-03-14 00:00:00.000000', 21, '2023-05-21 15:36:31.253821', true, false, false, null, null, 'I87TD0FP0', null, null, null, '2023-03-22 04:18:53.508000', '2023-04-14 00:00:00.000000', false, null, '소아과(PD)', '한지윤', '71 / 7115', '34100032');
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202303211715096, 'N059', '2023-03-21 00:00:00.000000', '2023-03-21 00:00:00.000000', '2023-04-14 00:00:00.000000', '2023-03-14 00:00:00.000000', 21, '2023-05-21 15:36:31.827610', true, false, false, null, null, 'I87TD0FP0', null, null, null, '2023-03-22 04:18:53.508000', '2023-04-14 00:00:00.000000', false, null, '소아과(PD)', '한지윤', '71 / 7115', '34100032');
INSERT INTO public.request (sample, service, date_request, date_start, date_due, date_sampling, tat, sync_time, register, cancel, delete, labemp, custom, info, type, accounting_category, accounting_code, date_reception, date_due_publish, resample, resample_origin, customer_dept_name, physician, ward, customer_reg_no) VALUES (202303211715096, 'S104', '2023-03-21 00:00:00.000000', '2023-03-21 00:00:00.000000', '2023-04-14 00:00:00.000000', '2023-03-14 00:00:00.000000', 21, '2023-05-21 15:36:32.462013', true, false, false, null, null, 'I87TD0FP0', null, null, null, '2023-03-22 04:18:53.508000', '2023-04-14 00:00:00.000000', false, null, '소아과(PD)', '한지윤', '71 / 7115', '34100032');

CREATE TABLE IF NOT EXISTS panel.panel_type
(
    id              bigserial constraint panel_type_pk primary key,
    panel           varchar not null,
    type            varchar,
    service         varchar not null,
    effective_date  timestamp,
    expiration_date timestamp
);

alter table panel.panel_type owner to panel;
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (121, 'ND', 'ND', 'N110', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (120, 'ND', 'DEM', 'N080', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (123, 'ND', 'APP', 'S086', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (122, 'ND', 'ALS', 'N130', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (125, 'ST', 'HPL', 'N099', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (124, 'ST', 'ARH', 'N037', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (127, 'ST', 'SCN5A', 'S077', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (126, 'ST', 'STK', 'N100', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (117, 'CM', 'MYH7', 'S078', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (119, 'ND', 'ALZ', 'N079', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (118, 'CM', 'CM', 'N038', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (213, 'CANCER', 'MLH1L', 'Z141', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (212, 'CANCER', 'PTEN', 'S063', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (215, 'CANCER', 'APCL', 'Z964', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (214, 'CANCER', 'MSH2L', 'Z962', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (256, 'DGS', null, 'ON139', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (225, 'BRCA', 'BRCA', 'Z137', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (224, 'BRCA', 'cele-BXXX', 'G069', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (227, 'BRCA', 'TP53', 'X009', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (226, 'BRCA', 'BRCA', 'Z138', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (229, 'BRCA', 'CGS', 'N101', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (228, 'BRCA', 'W-BRCA', 'N001', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (231, 'WES', null, 'N176', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (230, 'BRCA', 'CGS', 'N111', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (249, 'DGS', null, 'N127', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (248, 'DGS', null, 'N113', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (251, 'DGS', null, 'N138', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (250, 'DGS', null, 'N137', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (253, 'DGS', null, 'N143', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (252, 'DGS', null, 'N139', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (255, 'DGS', null, 'ON138', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (254, 'DGS', null, 'ON137', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (247, 'DGS', null, 'ON127', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (137, 'PD', 'DYT', 'N084', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (136, 'DM', 'ABCC8', 'S090', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (139, 'PD', 'LRRK2', 'S085', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (138, 'PD', 'PKS', 'N078', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (141, 'PD', 'VPS13A', 'S084', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (140, 'PD', 'ATP7B', 'S065', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (143, 'KD', 'ALP', 'N096', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (142, 'KD', 'AHUS', 'N095', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (129, 'ST', 'GSH', 'N185', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (128, 'ST', 'GSH', 'N089', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (131, 'ST', 'GSH', 'N075', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (130, 'ST', 'GSH', 'N112', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (133, 'ST', 'GSSTK', 'N087', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (132, 'ST', 'GSHPL', 'N088', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (135, 'DM', 'MODY', 'N081', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (134, 'ST', 'LDLR', 'N121', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (153, 'SP', 'SPG', 'N047', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (152, 'SP', 'ATX', 'N046', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (155, 'SP', 'SPG11', 'S081', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (154, 'SP', 'CACNA1A', 'S083', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (157, 'SS', 'IGD', 'N067', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (156, 'SS', 'SS', 'N042', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (159, 'SS', 'NIPBL', 'S103', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (158, 'SS', 'CHD7', 'S091', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (145, 'KD', 'PK', 'N098', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (144, 'KD', 'NPHP', 'N097', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (147, 'EP', 'EPIL', 'N054', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (146, 'KD', 'PKD', 'S102', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (149, 'MD', 'MD', 'N044', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (148, 'EP', 'SCN1A', 'S080', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (151, 'MD', 'DYSF', 'S082', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (150, 'MD', 'MP', 'N045', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (169, 'RA', 'prePTPN11', 'S109', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (168, 'RA', 'PTPN11', 'S064', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (171, 'IE', 'IEM', 'N050', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (170, 'IE', 'LSD', 'N049', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (173, 'IE', 'GAA', 'S089', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (172, 'IE', 'PCCA', 'S088', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (175, 'PN', 'CMT', 'N055', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (174, 'IE', 'HPRT1', 'S067', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (245, 'DES', null, 'T019', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (244, 'DES', null, 'ON133', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (237, 'DES', null, 'N059', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (236, 'DES', null, 'N058', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (239, 'DES', null, 'N131', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (242, 'DES', null, 'ON131', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (234, 'DES', null, 'N003', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (241, 'DES', null, 'ON003', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (240, 'DES', null, 'N132', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (243, 'DES', null, 'ON132', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (238, 'DES', null, 'N085', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (233, 'DES', null, 'S104', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (232, 'DES', null, 'N057', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (235, 'DES', null, 'N133', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (246, 'DES', null, 'T022', '2022-10-12 10:35:26.000000', '2022-10-18 16:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (270, 'GMD', null, 'S104', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (271, 'GMD', null, 'N057', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (272, 'GMD', null, 'N133', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (273, 'GMD', null, 'N003', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (274, 'GMD', null, 'N059', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (275, 'GMD', null, 'N058', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (276, 'GMD', null, 'N131', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (277, 'GMD', null, 'N085', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (278, 'GMD', null, 'ON003', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (279, 'GMD', null, 'N132', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (280, 'GMD', null, 'ON132', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (281, 'GMD', null, 'ON131', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (282, 'GMD', null, 'T019', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (283, 'GMD', null, 'ON133', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (287, 'GMD', 'SMCP', 'N187', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (288, 'GMD', 'MOC', 'N188', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (289, 'GMD', 'PRF', 'N189', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (290, 'GMD', 'CAKUT', 'N190', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (291, 'GMD', 'HHC', 'N191', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (292, 'GMD', 'PDD', 'N192', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (285, 'G2200101-CANCER', null, 'G2200101', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (286, 'G2200201-CANCER', null, 'G2200201', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (293, 'GMD', 'OCA', 'N193', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (294, 'GMD', 'OA', 'N194', '2023-01-31 00:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (295, 'WES', 'ASD', 'R2300101', '2022-12-22 00:00:00.000000', '2024-12-31 00:00:00.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (299, 'ST', 'GSH', 'ON089', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (284, 'GMD', null, 'T022', '2022-10-18 16:00:00.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (296, 'IE', 'LSD', 'R2200301', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (297, 'IE', 'LSD', 'R2200302', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (298, 'WES', 'ICON', 'R2200201', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (201, 'GMD', 'IBMFS', 'N183', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (200, 'GMD', 'SCN', 'N182', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (203, 'CANCER', 'GBRCA', 'S096', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (202, 'GMD', 'HLH', 'N184', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (205, 'CANCER', 'CH', 'N002', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (204, 'CANCER', 'GBRCA', 'S097', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (207, 'CANCER', 'GSC', 'N090', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (206, 'CANCER', null, 'N074', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (193, 'AN', 'F9', 'S032', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (192, 'AN', 'F8', 'S030', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (195, 'AN', 'F7', 'S053', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (194, 'AN', 'VWF', 'S034', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (197, 'AN', 'F12', 'S055', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (196, 'AN', 'F11', 'S054', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (199, 'GMD', 'ITP', 'N181', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (198, 'GMD', 'CMG', 'N180', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (217, 'CANCER', 'can', 'N076', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (216, 'CANCER', 'canP', 'N040', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (219, 'CANCER', 'canP', 'G2200101', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (218, 'CANCER', 'GSC', 'N109', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (221, 'OS-CANCER', 'Cancer', 'ON040', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (220, 'CANCER', 'canP', 'G2200201', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (223, 'BRCA', 'cele-BXXX', 'G068', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (222, 'OS-CANCER', 'Women', 'ON001', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (209, 'CANCER', 'CC', 'N022', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (208, 'CANCER', 'GSC', 'ON090', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (211, 'CANCER', 'MEN1', 'S061', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (210, 'CANCER', 'NF2', 'S062', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (161, 'CT', 'DSD', 'N048', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (160, 'CT', 'CTD', 'N043', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (163, 'CT', 'FBN1', 'S093', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (162, 'CT', 'CH', 'N052', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (165, 'RA', 'CHOL', 'N123', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (164, 'CT', 'DUOX2', 'S092', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (167, 'RA', 'RAS', 'N051', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (166, 'RA', 'DER', 'N039', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (185, 'EY', 'EYE', 'N072', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (184, 'RP', 'RP', 'N071', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (187, 'CD', 'NET', 'N119', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (186, 'MC', 'MCPH', 'N106', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (189, 'MT', 'MT', 'N122', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (188, 'CD', 'PCD', 'N108', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (191, 'AN', 'ANE', 'N148', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (190, 'AN', 'COAG', 'N053', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (177, 'IM', 'PID', 'N062', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (176, 'IM', 'IBD', 'N149', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (179, 'SD', 'COL2A1', 'S087', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (178, 'SD', 'SD', 'N041', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (181, 'AU', 'AUT', 'N107', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (180, 'SD', 'preFGFR3', 'S110', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (183, 'HL', 'SLC26A4', 'N027', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (182, 'HL', 'HL', 'N073', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (258, 'WES', null, 'T001', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (261, 'WES', null, 'N136', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (260, 'WES', null, 'N135', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (263, 'WES', null, 'ON134', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (262, 'WES', null, 'N142', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (259, 'WES', null, 'N134', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (257, 'DGS', null, 'ON143', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (268, 'WES', null, 'T023', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (269, 'WES', null, 'T035', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (266, 'WES', null, 'ON142', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (267, 'WES', null, 'OT001', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (264, 'WES', null, 'ON135', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');
INSERT INTO panel.panel_type (id, panel, type, service, effective_date, expiration_date) VALUES (265, 'WES', null, 'ON136', '2022-10-12 10:35:26.000000', '9999-12-31 23:59:59.000000');


CREATE TABLE IF NOT EXISTS panel.analysis
(
    batch            varchar(64) not null,
    row              integer     not null,
    request          varchar(24) not null,
    create_time      timestamp   not null,
    last_modify_at   timestamp,
    sample           bigint,
    service          varchar(12),
    serial           varchar(64),
    panel            varchar(16),
    file             varchar(255),
    sheet            uuid        not null,
    value            jsonb,
    result           varchar(64),
    last_modify_user varchar(64),
    activity_state   varchar(16) default 'ACTIVE'::character varying,
    primary key (batch, row, sheet, request)
);
alter table panel.analysis owner to panel;
create index idx5qllla1qjrmatnuatgnyljp25 on panel.analysis (activity_state);
create index idx8tg4e7b2epsk72c0r58jmpovs on panel.analysis (sample);
create index idxbfucmu7ybd9g55kukf2plicdo on panel.analysis (sheet);
create index idxji37syo5ekgv5i0u761x6ety3 on panel.analysis (panel);
create index idxauvasd2caulniif1ghmds72a0 on panel.analysis (file);
create index idxmlslx5kpcy7ej9vclinnrnf6n on panel.analysis (sample, service);

INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23HL001', 2, '202212211715066:N073', '2023-01-04 21:11:50.545998', null, 202212211715066, 'N073', '23HL001_20221221-171-5066_HL-02', 'HL', null, '01db4ff4-9689-47b0-b0d9-1659788912c5', '{"0x(%)": "0.0", "5x(%)": "100.0", "10x(%)": "100.0", "20x(%)": "100.0", "30x(%)": "100.0", "50x(%)": "99.9", "depth(x)": "571.92", "sample name": "23HL001_20221221-171-5066_HL-02_I"}', null, null, 'ACTIVE');
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23BRCA001', 4, '202212281715194:Z137', '2023-01-04 21:10:42.608230', null, 202212281715194, 'Z137', '23BRCA001_20221228-171-5194_23BRCA003-04_I', 'BRCA', null, 'a118c948-74b9-4929-abf9-35d8a50e6f37', '{"0x(%)": "0.0", "5x(%)": "100.0", "10x(%)": "100.0", "20x(%)": "100.0", "30x(%)": "100.0", "50x(%)": "100.0", "depth(x)": "721.34", "sample name": "23BRCA001_20221228-171-5194_23BRCA003-04_I"}', null, null, 'ACTIVE');
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23BRCA001', 4, '202212281715194:Z138', '2023-01-04 21:10:42.610056', null, 202212281715194, 'Z138', '23BRCA001_20221228-171-5194_23BRCA003-04_I', 'BRCA', null, 'a118c948-74b9-4929-abf9-35d8a50e6f37', '{"0x(%)": "0.0", "5x(%)": "100.0", "10x(%)": "100.0", "20x(%)": "100.0", "30x(%)": "100.0", "50x(%)": "100.0", "depth(x)": "721.34", "sample name": "23BRCA001_20221228-171-5194_23BRCA003-04_I"}', null, null, 'ACTIVE');
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23Cancer002', 17, '202212309223511:N090', '2023-01-04 21:11:08.987586', null, 202212309223511, 'N090', '23Cancer002_20221230-922-3511_GSC-17_I', 'GSC', null, '025394fd-39ec-4cdf-9398-f62959aacdf5', '{"0x(%)": "0.0", "5x(%)": "100.0", "10x(%)": "100.0", "20x(%)": "100.0", "30x(%)": "100.0", "50x(%)": "100.0", "depth(x)": "472.81", "sample name": "23Cancer002_20221230-922-3511_GSC-17_I"}', null, null, 'ACTIVE');
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23G2200201-Cancer005', 1, '202301049713201:G2200201', '2023-01-11 10:52:21.994616', null, 202301049713201, 'G2200201', '23G2200201-Cancer005_20230104-971-3201_canP-01_I', 'canP', null, '025394fd-39ec-4cdf-9398-f62959aacdf5', '{"0x(%)": "0.0", "5x(%)": "100.0", "10x(%)": "100.0", "20x(%)": "100.0", "30x(%)": "100.0", "50x(%)": "100.0", "depth(x)": "345.04", "sample name": "23G2200201-Cancer005_20230104-971-3201_canP-01_I"}', null, null, 'ACTIVE');
/*
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23GMD011', 4, '202303211715096:N058', '2023-04-09 01:21:25.740459', null, 202303211715096, 'N058', '23GMD011_20230321-171-5096_DES-4', 'GMD', null, '497ff234-fe85-4328-9d1e-55debae35193', '{}', null, null, 'ACTIVE');
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23GMD011', 4, '202303211715096:N057', '2023-04-09 01:21:25.739144', null, 202303211715096, 'N057', '23GMD011_20230321-171-5096_DES-4', 'GMD', null, '01db4ff4-9689-47b0-b0d9-1659788912c5', '{"0x(%)": "0.0", "5x(%)": "99.9", "10x(%)": "99.8", "20x(%)": "99.6", "30x(%)": "99.4", "50x(%)": "99.0", "sample": "23GMD011_20230321-171-5096_DES-4_M", "depth(x)": "457.58", "mt_variants": "77", "sample name": "23GMD011_20230321-171-5096_DES-4_M", "all_variants": "16509", "filter_variants": "354"}', null, null, 'ACTIVE');
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23GMD011', 4, '202303211715096:N059', '2023-04-09 01:21:25.741010', null, 202303211715096, 'N059', '23GMD011_20230321-171-5096_DES-4', 'GMD', null, '01db4ff4-9689-47b0-b0d9-1659788912c5', '{"0x(%)": "0.0", "5x(%)": "99.9", "10x(%)": "99.8", "20x(%)": "99.6", "30x(%)": "99.4", "50x(%)": "99.0", "sample": "23GMD011_20230321-171-5096_DES-4_M", "depth(x)": "457.58", "mt_variants": "77", "sample name": "23GMD011_20230321-171-5096_DES-4_M", "all_variants": "16509", "filter_variants": "354"}', null, null, 'ACTIVE');
INSERT INTO panel.analysis (batch, row, request, create_time, last_modify_at, sample, service, serial, panel, file, sheet, value, result, last_modify_user, activity_state) VALUES ('23GMD011', 4, '202303211715096:S104', '2023-04-09 01:21:25.741451', null, 202303211715096, 'S104', '23GMD011_20230321-171-5096_DES-4', 'GMD', null, '01db4ff4-9689-47b0-b0d9-1659788912c5', '{"0x(%)": "0.0", "5x(%)": "99.9", "10x(%)": "99.8", "20x(%)": "99.6", "30x(%)": "99.4", "50x(%)": "99.0", "sample": "23GMD011_20230321-171-5096_DES-4_M", "depth(x)": "457.58", "mt_variants": "77", "sample name": "23GMD011_20230321-171-5096_DES-4_M", "all_variants": "16509", "filter_variants": "354"}', null, null, 'ACTIVE');
 */

