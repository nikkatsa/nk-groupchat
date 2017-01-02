# nk-groupchat

This is a [__Netty__](http://netty.io) server using __Websockets__ for the communication between the server and a client.

## Functionality

The code provides basic functionality and the below actions:

* Connect to the webscoket server
* Log into the chat room, by choosing a user name
* Send chat messages which will be broadcasted to other connected clients
* Logout of the server

## Client UI

A very basic UI can be found at [/src/main/resources/index.html](https://github.com/nikkatsa/nk-groupchat/blob/master/src/main/resources/index.html)

## Server

The server code can be found at [/src/main/java/com/nikoskatsanos/netty/groupchat](https://github.com/nikkatsa/nk-groupchat/tree/master/src/main/java/com/nikoskatsanos/netty/groupchat)

### Building the server

Below are the steps for building the server. __Note__ that the code has a dependency to [nk-jutil](https://github.com/nikkatsa/nk-jutil), which is not pushed in Maven Central Repo, hence the user has to manually pull the project and build it, before building the Groupchat server.

* Pull [nk-jutil](https://github.com/nikkatsa/nk-jutil)
* Build nk-jutils by `mvn clean install`
* Pull [nk-groupchat](https://github.com/nikkatsa/nk-groupchat)
* Build nk-groupchat by `mvn clean install`
* Run __.sh__ file in /target/appassembler/bin
* Launch /src/main/resources/index.html
