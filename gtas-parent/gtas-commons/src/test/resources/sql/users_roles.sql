delete from GTAS_USERS;
delete from GTAS_ROLES;
insert into GTAS_ROLES values (1,'admin');
insert into GTAS_USERS values ('jpjones','password', 'JP', 'Jones',1);
insert into GTAS_USERS values ('svempati','password', 'Srinivas', 'Vempati',1);
commit;
