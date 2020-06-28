alter table frequent_flyer
    add flight_id bigint unsigned null;

alter table frequent_flyer
    add constraint FK6tr76rmxypnw4dq88n24degcq
        foreign key (flight_id) references flight (id);

