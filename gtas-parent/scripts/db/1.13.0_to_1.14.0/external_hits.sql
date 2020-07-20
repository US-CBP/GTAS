create table external_hit
(
    description varchar(255) null,
    id bigint unsigned not null
        primary key,
    constraint FKg72tfmws9ktqbcf6qlem4u300
        foreign key (id) references hit_maker (id)
);

