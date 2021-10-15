
    alter table message_status 
        add column created_at datetime;

    alter table message_status 
        add column updated_at datetime;
        
create index ms_updated_at_index on message_status (updated_at);
