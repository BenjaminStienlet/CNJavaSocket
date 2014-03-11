/**
 * 
 */
package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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
		try {
			outToClient.writeBytes(toString() + " 100 Continue\n\n");
			executeGet();
			if (headers.contains("Connection: close")){
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void put() {
		try {
			outToClient.writeBytes(toString() + " 100 Continue\n\n");
			extractPut();
			if (headers.contains("Connection: close")){
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void post() {
		put();
	}
	
}
