
-- ----------------------------
-- Roles
-- ----------------------------
INSERT INTO `role` VALUES ('1', 'Admin');
INSERT INTO `role` VALUES ('2', 'Manage Queries');
INSERT INTO `role` VALUES ('3', 'View Passenger');
INSERT INTO `role` VALUES ('4', 'Manage Watch List');
INSERT INTO `role` VALUES ('5', 'Manage Rules');
INSERT INTO `role` VALUES ('6', 'SysAdmin');
INSERT INTO `role` VALUES ('7', 'Manage Hits');
INSERT INTO `role` VALUES ('8', 'Manage Cases');


-- ----------------------------
-- Users
-- ----------------------------
-- password is 'password'
INSERT INTO gtas.user (user_id, active, email, first_name, high_priority_hits_email, email_enabled, last_name, password) VALUES ('GTAS', 1, 'Email', 'GTAS', false, false, 'Application User', '$2a$10$0rGc.QzA0MH7MM7OXqynJ.2Cnbdf9PiNk4ffi4ih6LSW3y21OkspG');
INSERT INTO gtas.user (user_id, active, email, first_name, high_priority_hits_email, email_enabled, last_name, password) VALUES ('ADMIN', 1, 'Email', 'Admin', false, false, 'Admin User', '$2a$10$0rGc.QzA0MH7MM7OXqynJ.2Cnbdf9PiNk4ffi4ih6LSW3y21OkspG');

-- ----------------------------
-- Records of user_role
-- ----------------------------

INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('ADMIN', 1);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('GTAS', 5);

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
insert into app_configuration (opt, val, description) values('MAX_PASSENGERS_PER_FUZZY_MATCH','2','Maximum number of passengers processed by rules per run');
insert into app_configuration (opt, val, description) values('MAX_RULE_DETAILS_CREATED','250','Number of hit details saved per batch.');
insert into app_configuration (opt, val, description) values('DATA_MANAGEMENT_TRUNC_TYPE_FLAG', 'ALL', 'Type of values include, ALL, APIS, PNR');
insert into app_configuration (opt, val, description)values ('FUZZY_MATCHING', 'true', 'Fuzzy matching toggle');
insert into app_configuration (opt, val, description)values ('DATA_MANAGEMENT_CUT_OFF_TIME_SPAN', '6', 'Time in months past which we can truncate data from our database');
insert into app_configuration (description, opt, val) values ('Time in years - an offset in which quickmatch can apply during fuzzy matching', 'QUICKMATCH_DOB_YEAR_OFFSET', '3');
INSERT INTO app_configuration (description, opt, val) VALUES ('UTC Server Time', 'UTC_SERVER', 'true');
INSERT INTO app_configuration (description, opt, val) VALUES ('Interpol Red Notices Watch List Hit Notification ARN', 'INTERPOL_SNS_NOTIFICATION_ARN', '');
INSERT INTO app_configuration (description, opt, val) VALUES ('Toggle Interpol Red Notices Watch List Hit Notification', 'ENABLE_INTERPOL_HIT_NOTIFICATION', 'false');
INSERT INTO app_configuration (description, opt, val) VALUES ('Interpol Red Notices ID', 'INTERPOL_WATCHLIST_ID', '');
INSERT INTO app_configuration (description, opt, val) VALUES ('Interpol Red Notices Notification Subject', 'INTERPOL_SNS_NOTIFICATION_SUBJECT', 'GTAS priority Hit Notification');
INSERT INTO app_configuration (description, opt, val) VALUES ('Recompile Rules', 'RECOMPILE_RULES', 'false');


insert into hit_category(id, category, description, severity) values(1, 'General', 'General category', 2);
insert into hit_category(id, category, description, severity) values(2, 'Terrorism', 'Terrorism related entities',0);
insert into hit_category(id, category, description, severity) values(3, 'World Health', 'Health Alert related',1);
insert into hit_category(id, category, description, severity) values(4, 'Federal Law Enforcement', 'Federal watch category',0);
insert into hit_category(id, category, description, severity) values(5, 'Local Law Enforcement', 'Local watch category',0);

INSERT INTO gtas.user_group (id, created_at, created_by, updated_at, updated_by, ug_name) VALUES (1, null, null, null, null, 'default');
INSERT INTO gtas.ug_user_join (ug_id, user_id) VALUES (1, 'ADMIN');
INSERT INTO gtas.ug_user_join (ug_id, user_id) VALUES (1, 'GTAS');
INSERT INTO gtas.ug_hit_category_join (ug_id, hc_id) VALUES (1, 1);
INSERT INTO gtas.ug_hit_category_join (ug_id, hc_id) VALUES (1, 2);
INSERT INTO gtas.ug_hit_category_join (ug_id, hc_id) VALUES (1, 3);
INSERT INTO gtas.ug_hit_category_join (ug_id, hc_id) VALUES (1, 4);
INSERT INTO gtas.ug_hit_category_join (ug_id, hc_id) VALUES (1, 5);

INSERT INTO gtas.note_type (id, created_at, created_by, updated_at, updated_by, nt_type) VALUES (1, null, null, null, null, 'GENERAL_PASSENGER');
INSERT INTO gtas.note_type (id, created_at, created_by, updated_at, updated_by, nt_type) VALUES (2, null, null, null, null, 'DELETED');

-- ----------------------------
-- Manual Hit HitMaker Population
-- ----------------------------
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('MANUAL_HIT', 'GTAS', 1);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('MANUAL_HIT', 'GTAS', 2);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('MANUAL_HIT', 'GTAS', 3);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('MANUAL_HIT', 'GTAS', 4);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('MANUAL_HIT', 'GTAS', 5);

INSERT INTO manual_lookout (description, id) VALUES ('General', 1);
INSERT INTO manual_lookout (description, id) VALUES ('Terrorism', 2);
INSERT INTO manual_lookout (description, id) VALUES ('World Health', 3);
INSERT INTO manual_lookout (description, id) VALUES ('Federal Law Enforcement', 4);
INSERT INTO manual_lookout (description, id) VALUES ('Local Law Enforcement', 5);

-- ----------------------------
-- External Hit HitMaker Population
-- ----------------------------

INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('EXTERNAL_HIT', 'GTAS', 1);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('EXTERNAL_HIT', 'GTAS', 2);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('EXTERNAL_HIT', 'GTAS', 3);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('EXTERNAL_HIT', 'GTAS', 4);
INSERT INTO hit_maker (hm_hit_type, hm_author, hm_hit_category) VALUES ('EXTERNAL_HIT', 'GTAS', 5);

INSERT INTO external_hit (description, id) VALUES ('General', 6);
INSERT INTO external_hit (description, id) VALUES ('Terrorism', 7);
INSERT INTO external_hit (description, id) VALUES ('World Health', 8);
INSERT INTO external_hit (description, id) VALUES ('Federal Law Enforcement', 9);
INSERT INTO external_hit (description, id) VALUES ('Local Law Enforcement', 10);

