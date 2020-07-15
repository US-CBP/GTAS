create table country_group_join
(
    country_group_id bigint unsigned not null,
    country_id bigint unsigned not null,
    primary key (country_group_id, country_id),
    constraint FKbtnmgnb8eln31o9m7lrqmm1k8
        foreign key (country_id) references country_and_organization (id),
    constraint FKdciuwx2b961oycwewtrok314e
        foreign key (country_group_id) references country_group (id)
);

