alter table wl_item 
add column `ITM_WL_KB` bigint(20) unsigned DEFAULT NULL,

alter table wl_item
    add constraint FKmln6t3ji97rv20w30w3idufnt
        foreign key (ITM_WL_KB) references watch_list (id);
        
        
alter table udr_rule 
	add column `ITM_UDR_REF` bigint(20) unsigned DEFAULT NULL;
	
	alter table wl_item add
  CONSTRAINT `FK2hyma0906w1y1dsiily8sh63x` FOREIGN KEY (`ITM_WL_KB`) REFERENCES `knowledge_base` (`id`);

  
  ALTER TABLE knowledge_base
DROP COLUMN KB_BLOB;

ALTER TABLE knowledge_base
DROP COLUMN RL_BLOB;
