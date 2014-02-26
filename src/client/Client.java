package client;

import support.Command;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
public class Client {
	
	
	public static void main(String[] args) {
		try {
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
		Socket clientSocket = new Socket("localhost", 6789); DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new
		InputStreamReader(clientSocket.getInputStream()));
		String sentence = inFromUser.readLine();
		outToServer.writeBytes(sentence + '\n');
		String modifiedSentence = inFromServer.readLine(); System.out.println("FROM SERVER: " + modifiedSentence); clientSocket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		}
	
	
	
	
	
	/**
	 * 
	 * 
	 * @param input
	 * @throws IllegalArgumentException
	 */
	public void command(String input) throws IllegalArgumentException {
		String commandInput = null;
		String versionNumber = null;
		String uri = null;
		int port = -1;
		try {
			String[] inputs = input.split(" ");
			commandInput = inputs[0];
			uri = inputs[1];
			port = Integer.parseInt(inputs[2]);
			versionNumber = inputs[3];
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
		
		Http version;
		
		if(versionNumber.equals("1.0") || versionNumber.equalsIgnoreCase("http/1.0"))
			 version = new Http10();
		else if(versionNumber.equals("1.1") || versionNumber.equalsIgnoreCase("http/1.1"))
			 version = new Http11();
		else
			throw new IllegalArgumentException();
		
		Command command = null;
		
		for(Command c : Command.values())
			if(commandInput.equalsIgnoreCase(c.toString()))
				command = c;
		if(command == null)
			throw new IllegalArgumentException();
		if(port <= 0)
			throw new IllegalArgumentException();
		
	}
	
}
