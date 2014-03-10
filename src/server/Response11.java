/**
 * 
 */
package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import support.Command;

public class Response11 extends Response {


	/**
	 * @param command
	 * @param uri
	 * @param inFromClient
	 * @param outToClient
	 * @param socket
	 * @param headers
	 */
	public Response11(Command command, String uri, BufferedReader inFromClient, DataOutputStream outToClient, 
			Socket socket, ArrayList<String> headers) {
		super(command, uri, inFromClient, outToClient, socket, headers);
	}
	
	public String toString() {
		return "HTTP/1.1";
	}

	@Override
	protected void head() {
		try{
			outToClient.writeBytes(toString() + " 100 Continue\n\n");
			executeHead();
			if (headers.contains("Connection: close")){
				socket.close();
			}
				
		} catch(Exception e) {
			System.out.println(e.getMessage());

		}
	}

	@Override
	protected void get() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void put() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void post() {
		// TODO Auto-generated method stub
		
	}
	
}
