/**
 * 
 */
package server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import support.Command;

/**
 * @author SWOP Group 3
 * @version 1.0
 */
public abstract class Response {

	protected String uri;
	protected BufferedReader inFromClient;
	protected DataOutputStream outToClient;
	protected Socket socket;
	protected ArrayList<String> headers;

	public Response(Command command, String uri, BufferedReader inFromClient, DataOutputStream outToClient, Socket socket, ArrayList<String> headers) {

		this.uri = uri;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.socket = socket;
		this.headers = headers;
		
		switch (command) {
		case GET: get();
		break;
		case PUT: put();
		break;
		case POST: post();
		break;
		case HEAD: head();
		break;
		}
	}
	
	protected abstract void get();
	
	protected abstract void put();
	
	protected abstract void post();
	
	protected abstract void head();
	
}
