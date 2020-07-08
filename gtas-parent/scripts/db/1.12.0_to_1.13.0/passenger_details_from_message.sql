create table passenger_details_from_message
(
    id bigint unsigned auto_increment
        primary key,
    created_at datetime null,
    created_by varchar(20) null,
    updated_at datetime null,
    updated_by varchar(20) null,
    pdfm_age int null,
    pdfm_deleted bit not null,
    dob date null,
    pdfm_first_name varchar(255) null,
    pdfm_gender varchar(2) null,
    pdfm_last_name varchar(255) null,
    pdfm_message_id bigint unsigned null,
    pdfm_message_type varchar(255) null,
    pdfm_middle_name varchar(255) null,
    pdfm_nationality varchar(255) null,
    pdfm_passenger_id bigint unsigned null,
    pdfm_passenger_type varchar(3) not null,
    pdfm_residency_country varchar(255) null,
    pdfm_suffix varchar(255) null,
    pdfm_title varchar(255) null,
    constraint FKdttvgtawgyiiiymi8bmjcjoak
        foreign key (pdfm_passenger_id) references passenger (id),
    constraint FKep2lmqfag1fgiqrocom9l9ujc
        foreign key (pdfm_message_id) references message (id)
);

