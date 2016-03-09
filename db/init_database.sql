/*
CREATE DATABASE IF NOT EXISTS survey_app;
USE survey_app;
*/

CREATE DATABASE IF NOT EXISTS taphcdm_db;
USE taphcdm_db;


CREATE TABLE IF NOT EXISTS users (
	user_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows for 255 users*/
	name VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	phone_number VARCHAR(20),
	site INT UNSIGNED NOT NULL,
	enabled BOOLEAN NOT NULL,
	email VARCHAR(50) NOT NULL,
	role VARCHAR(45),
	organization MEDIUMINT UNSIGNED NOT NULL, /*for volunteer and volunteer coordinator*/
	PRIMARY KEY (user_ID)
);

CREATE TABLE IF NOT EXISTS patients (
	patient_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 patients*/
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	preferredname VARCHAR(255),
	gender VARCHAR(3), /*Using VARCHAR(3) allows for 3 characters, expecting values like 'M', 'F', 'MTF', etc...*/
	email VARCHAR(50),
	volunteer TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	notes TEXT,
	alerts TEXT,	
	clinic MEDIUMINT UNSIGNED,
	myoscar_verified TINYINT(1) NOT NULL, /* 0--not authenticated, 1--authenticated*/
	volunteer2 TINYINT UNSIGNED NOT NULL,/* Same as user_ID */
	username VARCHAR(255) NOT NULL, /* username used to login in myoscar account */
	mrp SMALLINT UNSIGNED NOT NULL, 
	mrp_firstname VARCHAR(50) NOT NULL,
	mrp_lastname VARCHAR(50) NOT NULL,
	research_ID VARCHAR(10) NOT NULL, 
	enabled BOOLEAN NOT NULL,
	tap_username VARCHAR(50) NOT NULL,
	tap_password VARCHAR(255) NOT NULL,	
		
	PRIMARY KEY (patient_ID)
);

CREATE TABLE IF NOT EXISTS surveys (
	survey_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows 65,535  surveys*/
	title VARCHAR(50),
	type VARCHAR(255),
	contents MEDIUMBLOB,
	last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, /*Automatically updates when record changed*/
    priority TINYINT(1), /*Between 0-9*/
    description TEXT,
    site MEDIUMINT UNSIGNED NOT NULL,
    isDefault TINYINT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (survey_ID)
);

CREATE TABLE IF NOT EXISTS survey_results (
	result_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 results*/
	survey_ID SMALLINT UNSIGNED NOT NULL, /*Same as survey_ID field in surveys*/
	patient_ID SMALLINT UNSIGNED NOT NULL, /*Same as patient_ID in patients*/
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the survey (0=incomplete)*/
	startDate DATETIME, /*Automatically assigns the startDate when the survey is assigned*/
	editDate TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP, /*editDate represents the last edit date*/
	data MEDIUMBLOB, /*The survey data*/
	PRIMARY KEY (result_ID)
);

CREATE TABLE IF NOT EXISTS activities (
	event_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED INT allows for 4,294,967,295 events, which should be enough*/
	event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	start_time TIMESTAMP NULL,
	end_time TIMESTAMP NULL,
	volunteer SMALLINT UNSIGNED NOT NULL,		
	description TEXT,
	organization MEDIUMINT UNSIGNED,
	PRIMARY KEY (event_ID)
);

CREATE TABLE IF NOT EXISTS user_logs(
	log_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED INT allows for 4,294,967,295 logs, which should be enough*/
	event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	description TEXT,
	user SMALLINT UNSIGNED NOT NULL,	
	PRIMARY KEY (log_ID)
);

CREATE TABLE IF NOT EXISTS appointments (
	appointment_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 appointments*/
	volunteer TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	partner TINYINT UNSIGNED,
	patient SMALLINT UNSIGNED NOT NULL, /* Same as patient_ID */
	date_time DATETIME NOT NULL, /*Contains both date and time, separated using functions in query*/
	comments TEXT, /*Volunteer visit comments*/
	status TEXT NOT NULL, /*Approval status of appointment*/
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the appointment (0=incomplete)*/
	contactedAdmin BOOLEAN NOT NULL DEFAULT 0, /*The status of the volunteer contacted the admin*/
	hasNarrative BOOLEAN NOT NULL DEFAULT 0, /*The narrative has been completed of the appointment (0=incomplete)*/
	alerts TEXT,
	key_observations TEXT,
	plan TEXT,	
	type SMALLINT UNSIGNED NOT NULL,
	PRIMARY KEY(appointment_ID)
);

