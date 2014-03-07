package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.io.InputStreamReader;

import support.Command;


public class Handler implements Runnable {
	
	Socket socket;
	
	public Handler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			String initialRequestLine = inFromClient.readLine();
			System.out.println("Received: " + initialRequestLine);
			
			ArrayList<String> headers = new ArrayList<String>();
			String header;
			System.out.println("Headers:");
			while(!(header = inFromClient.readLine()).equalsIgnoreCase("")) {
				System.out.println(header);
				headers.add(header);
			}
			
			parse(initialRequestLine,inFromClient,outToClient,headers);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void parse(String input, BufferedReader inFromClient, DataOutputStream outToClient, ArrayList<String> headers) {
		String commandInput = null;
		String versionNumber = null;
		String uri = null;
		
		try {
			String[] inputs = input.split(" ");
			commandInput = inputs[0];
			uri = inputs[1];
			versionNumber = inputs[2];
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
		//parse command
		Command command = null;
		for(Command c : Command.values())
			if(commandInput.equalsIgnoreCase(c.toString())) command = c;
		if(command == null) throw new IllegalArgumentException();

		//HTTP version
		Response version;
		if(versionNumber.equals("1.0") || versionNumber.equalsIgnoreCase("http/1.0"))
			version = new Response10(command, uri, inFromClient, outToClient, socket, headers);
		//else if(versionNumber.equals("1.1") || versionNumber.equalsIgnoreCase("http/1.1"))
			//version = new Response11();
		else
			throw new IllegalArgumentException();

		
	}
}