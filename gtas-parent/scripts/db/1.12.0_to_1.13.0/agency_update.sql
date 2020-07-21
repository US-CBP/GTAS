alter table agency
    add flight_id bigint unsigned null;

alter table agency
    add constraint FK7poyj75iltb0pmxasiq64h7f0
        foreign key (flight_id) references flight (id);

