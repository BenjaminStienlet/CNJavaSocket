/**
 * 
 */
package server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
				outToClient.writeBytes("HTTP/1.0 200 OK\n");
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss z");
				outToClient.writeBytes("Date: "+ dateFormat.format(cal.getTime()) + "\n");
				Scanner scan = new Scanner(file);
				String content = scan.useDelimiter("\\Z").next();
				outToClient.writeBytes("Content-Length: " + content.length() + "\n");

				if(uri.endsWith("html")) {
					outToClient.writeBytes("Content-Type: text/html\n");
				}
				else if (uri.endsWith("png")) {
					outToClient.writeBytes("Content-Type: image/png\n");
				}
				else if (uri.endsWith("jpg")) {
					outToClient.writeBytes("Content-Type: image/jpg\n");
				}
				else if (uri.endsWith("gif")) {
					outToClient.writeBytes("Content-Type: image/gif\n");
				}

				outToClient.writeBytes("\n");
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
	
}
