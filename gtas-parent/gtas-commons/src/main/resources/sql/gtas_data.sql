-- ----------------------------
-- Roles
-- ----------------------------
INSERT INTO `role` VALUES ('1', 'Admin');
INSERT INTO `role` VALUES ('2', 'Manage Queries');
INSERT INTO `role` VALUES ('3', 'View Passenger');
INSERT INTO `role` VALUES ('4', 'Manage Watch List');
INSERT INTO `role` VALUES ('5', 'Manage Rules');
INSERT INTO `role` VALUES ('6', 'SysAdmin');
INSERT INTO `role` VALUES ('7', 'One Day Lookout');


-- ----------------------------
-- Users
-- ----------------------------
-- password is 'password'
INSERT INTO `user` VALUES ('gtas',1, 'GTAS', 'Application User', '$2a$10$0rGc.QzA0MH7MM7OXqynJ.2Cnbdf9PiNk4ffi4ih6LSW3y21OkspG');
INSERT INTO `user` VALUES ('admin',1, 'Admin', 'Admin', '$2a$10$0rGc.QzA0MH7MM7OXqynJ.2Cnbdf9PiNk4ffi4ih6LSW3y21OkspG');

-- ----------------------------
-- Records of user_role
-- ----------------------------

INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('admin', 1);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('gtas', 5);

-- ----------------------------
-- Records of flight_direction
-- ----------------------------

INSERT INTO `flight_direction` VALUES (1,'I', 'Inbound');
INSERT INTO `flight_direction` VALUES (2,'O', 'Outbound');
INSERT INTO `flight_direction` VALUES (3,'C', 'Continuance');
INSERT INTO `flight_direction` VALUES (4,'A', 'Any');

-- ----------------------------
-- Records of app_configuration
-- ----------------------------
insert into app_configuration (opt, val, description) values('HOME_COUNTRY', 'USA', 'home country for the loader to determine incoming/outgoing flights');
insert into app_configuration (opt, val, description) values('ELASTIC_HOSTNAME','localhost','ElasticSearch hostname');
insert into app_configuration (opt, val, description) values('ELASTIC_PORT','9300','ElasticSearch port');
insert into app_configuration (opt, val, description) values('QUEUE_OUT', 'GTAS_Q_OUT', 'queue name for storing outgoing messages');
insert into app_configuration (opt, val, description) values('QUEUE_IN', 'GTAS_Q_IN', 'queue name for storing incoming messages');
insert into app_configuration (opt, val, description) values('UPLOAD_DIR', 'C:\\MESSAGE', 'directory for uploading files from UI');
insert into app_configuration (opt, val, description) values('HOURLY_ADJ','-5','Dashboard Time Adjustment');
insert into app_configuration (opt, val, description) values('DASHBOARD_AIRPORT','IAD','Dashboard Airport');
insert into app_configuration (opt, val, description) values('SMS_TOPIC_ARN','','The ARN of the topic used by SmsService');
insert into app_configuration (opt, val, description) values('MATCHING_THRESHOLD','.95','Threshold which to determine name match');
insert into app_configuration (opt, val, description) values('MAX_PASSENGER_QUERY_RESULT','1000','Maximum amount of passenger results from query allowed');
insert into app_configuration (opt, val, description) values('MAX_FLIGHT_QUERY_RESULT','1000','Maximum amount of flight results from query allowed');
insert into app_configuration (opt, val, description) values('FLIGHT_RANGE','3','Time range for adding flights to name matching queue');
insert into app_configuration (opt, val, description) values('REDIS_KEYS_TTL','5','Number of days indexed REDIS Keys to expire in');
insert into app_configuration (opt, val, description) values('REDIS_KEYS_TTL_TIME_UNIT','DAYS','REDIS keys expiration time units - DAYS or MINUTES ');
insert into app_configuration (opt, val, description) values('APIS_ONLY_FLAG','FALSE','Is APIS the only message source in use.');
insert into app_configuration (opt, val, description) values('APIS_VERSION','16B','Latest APIS version being used.');
insert into app_configuration (opt, val, description) values('MAX_RULE_HITS','300','Number of rule hits allowed per rule');
insert into app_configuration (opt, val, description) values('BOOKING_COMPRESSION_AMOUNT','50','Maximum number of messages processed by compression job at each run');
insert into app_configuration (opt, val, description) values('MAX_PASSENGERS_PER_RULE_RUN','3000','Maximum number of passengers processed by rules per run');
insert into app_configuration (opt, val, description) values('MAX_PASSENGERS_PER_FUZZY_MATCH','2','Maximum number of passengers processed by rules per run');
insert into app_configuration (opt, val, description) values('MAX_MESSAGES_PER_RULE_RUN','500','Maximum number of messages processed by rules per run');
insert into app_configuration (opt, val, description) values('MAX_FLIGHTS_PER_BATCH','2','Number of flights saved per batch.');
insert into app_configuration (opt, val, description) values('THREADS_ON_LOADER','5','Number of threads on loader.');
insert into app_configuration (opt, val, description) values('DATA_MANAGEMENT_TRUNC_TYPE_FLAG', 'ALL', 'Type of values include, ALL, APIS, PNR');
insert into app_configuration (opt, val, description)values ('THREADS_ON_RULES', '5', 'Number of threads on loader.');
insert into app_configuration (opt, val, description)values ('FUZZY_MATCHING', 'true', 'Fuzzy matching toggle');
insert into app_configuration (opt, val, description)values ('DATA_MANAGEMENT_CUT_OFF_TIME_SPAN', '6', 'Time in months past which we can truncate data from our database');
insert into app_configuration (opt, val, description)values ('GRAPH_DB_URL', 'bolt://localhost:7687', 'Neo4J Address');
insert into app_configuration (opt, val, description)values ('GRAPH_DB_TOGGLE', 'false', 'Neo4J Toggle');
insert into app_configuration (description, opt, val) values ('Time in years - an offset in which quickmatch can apply during fuzzy matching', 'QUICKMATCH_DOB_YEAR_OFFSET', '3');
INSERT INTO app_configuration (description, opt, val) VALUES ('UTC Server Time', 'UTC_SERVER', 'true');

