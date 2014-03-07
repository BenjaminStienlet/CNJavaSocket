package client;

import support.Command;

public class Http11 extends Http {


	public Http11(Command command, String host, String resource, String ip,
			int port) {
		super(command, host, resource, ip, port);
	}

	@Override
	public String toString() {
		return "HTTP/1.1";
	}

	@Override
	protected void put() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void get() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void head() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void post() {
		// TODO Auto-generated method stub
		
	}

	
	

//	if(toString().equals("HTTP/1.1")) {
//		outputSentence += "\n HOST: " + host;
//	}
}
