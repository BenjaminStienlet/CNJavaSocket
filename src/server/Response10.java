/**
 * 
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import support.Command;

/**
 * @version 1.0
 */
public class Response10 extends Response {

	public Response10(Command command, String uri, BufferedReader inFromClient, DataOutputStream outToClient, Socket socket, ArrayList<String> headers) {
		super(command, uri, inFromClient, outToClient, socket, headers);
	}

	@Override
	protected void put() {
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
			
			outToClient.writeBytes("HTTP/1.0 200 OK\n\n");
			socket.close();

		}
		catch(Exception e) {
			try{
				outToClient.writeBytes("HTTP/1.0 500 Internal Server Error\n\n");
				System.out.println(e.toString());
			} catch (Exception e2) {
				System.out.println(e2.toString());
			}
		}
	}

	@Override
	protected void get() {
		try{
			if(uri.startsWith("/")) {
				uri = uri.substring(1);
			}
			File file = new File(uri);
			if(file.exists() && !file.isDirectory()) {
				outToClient.writeBytes("HTTP/1.0 200 OK\n\n");
				Scanner scan = new Scanner(file);
				String content = scan.useDelimiter("\\Z").next();
				System.out.println("Content of file: \n" + content);
				outToClient.writeBytes(content);
				scan.close();
			} else {
				outToClient.writeBytes("HTTP/1.0 404 File Not Found\n\n");
			}
			socket.close();
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void post() {
		put();
		
	}

	@Override
	protected void head() {
		try{
			if(uri.startsWith("/")) {
				uri = uri.substring(1);
			}
			File file = new File(uri);
			if(file.exists() && !file.isDirectory()) {
				outToClient.writeBytes("HTTP/1.0 200 OK\n");
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss z");
				outToClient.writeBytes("Date: "+ dateFormat.format(cal.getTime()) + "\n");
				Scanner scan = new Scanner(file);
				String content = scan.useDelimiter("\\Z").next();
				outToClient.writeBytes("Content-Length: " + content.length() + "\n");
				outToClient.writeBytes("Content-Type: text/html\n");
				outToClient.writeBytes("\n");
				scan.close();
			} else {
				outToClient.writeBytes("HTTP/1.0 404 File Not Found\n\n");
			}
			socket.close();

		} catch(Exception e) {
			System.out.println(e.getMessage());

		}
	}
}
