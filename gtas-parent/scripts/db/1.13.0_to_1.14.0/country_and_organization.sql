create table country_and_organization
(
    id bigint unsigned auto_increment
        primary key,
    created_at datetime null,
    created_by varchar(20) null,
    updated_at datetime null,
    updated_by varchar(20) null,
    cao_organization varchar(255) null,
    cao_country_id bigint unsigned not null,
    constraint FKnfcyyos7y779p7epm0h5mst1l
        foreign key (cao_country_id) references country (id)
);

