alter table email
    add flight_id bigint unsigned null;

alter table email
    add constraint FK72parbwyahh343lijggs3pjnc
        foreign key (flight_id) references flight (id);

