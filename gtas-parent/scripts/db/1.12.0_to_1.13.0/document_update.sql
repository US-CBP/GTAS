alter table document
    add flight_id bigint unsigned null;

alter table document
    add constraint FKco5r2xisfn4i4ifxsr5k470m0
        foreign key (flight_id) references flight (id);

alter table document
    add message_type varchar(255) null;

