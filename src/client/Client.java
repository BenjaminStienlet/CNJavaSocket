package client;

import support.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
* A HTPP/1.0 and HTPP/1.1 client
*
* Arguments: 		HTTPCommand URI Port HTTPversion
* - HTTPCommand: 	HEAD, GET, PUT or POST
* - URI: 			uniform resource identifier, e.g. www.google.com
* - Port: 			the port number
* - HTTPversion: 	HTTP/1.0 or HTTP/1.1
*
* @author Tom Stappaerts, Benjamin Stienlet
*/
public class Client {

	public static void main(String[] args) {
		boolean exit = false;
		int errorCounter = 0;
		while(exit != true && errorCounter <10) {
			System.out.println("Input (x for exit): ");
			try {
				BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
				String sentence = inFromUser.readLine();
				if(sentence.equalsIgnoreCase("x".trim())) {
					exit = true;
				} else {
					Client.command(sentence);
				}
			}catch (Exception e) {
				System.out.println("Something went wrong. Try again.");
				System.out.println("Error: " + e.getMessage());
				errorCounter++;
			}
		}
		System.out.println("Exit");
	}

	/**
	 * Parses and executes the given command.
	 *
	 * @param 	input
	 * 			The command to execute
	 * @throws 	IllegalArgumentException
	 * 			Thrown when the given string does not follow the specified format
	 * @throws 	UnknownHostException
	 * 			(See parseURI)
	 */
	public static void command(String input) throws IllegalArgumentException, UnknownHostException {
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

		//Command
		Command command = null;
		for(Command c : Command.values())
			if(commandInput.equalsIgnoreCase(c.toString())) command = c;
		if(command == null) throw new IllegalArgumentException();
		//Port
		if(port <= 0) throw new IllegalArgumentException();

		String[] parsed = parseURI(uri);
		String host = parsed[0];
		String resource = parsed[1];
		String ip = parsed[2];

		//HTTP version
		if(versionNumber.equals("1.0") || versionNumber.equalsIgnoreCase("http/1.0"))
			new Http10(command,host,resource,ip,port);
		else if(versionNumber.equals("1.1") || versionNumber.equalsIgnoreCase("http/1.1"))
			new Http11(command,host,resource,ip,port);
		else
			throw new IllegalArgumentException();

	}

	/**
	 * Parses the given uri and returns the host and the ip- and resource-address.
	 * 
	 * @result	result[0] = host
	 * 			result[1] = resource
	 * 			result[2] = ip
	 * @throws	IllegalArgumentException
	 * 			Thrown when the given string does not follow the format of a uri.
	 * @throws	UnknownHostException
	 * 			Thrown when no IP address for the host could be found, 
	 * 			or when a scope_id was specified for a global IPv6 address.
	 */
	public static String[] parseURI(String uri) throws IllegalArgumentException, UnknownHostException {
		String[] result = new String[3];
		String ipRegex = "^((\\d{1,3}\\.){3}\\d{1,3})(/\\S*)?$";
		String hostRegex1 = "^(http:\\/\\/)?(www\\.)?([^\\/]*)$";
		String hostRegex2 = "^(http:\\/\\/)?(www\\.)?([^\\/]*)(/\\S*)$";

		if(uri.matches(ipRegex)) {
			result[2] = uri.replaceAll(ipRegex, "$1");
			result[1] = uri.replaceAll(ipRegex, "$3");
			result[0] = result[2];
		}
		else if(uri.matches(hostRegex1)) {
			result[0] = uri.replaceAll(hostRegex1, "$3");
			result[1] = "/";
			result[2] = InetAddress.getByName(result[0]).getHostAddress();
		}
		else if(uri.matches(hostRegex2)) {
			result[0] = uri.replaceAll(hostRegex2, "$3");
			result[1] = uri.replaceAll(hostRegex2, "$4");
			result[2] = InetAddress.getByName(result[0]).getHostAddress();
		}
		else {
			throw new IllegalArgumentException();
		}
		return result;
	}

}


