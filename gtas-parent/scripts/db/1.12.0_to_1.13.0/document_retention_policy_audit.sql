create table document_retention_policy_audit
(
    id bigint unsigned auto_increment
        primary key,
    created_at datetime null,
    created_by varchar(20) null,
    updated_at datetime null,
    updated_by varchar(20) null,
    description varchar(255) null,
    guuid binary(255) null,
    action varchar(255) null,
    document_id bigint unsigned not null,
    constraint FK43f5887epocyc9anqj0ofni5q
        foreign key (document_id) references document (id)
);

