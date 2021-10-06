alter table wl_item 
	add column `KEY_STRING` varchar(255) DEFAULT NULL;
         
create index wl_key_index on wl_item (KEY_STRING);
