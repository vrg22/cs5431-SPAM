package communications;

interface Communications {
    // TODO: decide what type this data should be
    public void send(Message data);
	public Message receive();
}
