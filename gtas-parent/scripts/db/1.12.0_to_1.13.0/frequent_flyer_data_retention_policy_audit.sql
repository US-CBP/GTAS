create table frequent_flyer_data_retention_policy_audit
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
    frequent_flyer_id bigint unsigned not null,
    constraint FK4hv6c29uqb34ehmbn74jtl4pm
        foreign key (frequent_flyer_id) references frequent_flyer (id)
);

