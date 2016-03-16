package communications;


interface Communications {
    void send(Message data);
	Message receive();
}
