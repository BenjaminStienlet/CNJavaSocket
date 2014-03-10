package client;

import java.io.IOException;

import support.Command;

@SuppressWarnings("deprecation")
public class Http10 extends Http{


	/**
	 * @param command
	 * @param host
	 * @param resource
	 * @param ip
	 * @param port
	 */
	public Http10(Command command, String host, String resource, String ip, int port) {
		super(command, host, resource, ip, port);
	}


	@Override
	public String toString() {
		return "HTTP/1.0";
	}

	@Override
	protected void initialRequest(Command command) throws IOException {
		String outputSentence = command + " " + resource + " " + toString();
		System.out.println("Input: " + outputSentence);
		outToServer.writeBytes(outputSentence + "\n");
	}

	@Override
	public void put() {
		try{
			executePut();
			if(!clientSocket.isClosed()) {
				System.out.println("closing socket");
				clientSocket.close();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void get() {
		try{
			outToServer.writeBytes("\n");
			System.out.println("Written output");
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Headers: \n");
			executeGet(status);
			if (!clientSocket.isClosed()) {
				System.out.println("Closing socket");
				clientSocket.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}


	@Override
	public void head() {
		try{
			executeHead();

			if (!clientSocket.isClosed()) {
				System.out.println("Closing socket");
				clientSocket.close();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


	@Override
	public void post() {
		put();
	}


	@Override
	protected void executeGet(String host2, String resource2, String ip2) {
		try {
			clientSocket.close();
			System.out.println("Socket closed");
		} catch (IOException e) {
		}
		new Http10(Command.GET,host2,resource2,ip2,this.port);
	}



}
