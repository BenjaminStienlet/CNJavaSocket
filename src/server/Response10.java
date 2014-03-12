package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import support.Command;

/**
 * @version 1.0
 */
public class Response10 extends Response {

	public Response10(Command command, String uri, DataInputStream inFromClient, DataOutputStream outToClient, 
			Socket socket, ArrayList<String> headers) {
		super(command, uri, inFromClient, outToClient, socket, headers);
	}
	
	public String toString() {
		return "HTTP/1.0";
	}

	@Override
	protected void put() {
		try {
			executePut();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void get() {
		try {
			executeGet();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void post() {
		put();
	}

	@Override
	protected void head() {
		try{
			executeHead();
			socket.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());

		}
	}
}
