/**
 * 
 */
package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import support.Command;

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
	
	public abstract String toString();

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	protected boolean executeHead() throws IOException, FileNotFoundException {
		if(uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		
//		String regex = "\\s*\\.(html|png|jpg|gif)$";
		
		File file = new File(uri);
		if(file.exists() && !file.isDirectory()) {
			if (uri.endsWith("html") || uri.endsWith("png") || uri.endsWith("jpg") || uri.endsWith("gif")) {
				List<String> headers = new ArrayList<String>();
				headers.add("HTTP/1.0 200 OK\n");
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss z");
				headers.add("Date: "+ dateFormat.format(cal.getTime()) + "\n");
				Scanner scan = new Scanner(file);
				String content = scan.useDelimiter("\\Z").next();
				headers.add("Content-Length: " + content.length() + "\n");

				if(uri.endsWith("html")) {
					headers.add("Content-Type: text/html\n");
				}
				else if (uri.endsWith("png")) {
					headers.add("Content-Type: image/png\n");
				}
				else if (uri.endsWith("jpg")) {
					headers.add("Content-Type: image/jpg\n");
				}
				else if (uri.endsWith("gif")) {
					headers.add("Content-Type: image/gif\n");
				}

				headers.add("\n");

				System.out.println("Headers sent to client:");
				for (String header : headers) {
					System.out.println(header);
					outToClient.writeBytes(header);
				}
				
				scan.close();
				return true;
			}
			else {
				outToClient.writeBytes(toString() + " 404 File Not Found\n\n");
				return false;
			}
		} else {
			outToClient.writeBytes(toString() + " 404 File Not Found\n\n");
			return false;
		}
	}

	/**
	 * 
	 */
	protected void executeGet() {
		try{
			if(uri.startsWith("/")) {
				uri = uri.substring(1);
			}
			if (executeHead()){
				File file = new File(uri);
				if (uri.endsWith("html")) {
					Scanner scan = new Scanner(file);
					String content = scan.useDelimiter("\\Z").next();
					System.out.println("Content of file: \n" + content);
					outToClient.writeBytes(content);
					scan.close();
				}
				else {
					FileInputStream scan = new FileInputStream(file);
					byte[] data = new byte[(int) file.length()];
					scan.read(data);
					outToClient.write(data);
					scan.close();
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 */
	protected void extractPut() {
		try{
			String inputString;
			String inputFromUser = "";
			int length = 0;
			for(String header: headers) {
				if (header.startsWith("Content-Length: ")) {
					String[] split = header.split(" ");
					String number = split[1].trim();
					length = Integer.parseInt(number);
				}
			}
	
			if (length > 0) {
				while (length > 0) {
					inputString = inFromClient.readLine();
					inputFromUser += inputString + "\n";
					byte[] bytes = inputString.getBytes();
					length -= bytes.length;
				}
			}
			else {
				while (socket.isConnected() && (inputString = inFromClient.readLine()) != null) {
					inputFromUser += inputString + "\n";
				}
			}
			System.out.println("Input: \n" + inputFromUser);
			if(uri.startsWith("/")) {
				uri = uri.substring(1);
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(uri), "utf-8"));
			out.write(inputFromUser);
			out.close();
	
			outToClient.writeBytes(toString() + " 200 OK\n\n");
	
		}
		catch(Exception e) {
			try{
				outToClient.writeBytes(toString() + " 500 Internal Server Error\n\n");
				System.out.println(e.toString());
			} catch (Exception e2) {
				System.out.println(e2.toString());
			}
		}
	}
	
}
