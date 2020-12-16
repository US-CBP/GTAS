SET NAMES utf8mb4;
insert into credit_card_type(code, description) values ("AX", "American Express");
insert into credit_card_type(code, description) values ("CA", "Mastercard");
insert into credit_card_type(code, description) values ("DC", "Diners Club Int'l");
insert into credit_card_type(code, description) values ("DS", "Discover Card");
insert into credit_card_type(code, description) values ("JC", "JCB");
insert into credit_card_type(code, description) values ("TP", "UATP");
insert into credit_card_type(code, description) values ("UP", "UnionPay");
insert into credit_card_type(code, description) values ("VI", "Visa");

 -- POPULATE CREDITCARDTYPERESTORE TABLE AS EXACT DUPLICATE --
insert into credit_card_type_restore(id, code, description) select id, code, description from credit_card_type;

-- SET ORIGINID ON THE INITIAL RECORDS IN THE CREDITCARDTYPE TABLE ONLY
update credit_card_type set originId = id;
