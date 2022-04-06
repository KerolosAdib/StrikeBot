# Strike-Bot
A discord bot that I created that can be used as a strike system, and view how many strikes each person has.

## Discord Scopes
The required scopes for this bot are `bot` and `applications.commands`, and is set as an administrator.

## Strike Commands
Slash commands are related to the strike functionality.
* /strike 'user': strikes a specific user, after 3 strikes they get kicked from their current server. Note this commands can only be used users with administrative permissions (and me for testing purposes).
* /getstrikes: pulls up an ephemeral embed that shows how many strikes each user has showing ten users per page. Can be used by anyone.

## Unrelated Commands
Slash commands that are not related to the main functionality but are commands that I added for fun. Note some of these commands already existed iside of discord already.
All of these commands except /roulette can only be used with administrative permissions.
* /disconnect 'user': disconnects a specified user from a voice channel.
* /roulette: picks a random voice channel with people in it, and then picks a random person inside of that specified voice channel and disconnects them. (might rework this later)
* /kick 'user': kicks a specified user from the server.
* /changemembernickname 'user' 'nickname': changes the specified users nickname into the specified nickname.

## Invite link
* https://discord.com/api/oauth2/authorize?client_id=587869275814101005&permissions=8&scope=bot
