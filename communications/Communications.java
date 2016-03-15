package communications;

interface Communications {
    // TODO: decide what type this data should be
    void send(Message data);
	Message receive();
}
