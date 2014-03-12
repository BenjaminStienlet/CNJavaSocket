package server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	protected DataInputStream inFromClient;
	protected DataOutputStream outToClient;
	protected Socket socket;
	protected ArrayList<String> headers;

	public Response(Command command, String uri, DataInputStream inFromClient, DataOutputStream outToClient, Socket socket, ArrayList<String> headers) {

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
				headers.add(toString() + " 200 OK\n");
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss z");
				headers.add("Date: "+ dateFormat.format(cal.getTime()) + "\n");
				headers.add("Content-Length: " + file.length() + "\n");

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
					System.out.print(header);
					outToClient.writeBytes(header);
				}

				return true;
			}
			else {
				error404();
				return false;
			}
		} else {
			error404();
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
	protected void executePut() {
		try{
			int length = 0;
			for(String header: headers) {
				if (header.startsWith("Content-Length: ")) {
					String[] split = header.split(" ");
					String number = split[1].trim();
					length = Integer.parseInt(number);
				}
			}

			if (length > 0) {
				byte[] bytes = new byte[length];
				inFromClient.readFully(bytes,0,length);

				if(uri.startsWith("/")) {
					uri = uri.substring(1);
				}
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uri));
				out.write(bytes);
				out.close();
				System.out.println("Written to File");
				outToClient.writeBytes(toString() + " 200 OK\n\n");

			} else {
				error500();
			}
		}
		
		catch(Exception e) {
			try{
				error500();
				System.out.println(e.getStackTrace());
			} catch (Exception e2) {
				System.out.println(e2.getStackTrace());
			}
		}
	}
	
	protected void error(String errorMessage) {
		try {
			outToClient.writeBytes("Content-Type: text/html");

			List<String> headers = new ArrayList<String>();
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss z");
			headers.add("Date: "+ dateFormat.format(cal.getTime()) + "\n");
			headers.add("Content-Length: " + errorMessage.getBytes("utf-8").length + "\n");
			headers.add("Content-Type: text/html\n");
			headers.add("\n");

			System.out.println("Headers sent to client:");
			for (String header : headers) {
				System.out.println(header);
				outToClient.writeBytes(header);
			}

			outToClient.writeBytes(errorMessage);
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	protected void error404() {
		try {
			outToClient.writeBytes(toString() + " 404 File Not Found\n");
			error("<html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1><p>The requested URL was not found on this server.</p></body></html>");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	protected void error500() {
		try {
			outToClient.writeBytes(toString() + " 500 Server Error\n");
			error("<html><head><title>500 Server Error</title></head><body><h1>500 Server Error</h1><p>An unexpected server error.</p></body></html>");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
