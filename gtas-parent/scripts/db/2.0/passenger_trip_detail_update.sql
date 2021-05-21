ALTER TABLE passenger_trip_details
ADD  most_recent_message bigint(20) unsigned DEFAULT NULL;

alter table passenger_trip_details
    add constraint FKgo03ewsl8acsta6odlb0ymuvl
        foreign key (most_recent_message) references message (id);
