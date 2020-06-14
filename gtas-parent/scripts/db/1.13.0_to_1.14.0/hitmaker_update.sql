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
    add
        CONSTRAINT `FKd9w8eunfyygsxhr92af4ok3i1` FOREIGN KEY (`hm_cg_id`) REFERENCES `country_group` (`id`);



