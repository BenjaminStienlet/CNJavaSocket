package client;

import support.Command;

public class Http11 extends Http {

	@Override
	public String toString() {
		return "HTTP/1.1";
	}

	@Override
	public void executeCommand(Command command, String host, String resource,
			String ip, int port) {
		// TODO Auto-generated method stub
		
	}
	
	

//	if(toString().equals("HTTP/1.1")) {
//		outputSentence += "\n HOST: " + host;
//	}
}