/*These 4 statuses are irremovable (though mutable) and must exist in some form in order to preserve the case management flow, with this order for ID purposes. */
insert into disposition_status(id, name, description) values(1, 'NEW', 'New Case');
insert into disposition_status(id, name, description) values(2, 'OPEN', 'Case is open');
insert into disposition_status(id, name, description) values(3, 'CLOSED', 'No action required');
insert into disposition_status(id, name, description) values(4, 'RE-OPEN', 'Re-opened case');
insert into disposition_status(id, name, description) values(5, 'PENDING CLOSURE','Case is pending closure');

insert into hit_disposition_status(id, name, description) values(1, 'NEW', 'New Case');
insert into hit_disposition_status(id, name, description) values(2, 'OPEN', 'Case is open');
insert into hit_disposition_status(id, name, description) values(3, 'CLOSED', 'No action required');
insert into hit_disposition_status(id, name, description) values(4, 'RE-OPEN', 'Re-opened case');
insert into hit_disposition_status(id, name, description) values(5, 'PENDING CLOSURE','Case is pending closure');

insert into case_disposition_status(id, name, description) values(1, 'Admit', 'Admit');
insert into case_disposition_status(id, name, description) values(2, 'Deny Boarding', 'Deny Boarding');
insert into case_disposition_status(id, name, description) values(3, 'No Show', 'No Show');
insert into case_disposition_status(id, name, description) values(4, 'Cancelled', 'Cancelled');
insert into case_disposition_status(id, name, description) values(5, 'Duplicate','Duplicate');
insert into case_disposition_status(id, name, description) values(6, 'Refuse Entry', 'Refuse Entry');
insert into case_disposition_status(id, name, description) values(7, 'Secondary Referral', 'Secondary Referral');
insert into case_disposition_status(id, name, description) values(8, 'False Match', 'False Match');


insert into rule_category(catId, category, description, priority) values(1, 'General', 'General category', 5);
insert into rule_category(catId, category, description, priority) values(2, 'Terrorism', 'Terrorism related entities', 1);
insert into rule_category(catId, category, description, priority) values(3, 'World Health', 'Health Alert related', 2);
insert into rule_category(catId, category, description, priority) values(4, 'Federal Law Enforcement', 'Federal watch category', 3);
insert into rule_category(catId, category, description, priority) values(5, 'Local Law Enforcement', 'Local watch category', 4);

