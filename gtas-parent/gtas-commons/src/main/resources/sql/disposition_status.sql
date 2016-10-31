/*These 4 statuses are irremovable (though mutable) and must exist in some form in order to preserve the case management flow, with this order for ID purposes. */
insert into disposition_status(id, name, description) values(1, 'NEW', 'New Case');
insert into disposition_status(id, name, description) values(2, 'OPEN', 'Case is open');
insert into disposition_status(id, name, description) values(3, 'CLOSED', 'No action required');
insert into disposition_status(id, name, description) values(4, 'RE-OPEN', 'Re-opened case');
insert into disposition_status(id, name, description) values(5, 'PENDING CLOSURE','Case is pending closure');
