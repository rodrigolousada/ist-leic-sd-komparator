package pt.ulisboa.tecnico.sdis.ws.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CAClientApp {

	private static final String APP_NAME = "ca-ws-cli";

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: %s wsURL OR uddiURL wsName%n", APP_NAME);
			return;
		}
		String wsURL = null;
		String uddiURL = null;
		String wsName = null;
		if (args.length == 1) {
			wsURL = args[0];
		} else if (args.length >= 2) {
			uddiURL = args[0];
			wsName = args[1];
		}

		// Create client
		CAClient client = null;

		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new CAClient(wsURL);
		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n", uddiURL, wsName);
			client = new CAClient(uddiURL, wsName);
		}

		// command loop
		Scanner keyboardSc;
		keyboardSc = new Scanner(System.in);

		do {
			System.out.println("Please type GET <certificateName> or QUIT");

			String command = keyboardSc.nextLine();
			String commandArgs[] = command.split(" ");

			if ("QUIT".equalsIgnoreCase(commandArgs[0]) || "Q".equalsIgnoreCase(commandArgs[0]))
				break;

			if (commandArgs.length <= 1)
				continue;

			if ("GET".equalsIgnoreCase(commandArgs[0]) || "G".equalsIgnoreCase(commandArgs[0])) {
				for (int i = 1; i < commandArgs.length; i++) {
					String certName = commandArgs[i];
					try {
						System.out.printf("Retrieving certificate %s from server%n", certName);
						String result = client.getCertificate(certName);
                        if (result == null) {
                            System.out.println("Certificate not found!");
                            continue;
                        }
						System.out.printf("Received %d characters%n", result.length());
						String fileName = certName + ".cer";
						System.out.printf("Writing to file %s%n", fileName);
						writeFile(fileName, result);

					} catch (Exception e) {
						String message = e.getMessage();
						message = (message == null ? "" : message);
						System.out.printf("Error: %s %s%n", e.getClass().getSimpleName(), message);
					}
				}
			}
		} while (true);

		keyboardSc.close();
	}

	private static void writeFile(String fileName, String result) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

		bw.write(result);
		bw.close();
	}

}
