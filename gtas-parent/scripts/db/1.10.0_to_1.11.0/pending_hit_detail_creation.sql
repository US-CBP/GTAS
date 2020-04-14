/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

create or replace table pending_hit_detail
(
    id bigint unsigned auto_increment
        primary key,
    created_at datetime null,
    created_by varchar(20) null,
    updated_at datetime null,
    updated_by varchar(20) null,
    description varchar(255) null,
    title varchar(255) not null,
    created_date datetime not null,
    flight bigint unsigned not null,
    hitEnum varchar(255) null,
    hm_id bigint unsigned not null,
    hit_type varchar(3) not null,
    passenger bigint unsigned null,
    percentage_match float null,
    cond_text text null,
    constraint FK4tmq550r3sjr8dy87555rt333
        foreign key (hm_id) references hit_maker (id),
    constraint FKja4iov1d6lhniq3dgpmdiwk4r
        foreign key (passenger) references passenger (id),
    constraint FKtk5y8ih7nudpcds2500n65x1k
        foreign key (flight) references flight (id)
);

