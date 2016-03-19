# cs5431-SPAM
A secure password manager for CS 5431, System Security Practicum

## Compile
To compile the system, enter SPAM's root directory and run:
    ant
This will create a /bin/ directory with the .class files for the
project.

## Run
To run the server application, enter the /bin/ directory and run:
    java communications/CommServer
To run the client application, enter the /bin/ directory and run:
    java client/ClientUser localhost 5998
(change the hostname and port # to the server's location/port)
