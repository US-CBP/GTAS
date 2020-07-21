create table notes_data_retention_policy_audit
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
    note_data_retention_id bigint unsigned not null,
    constraint FK89oi68xqwkj10p83i0pw66fc1
        foreign key (note_data_retention_id) references notes (id)
);

