package main.java;

interface Communications {
    void send(Message data);
	Message receive();
}
