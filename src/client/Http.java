package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import support.Command;

public abstract class Http {
	
	protected String host;
	protected String resource;
	protected String ip;
	protected int port;
	protected Socket clientSocket = null;
	protected DataOutputStream outToServer;
	protected BufferedReader inFromServer;

	public Http(Command command, String host, String resource, String ip, int port) {
		try {
			Socket socket = new Socket(ip, port);
			System.out.println("Socket estabilished: " + socket.toString() );
			initialize(command, host, resource, ip, port, socket);
		} catch (IOException e) {
			System.out.println(e.toString());
		} 
	}
	
	public Http(Command command, String host, String resource, String ip, int port, Socket socket) {
		initialize(command, host, resource, ip, port, socket);
	}
	
	private void initialize(Command command, String host, String resource, String ip, int port, Socket socket) {
		this.host = host;
		this.resource = resource;
		this.ip = ip;
		this.port = port;
		
		try {
			clientSocket = socket;
			if (!clientSocket.isConnected()) {
				clientSocket = new Socket(ip, port);
				System.out.println("Socket estabilished: " + clientSocket.toString() );
			}

			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			initialRequest(command);
			
			if (command.equals(Command.GET))
				get();
			else if(command.equals(Command.PUT))
				put();
			else if(command.equals(Command.POST))
				post();
			else if(command.equals(Command.HEAD))
				head();
			
			outToServer.close();
			inFromServer.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		} 
	}
	
	protected abstract void initialRequest(Command command) throws IOException;
	protected abstract void put();
	protected abstract void get();
	protected abstract void head();
	protected abstract void post();
	
}
