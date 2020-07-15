create table message_document
(
    document_id bigint unsigned not null,
    message_id bigint unsigned not null,
    primary key (document_id, message_id),
    constraint FKaimi7d94mn9emqi5c9fwogkxx
        foreign key (document_id) references message (id),
    constraint FKsupcjvtbinkjxi9odksukx981
        foreign key (message_id) references document (id)
);

