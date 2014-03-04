package client;

import support.Command;

public abstract class Http {
	
	public abstract void executeCommand(Command command, String host, String resource, String ip, int port);
	
}
