SET NAMES utf8mb4;
insert into credit_card_type(code, description, updated_at) values ("AX", "American Express", NOW());
insert into credit_card_type(code, description, updated_at) values ("CA", "Mastercard", NOW());
insert into credit_card_type(code, description, updated_at) values ("DC", "Diners Club Int'l", NOW());
insert into credit_card_type(code, description, updated_at) values ("DS", "Discover Card", NOW());
insert into credit_card_type(code, description, updated_at) values ("JC", "JCB", NOW());
insert into credit_card_type(code, description, updated_at) values ("TP", "UATP", NOW());
insert into credit_card_type(code, description, updated_at) values ("UP", "UnionPay", NOW());
insert into credit_card_type(code, description, updated_at) values ("VI", "Visa", NOW());

 -- POPULATE CREDITCARDTYPERESTORE TABLE AS EXACT DUPLICATE --
insert into credit_card_type_restore(id, code, description) select id, code, description from credit_card_type;

-- SET ORIGINID ON THE INITIAL RECORDS IN THE CREDITCARDTYPE TABLE ONLY
update credit_card_type set originId = id;
