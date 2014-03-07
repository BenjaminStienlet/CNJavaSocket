package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

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
			String filename = null;
			String contentType = null;

			//Getting headers.
			while((sentence = inFromServer.readLine()) != null && !sentence.trim().isEmpty()) {
				if(sentence.startsWith("Content-Type:")) {
					contentType = sentence.split("Content-Type: ")[1].trim();
				}
				System.out.println(sentence);
			}
			//Getting file information. 2 possibilities: html or image. Others are not supported.
			if(contentType == "text/html") {
				filename = "received.html";
				BufferedWriter outToFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
				while((sentence = inFromServer.readLine()) != null) {
					outToFile.write(sentence);
				}
				outToFile.close();
				System.out.println("Written to file: " + filename);
				System.out.println("closing socket");
				clientSocket.close();

				//Getting the images out of the html.
				File input = new File(filename);
				Document doc = Jsoup.parse(input, "UTF-8");
				Elements imgs = doc.select("img");
				for (int i = 0; i < imgs.size(); i++) {
					Element el = imgs.get(i);
					String uri = el.attr("src");

					String[] parsed = Client.parseURI(uri);
					String hostImg = parsed[0];
					String resourceImg = parsed[1];
					String ipImg = parsed[2];
					//Getting images.
					Http10 getImage = new Http10(Command.GET, hostImg, resourceImg, ipImg, this.port);
				}

			} else if(contentType.startsWith("image")) {
				String imageType = contentType.split("/")[1].trim();
				if(imageType == "jpeg" || imageType == "png" || imageType == "gif") {
					//making new unexisting filename for the image. Limit on 100 for safety purposes.
					for(int i = 0;i<100;i++) {
						filename = "received"+i+"."+imageType;
						File f = new File(filename);
						if(f.exists() && !f.isDirectory()) {
							break;
						}
					}
					String receivedFileString = "";
					while((sentence = inFromServer.readLine()) != null) {
						receivedFileString += sentence.trim();
					}
					byte[] bytes = receivedFileString.getBytes(Charset.forName("UTF-8"));
					FileOutputStream fos = new FileOutputStream(filename);
					fos.write(bytes);
					fos.close();
					System.out.println("Image written to: " + filename);			
				} else {
					System.out.println("This client does not support the image type: "+imageType);
				}
				
			} else  {
				System.out.println("Content-type: " + contentType + " Not implemented.");
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
