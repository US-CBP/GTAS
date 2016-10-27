/*These 4 statuses are irremovable (though mutable) and must exist in some form in order to preserve the case management flow, with this order for ID purposes. */
insert into disposition_status(name, description) values('NEW', 'New Case');
insert into disposition_status(name, description) values('OPEN', 'Case is open');
insert into disposition_status(name, description) values('CLOSED', 'No action required');
insert into disposition_status(name, description) values('RE-OPEN', 'Re-opened case');
insert into disposition_status(name, description) values('PENDING CLOSURE','Case is pending closure');
