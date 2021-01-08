create table saved_segment
(
    id bigint unsigned auto_increment
        primary key,
    created_at datetime null,
    created_by varchar(20) null,
    updated_at datetime null,
    updated_by varchar(20) null,
    rawSegment varchar(255) null,
    regex varchar(255) null,
    segmentName varchar(255) null,
    pnr_id bigint unsigned null,
    constraint FKc3mexjuclbkibbah2ddrvon06
        foreign key (pnr_id) references pnr (id)
);

