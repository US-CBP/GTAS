create table data_retention_status
(
    id bigint unsigned auto_increment
        primary key,
    created_at datetime null,
    created_by varchar(20) null,
    updated_at datetime null,
    updated_by varchar(20) null,
    drs_deleted_apis bit null,
    drs_deleted_PNR bit null,
    drs_has_apis_message bit null,
    drs_has_pnr_message bit null,
    drs_masked_apis bit null,
    drs_masked_pnr bit null,
    drs_passenger_id bigint unsigned null,
    constraint UK_djo75we51lcpluphf2p8f2x8h
        unique (drs_passenger_id),
    constraint FKi0v6rwut86udws8e0iamexx3r
        foreign key (drs_passenger_id) references passenger (id)
);

