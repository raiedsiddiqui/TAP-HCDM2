TAPP
====

The Tapestry thing.

For project mockups and planning, see https://oscaremr.atlassian.net/wiki/display/TAPP/Tapestry+Home

Documentation can be found at src/site/markdown/

Installation
------------

- Run `git clone https://github.com/tapestry-team/tapestry.git"
- Run `cd tapestry`.
- Open the 'tapp-admin' file and make sure that WEBAPP_DIR points to your Tomcat web app folder.
- Run db/init_database.sql and db/insert_users.sql to create the database tables and add a default administrator (username and password 'admin').
- Run `./tapp-admin --install`
- NOTE: You will also have to configure the database username and password in WEB-INF/spring-security.xml or you will not be able to log in

You should now be able to log in with the administrator account and begin adding users and patients. I recommend creating a different administrator and disabling the default one.
