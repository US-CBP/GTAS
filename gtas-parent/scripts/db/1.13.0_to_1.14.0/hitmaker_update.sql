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
        hm_hit_category bigint unsigned DEFAULT NULL;

alter table hit_maker
    add
        CONSTRAINT `FKd9w8eunfyygsxhr92af4ok3i1` FOREIGN KEY (`hm_cg_id`) REFERENCES `country_group` (`id`);

alter table hit_maker
    add constraint FKk7fb7xur9um2p3dcttqipjj72
        foreign key (hm_io_id) references intra_organization_group (id);