CREATE TABLE IF NOT EXISTS messages (
	message_ID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	recipient INT UNSIGNED NOT NULL, /* Same as user_ID */
	sender INT UNSIGNED NOT NULL,
	msg TEXT,
    sent TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	subject VARCHAR(255) NOT NULL,
	msgRead BOOLEAN NOT NULL DEFAULT 0,
	PRIMARY KEY (message_ID)
);

CREATE TABLE IF NOT EXISTS pictures (
    picture_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    path VARCHAR(255), /* path to picture file */
    owner SMALLINT, /* the ID of the user or patient the picture belongs to */
    owner_is_user BOOLEAN, /* 1 if the value of owner refers to a user, 0 if a patient */
    PRIMARY KEY (picture_ID)
);

CREATE TABLE IF NOT EXISTS narratives (
    narrative_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL,
    contents TEXT,
    edit_Date TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP, /*edit_Date represents the last edit date*/    
    volunteer SMALLINT UNSIGNED NOT NULL,
    patient_ID SMALLINT UNSIGNED NOT NULL,
    appointment SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY (narrative_ID)
);

CREATE TABLE IF NOT EXISTS volunteers (
	volunteer_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	preferredname VARCHAR(255),
	username VARCHAR(50),
	password VARCHAR(255) NOT NULL,	
	gender VARCHAR(3), /*Using VARCHAR(3) allows for 3 characters, expecting values like 'M', 'F', 'MTF', etc...*/
	email VARCHAR(50),
	experience_level VARCHAR(1), /* Using VARCHAR(1) for 1 character, expecting value like "E", "B", "I"...*/
	street_number VARCHAR(20),
	street VARCHAR(100),
	appartment VARCHAR(10),	
	city VARCHAR(50),
	province VARCHAR(3),
	country VARCHAR(50),
	home_phone VARCHAR(50),
	cell_phone VARCHAR(50),
	emergency_contact VARCHAR(50),
	emergency_phone VARCHAR(100),
	postal_code VARCHAR(10),
	notes TEXT,
	availability TEXT,
	organization MEDIUMINT UNSIGNED NOT NULL,
	total_vlc_score DECIMAL(6,2),
	number_years_experience DECIMAL(6,2),
	availability_per_month DECIMAL(6,2),
	technology_skills_score DECIMAL(6,2),
	perception_older_adult_score DECIMAL(6,2),
	vlc_ID MEDIUMINT UNSIGNED NOT NULL,
	interview_date DATE NOT NULL DEFAULT '1900-01-01',
	date_of_birth DATE NOT NULL DEFAULT '1900-01-01',
	mature_or_student VARCHAR(5), /* Using VARCHAR(1) for 1 character, expecting value like "S"--Student, "M"--Mature*/
	reference1 VARCHAR(50),
	reference2 VARCHAR(50),
	reference_check_date DATE NOT NULL DEFAULT '1900-01-01',
	police_check_received_date DATE NOT NULL DEFAULT '1900-01-01',
	tb_test_received_date DATE NOT NULL DEFAULT '1900-01-01',
	vlc_completion_date DATE NOT NULL DEFAULT '1900-01-01',
	confidentiality_agreement_signed_date DATE NOT NULL DEFAULT '1900-01-01',
	volunteer_agreement_signed_date DATE NOT NULL DEFAULT '1900-01-01',
	photo_received_date DATE NOT NULL DEFAULT '1900-01-01',	
	source VARCHAR(100),
	application_form_completion_date DATE NOT NULL DEFAULT '1900-01-01',
	background_info TEXT NOT NULL,
	vol_experience TEXT NOT NULL,
	tech_comfort TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	language_info TINYINT NOT NULL DEFAULT 1, /* 1--Yes, 2-No*/
	application_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	interview_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	reference_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	police_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	tb_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	vlc_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	confidentiality_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	volagreement_completed TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	photo_received TINYINT(1) NOT NULL DEFAULT 2, /* 1--Yes, 2-No*/
	current_status TINYINT(1) NOT NULL DEFAULT 1, /* 1--Active, 2-Inactive*/
	reason_inactivity TEXT NOT NULL, 
	
	PRIMARY KEY (volunteer_ID)	
);

