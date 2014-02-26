import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Test {

	@SuppressWarnings("resource")

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		String uri = "213.239.154.20";
		int port = 80;
		Socket clientSocket = new Socket(uri,port);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		System.out.println(clientSocket.toString());
		
		outToServer.writeBytes("GET / HTTP/1.1 \r\n");
		System.out.println(clientSocket.toString());


		String fromServer;
		while((fromServer = inFromServer.readLine()) != null) {
			System.out.println(fromServer);
		}
		
		
	}
	
}
