package client;

import java.io.IOException;

import support.Command;

/**
 * Class to execute the given HTTP/1.0 command
 * 
 * @author 	Tom Stappaerts, Benjamin Stienlet
 */
@SuppressWarnings("deprecation")
public class Http10 extends Http{


	/**
	 * Constructor
	 * @param command
	 * @param host
	 * @param resource
	 * @param ip
	 * @param port
	 */
	public Http10(Command command, String host, String resource, String ip, int port) {
		super(command, host, resource, ip, port);
	}


	/**
	 * String representation
	 */
	@Override
	public String toString() {
		return "HTTP/1.0";
	}

	/**
	 * Handles the initialRequestLine
	 */
	@Override
	protected void initialRequest(Command command) throws IOException {
		String outputSentence = command + " " + resource + " " + toString();
		System.out.println("Input: " + outputSentence);
		outToServer.writeBytes(outputSentence + "\n");
	}

	/**
	 * Handles the put
	 */
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

	/**
	 * Handles the get Request
	 */
	@Override
	public void get() {
		try{
			//Read initialResponseLine
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


	/**
	 * Handles the head request
	 */
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


	/**
	 * Handles the post request
	 */
	@Override
	public void post() {
		put();
	}


	/**
	 * Executes a new get. For HTTP 1.0 this means a new socket
	 */
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
