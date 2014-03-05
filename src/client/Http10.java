package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import support.Command;

public class Http10 extends Http{


	/**
	 * @param command
	 * @param host
	 * @param resource
	 * @param ip
	 * @param port
	 */
	public Http10(Command command, String host, String resource, String ip,
			int port) {
		super(command, host, resource, ip, port);
	}

	public void executeCommand(Command command, String host, String resource, String ip, int port) {
		try {


			if (command.needsInput()) {
				System.out.println("What do you want to send? (terminate with enter)");
				BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
				//				ArrayList<String> input = new ArrayList<String>();
				String input = "";
				String inputString;
				while(!(inputString = inFromUser.readLine()).equals("")) {
					input += " " + inputString;
				}
				input = input.trim();
				outToServer.writeBytes("Content-Length: " + input.getBytes().length + "\n\n");
				outToServer.writeBytes(input+"\n");
			}
			else {
				outToServer.writeBytes("\n");
			}

			System.out.println("Written output");
			String sentence1;
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Header: \n");
			while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
				System.out.println(sentence1);
			}

			System.out.println("closing socket");
			clientSocket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "HTTP/1.0";
	}

	@Override
	public void put() {
		try{
			System.out.println("What do you want to send? (terminate with enter)");
			BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
			String input = "";
			String inputString;
			while(!(inputString = inFromUser.readLine()).equals("")) {
				input += " " + inputString;
			}
			input = input.trim();
			outToServer.writeBytes("Content-Length: " + input.getBytes().length + "\n\n");
			outToServer.writeBytes(input+"\n");

			System.out.println("Written output");
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Header: \n");
			String sentence1;
			while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
				System.out.println(sentence1);
			}

			System.out.println("closing socket");
			clientSocket.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public void get() {
		try{
			outToServer.writeBytes("\n");
			System.out.println("Written output");
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Header: \n");
			String sentence;
			String filename = "received.html";
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
			while((sentence = inFromServer.readLine()) != null && !sentence.trim().isEmpty()) {
				System.out.println(sentence);
			}
			while((sentence = inFromServer.readLine()) != null) {
				out.write(sentence);
			}
			out.close();
			
			File input = new File(filename);
			Document doc = Jsoup.parse(input, "UTF-8");
			Elements imgs = doc.select("img");
			for (int i = 0; i < imgs.size(); i++) {
				Element el = imgs.get(i);
				String src = el.attr("src");
				String outputSentence =  "GET " + src + " " + toString();
				outToServer.writeBytes(outputSentence + "\n\n");
				//TODO: ophalen image
				
			}
			
			System.out.println("Written to file: " + filename);
			System.out.println("closing socket");
			clientSocket.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}


	@Override
	public void head() {
		try{
			outToServer.writeBytes("\n");
			System.out.println("Written output");
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Header: \n");
			String sentence1;
			while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
				System.out.println(sentence1);
			}

			System.out.println("closing socket");
			clientSocket.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public void post() {
		put();
	}



}
