create table passenger_details_retention_policy_audit
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
    pdrpa_doc_id bigint unsigned not null,
    constraint FKblpc6ytdus2iig2e83doxougp
        foreign key (pdrpa_doc_id) references passenger (id)
);

