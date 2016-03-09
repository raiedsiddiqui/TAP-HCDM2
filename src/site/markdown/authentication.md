Authentication
==============

Tapestry uses the Spring Security framework for user authentication.
Page access rules are defined in WEB-INF/spring-security.xml


Database
--------
Users are defined in the taphcdm_db.users table. This table contains the following fields:
* id: integer, primary key
* name: the full name of the user
* username: the username used for logging in
* password: the password used for logging in (this should be hashed but isn't implemented yet)
* role: ROLE_USER or ROLE_ADMIN, defines whether user is a caretaker or administrator
* enabled: true or false, defines whether account is active or not
* email: email address for user, used to send temporary login information
