package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import support.Command;

/**
 * 
 * 
 * @author 	Tom Stappaerts, Benjamin Stienlet
 */
@SuppressWarnings("deprecation")
public class Handler implements Runnable {
	
	Socket socket;
	
	public Handler(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * New handler: thread: run()
	 */
	@Override
	public void run() {
		try {
			//Streams
			DataInputStream inFromClient = new DataInputStream(socket.getInputStream()); 
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			ArrayList<String> headers = new ArrayList<String>();
			String initialRequestLine;
			
			while(!socket.isClosed()) {
				// Socket timeout
				socket.setSoTimeout(20000);
				while ((initialRequestLine = inFromClient.readLine()) == null || initialRequestLine.trim().isEmpty()) {
					//Read again
				}
				System.out.println("Received: " + initialRequestLine);
				String header;
				System.out.println("Headers:");
				while((header = inFromClient.readLine()) != null && !header.equalsIgnoreCase("")) {
					System.out.println(header);
					headers.add(header);
				}

				parse(initialRequestLine,inFromClient,outToClient,headers);
			}
		} catch (SocketTimeoutException e) {
			System.out.println("Connection timed out");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception");
		} 
	}
	
	/**
	 * Parses the given string
	 */
	private void parse(String input, DataInputStream inFromClient, DataOutputStream outToClient, ArrayList<String> headers) {
		String commandInput = null;
		String versionNumber = null;
		String uri = null;
		try {
			// Parse uri
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
		if(versionNumber.equals("1.0") || versionNumber.equalsIgnoreCase("http/1.0"))
			new Response10(command, uri, inFromClient, outToClient, socket, headers);
		else if(versionNumber.equals("1.1") || versionNumber.equalsIgnoreCase("http/1.1"))
			new Response11(command, uri, inFromClient, outToClient, socket, headers);
		else
			throw new IllegalArgumentException();
	}
}