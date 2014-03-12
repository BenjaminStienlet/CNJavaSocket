/**
 * 
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import support.Command;

public class Response11 extends Response {


	public Response11(Command command, String uri, DataInputStream inFromClient, DataOutputStream outToClient, 
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
			executePut();
			if (headers.contains("Connection: close") && !socket.isClosed()){
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
