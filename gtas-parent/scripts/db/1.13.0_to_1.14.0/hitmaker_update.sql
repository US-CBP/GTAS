alter table hit_maker
    add
    created_at datetime null;

alter table hit_maker
    add
        created_by varchar(20) null;

alter table hit_maker
    add
        updated_at datetime null;

alter table hit_maker
    add
        updated_by varchar(20) null;

alter table hit_maker
    add
        `hm_cg_id` bigint(20) unsigned DEFAULT NULL;


alter table hit_maker
    add constraint FKd9w8eunfyygsxhr92af4ok3i1
        foreign key (hm_cg_id) references country_group (id);




