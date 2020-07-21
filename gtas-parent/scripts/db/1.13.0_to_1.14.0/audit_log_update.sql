alter table audit_log
    add DTYPE varchar(255) not null;

alter table audit_log
    add passenger_id bigint unsigned null;

alter table audit_log
    add flight_id bigint unsigned null;

alter table audit_log
    add constraint FK75a20e3nasquy22c2kafu6641
        foreign key (passenger_id) references passenger (id);

alter table audit_log
    add constraint FKggxjhroa9rhnwqwdp4jae1hgo
        foreign key (flight_id) references flight (id);

