create table address_data_retention_policy_audit
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
    address_id bigint unsigned not null,
    constraint FKj5g79sclo9jbjwh21uy2bx3m9
        foreign key (address_id) references address (id)
);

