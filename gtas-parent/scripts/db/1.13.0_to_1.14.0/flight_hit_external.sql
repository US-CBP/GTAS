create table flight_hit_external
(
    fhe_flight_id bigint unsigned not null
        primary key,
    fhe_hit_count int null,
    constraint FK5v2l4en9dj2dxb222kgvnjb06
        foreign key (fhe_flight_id) references flight (id)
);

