Messaging
=========

Tapestry allows administrators to send messages to volunteers.

Behind the scenes, we are simply adding an entry in the taphcdm_db.messages
database. When a volunteer goes to their inbox, we grab all messages that
have their name on it and present them in a list.