CREATE TABLE IF NOT EXISTS organizations (
	organization_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	primary_contact VARCHAR(255) NOT NULL,
	primary_phone VARCHAR(20),
	secondary_contact VARCHAR(255),
	secondary_phone VARCHAR(20),
	street_number VARCHAR(20),
	street VARCHAR(100),
	city VARCHAR(50),
	province VARCHAR(3),
	country VARCHAR(50),
	postal_code VARCHAR(10),
	
	PRIMARY KEY (organization_ID)	
);

CREATE TABLE IF NOT EXISTS clinics(
	clinic_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	clinic_name VARCHAR(255) NOT NULL,
	address VARCHAR(255),
	phone VARCHAR(50),
	site_ID MEDIUMINT UNSIGNED NOT NULL,
	
	PRIMARY KEY (clinic_ID)		
);

CREATE TABLE IF NOT EXISTS sites(
	site_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	site_name VARCHAR(255) NOT NULL,
		
	PRIMARY KEY (site_ID)		
);

CREATE TABLE IF NOT EXISTS mrps(
	mrp_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	role VARCHAR(50) NOT NULL,
	clinic SMALLINT UNSIGNED NOT NULL,
	
	PRIMARY KEY (mrp_ID)		
);

CREATE TABLE IF NOT EXISTS users_archive (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	deleted_user_ID INT UNSIGNED NOT NULL, /*Using UNSIGNED TINYINT allows for 255 users*/
	name VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL,
	phone_number VARCHAR(20),
	site VARCHAR(50),
	enabled BOOLEAN NOT NULL,
	email VARCHAR(50) NOT NULL,
	role VARCHAR(45),
	organization MEDIUMINT UNSIGNED NOT NULL, /*for volunteer and volunteer coordinator*/
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS narratives_archive (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,    
	updated_narrative_ID MEDIUMINT UNSIGNED NOT NULL,
    title VARCHAR(50) NOT NULL,
    contents TEXT,
    last_edit TIMESTAMP DEFAULT 0 ,  
    volunteer_ID SMALLINT UNSIGNED NOT NULL,
    patient_ID SMALLINT UNSIGNED NOT NULL,
    appointment_ID SMALLINT UNSIGNED NOT NULL,
    update_action VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,    
	updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS surveys_archive (
	id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows 255 surveys*/
	deleted_survey_ID TINYINT UNSIGNED NOT NULL,
	title VARCHAR(50),
	type VARCHAR(255),
	contents MEDIUMBLOB,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, /*Automatically updates when record changed*/
    priority TINYINT(1), /*Between 0-9*/
    description TEXT,
    site MEDIUMINT UNSIGNED NOT NULL,
    deleted_by VARCHAR(255) NOT NULL,
	 	
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS survey_results_archive (
	id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 results*/
	deleted_result_ID SMALLINT UNSIGNED NOT NULL,
	survey_ID SMALLINT UNSIGNED NOT NULL, /*Same as survey_ID field in surveys*/
	patient VARCHAR(255) NOT NULL, 
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the survey (0=incomplete)*/
	startDate DATETIME, /*Automatically assigns the startDate when the survey is assigned*/
	data MEDIUMBLOB, /*The survey data*/
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS activities_archive (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	deleted_event_ID INT UNSIGNED NOT NULL , /*Using UNSIGNED INT allows for 4,294,967,295 events, which should be enough*/
	start_time TIMESTAMP NOT NULL DEFAULT 0,
	end_time TIMESTAMP NOT NULL DEFAULT 0,
	volunteer VARCHAR(255) NOT NULL,	
	description TEXT,
	organization VARCHAR(255) NOT NULL,
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS volunteers_archive (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	deleted_volunteer_ID MEDIUMINT UNSIGNED NOT NULL ,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	preferredname VARCHAR(255),
	username VARCHAR(50),
	gender VARCHAR(10), 
	email VARCHAR(50),
	experience_level VARCHAR(20),
	street_number VARCHAR(20),
	street VARCHAR(100),
	appartment VARCHAR(10),	
	city VARCHAR(50),
	province VARCHAR(3),
	country VARCHAR(50),
	home_phone VARCHAR(20),
	cell_phone VARCHAR(20),
	emergency_contact VARCHAR(50),
	emergency_phone VARCHAR(20),
	postal_code VARCHAR(10),
	organization MEDIUMINT UNSIGNED NOT NULL,
	vlc_ID MEDIUMINT UNSIGNED NOT NULL,
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY (id)	
);

CREATE TABLE IF NOT EXISTS appointments_archive (
	id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
	deleted_appointment_ID SMALLINT UNSIGNED NOT NULL , /*Using UNSIGNED SMALLINT allows for 65,535 appointments*/
	volunteer TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	partner TINYINT UNSIGNED,
	patient SMALLINT UNSIGNED NOT NULL, /* Same as patient_ID */
	date_time DATETIME NOT NULL, /*Contains both date and time, separated using functions in query*/
	comments TEXT, /*Volunteer visit comments*/
	status TEXT NOT NULL, /*Approval status of appointment*/
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the appointment (0=incomplete)*/	
	type SMALLINT UNSIGNED NOT NULL,
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS messages_archive (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	deleted_message_ID BIGINT UNSIGNED NOT NULL ,
	recipient TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	sender TINYINT UNSIGNED NOT NULL,
	msg TEXT,
    sent TIMESTAMP NULL ,
	subject VARCHAR(255) NOT NULL,
	msgRead BOOLEAN NOT NULL DEFAULT 0,
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS organizations_archive (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	deleted_organization_ID MEDIUMINT UNSIGNED,
	name VARCHAR(255) NOT NULL,
	primary_contact VARCHAR(255) NOT NULL,
	primary_phone VARCHAR(20),
	secondary_contact VARCHAR(255),
	secondary_phone VARCHAR(20),
	street_number VARCHAR(20),
	street VARCHAR(100),
	city VARCHAR(50),
	province VARCHAR(3),
	country VARCHAR(50),
	postal_code VARCHAR(10),
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY (id)	
);

CREATE TABLE IF NOT EXISTS volunteer_surveys (
	survey_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows 65,535  surveys*/
	title VARCHAR(50),
	type VARCHAR(255),
	contents MEDIUMBLOB,
	last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, /*Automatically updates when record changed*/
    priority TINYINT(1), /*Between 0-9*/
    description TEXT,
    site MEDIUMINT UNSIGNED NOT NULL,
    isDefault TINYINT(1) UNSIGNED NOT NULL,
    
	PRIMARY KEY (survey_ID)
);

CREATE TABLE IF NOT EXISTS volunteer_survey_results (
	result_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 results*/
	volunteer_survey_ID SMALLINT UNSIGNED NOT NULL, /*Same as survey_ID field in surveys*/
	volunteer_ID SMALLINT UNSIGNED NOT NULL, /*Same as patient_ID in patients*/
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the survey (0=incomplete)*/
	startDate DATETIME, /*Automatically assigns the startDate when the survey is assigned*/
	editDate TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP, /*editDate represents the last edit date*/	
	data MEDIUMBLOB, /*The survey data*/
	PRIMARY KEY (result_ID)
);

CREATE TABLE IF NOT EXISTS activities_admin (
	event_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED INT allows for 4,294,967,295 events, which should be enough*/
	event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	start_time TIMESTAMP NULL,
	end_time TIMESTAMP NULL,
	userId SMALLINT UNSIGNED NOT NULL,		
	description TEXT,
	siteId SMALLINT UNSIGNED NOT NULL,
	PRIMARY KEY (event_ID)
);

CREATE TABLE IF NOT EXISTS activities_admin_archive (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	deleted_event_ID INT UNSIGNED NOT NULL , /*Using UNSIGNED INT allows for 4,294,967,295 events, which should be enough*/
	start_time VARCHAR(25),
	end_time VARCHAR(25),
	userId SMALLINT UNSIGNED NOT NULL,	
	description TEXT,
	siteId SMALLINT UNSIGNED NOT NULL,
	deleted_by VARCHAR(255) NOT NULL,
	deleted_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS site_preferences(
	preference_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
	site_ID SMALLINT UNSIGNED NOT NULL UNIQUE,
	sos_button TINYINT(1) NOT NULL, /* 0--Disable, 1--Enable*/
	elder_abuse_button VARCHAR(255),
	elder_abuse_content Text,
	self_harm_button VARCHAR(255),
	self_harm_content Text,
	crisis_lines_button VARCHAR(255),
	crisis_lines_content Text,
	sos_receiver VARCHAR(255),
	UNIQUE (site_ID),
	appt_notification_receiver VARCHAR(255),
	report_notification_receiver VARCHAR(255),
	social_context_report TINYINT(1), /* 0--Disable, 1--Enable*/
	social_context_template Text,
	alerts_report TINYINT(1), /* 0--Disable, 1--Enable*/
	alerts_content Text,	
	
	PRIMARY KEY (preference_ID)		
);
COMMIT;
