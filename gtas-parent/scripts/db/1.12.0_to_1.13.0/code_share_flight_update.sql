alter table code_share_flight
    add flight_id bigint unsigned null;

alter table code_share_flight
    add constraint FKtcnw4np8535s75wdtur0pswty
        foreign key (flight_id) references flight (id);

