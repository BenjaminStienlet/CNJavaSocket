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
		this.host = host;
		this.resource = resource;
		this.ip = ip;
		this.port = port;
		
		try {
			clientSocket = new Socket(ip, port);
			System.out.println("Socket estabilished: " + clientSocket.toString() );

			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"ISO-8859-15"));
			String outputSentence = command + " " + resource + " " + toString();
			System.out.println("Input: " + outputSentence);
			outToServer.writeBytes(outputSentence + "\n");

//			switch (command) {
//				case PUT: put();
//				case GET: get();
//				case POST: post();
//				case HEAD: head();
//			}
			
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
	
	
	protected abstract void put();
	protected abstract void get();
	protected abstract void head();
	protected abstract void post();
	
}
