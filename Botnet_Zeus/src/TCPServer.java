import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {
	String fileToSend = null;
	private int port = 0;

	public TCPServer(int port, XMLHandler handle) {
		this.fileToSend = handle.malwarePath;
		this.port = port;
	}

	public void run() {
		ObjectOutputStream outStream;
		ObjectInputStream inStream;
		boolean connectionClosed = false;
		String message = "";

		try {
			ServerSocket server = new ServerSocket(port, 10);
			Socket connection;
			while (!connectionClosed) {
				System.out.println("Listen to port: " + port);

				// It will be stuck here until some client connect
				connection = server.accept();

				System.out.println("Connection stabilished with: "
						+ connection.getInetAddress().getHostAddress());

				// get input stream and output stream
				outStream = new ObjectOutputStream(connection.getOutputStream());
				inStream = new ObjectInputStream(connection.getInputStream());

				// sending message to client
				outStream.writeObject("Connection stabilished...\n");

				try {
					// getting client message
					message = (String) inStream.readObject();

					switch (message) {
					case "getConfFile":
						sendFile(outStream);
						break;
					default:
						System.out.println("Cliente>> " + message);
						outStream.writeObject(message);
						break;
					}
				} catch (IOException iOException) {
					System.err.println("error: " + iOException.toString());
				}

				System.out.println("Connection closed by client");
				outStream.close();
				inStream.close();
				connection.close();
			}
			server.close();
		} catch (Exception e) {
			System.err.println("Erro: " + e.toString());
		}
	}

	private void sendFile(ObjectOutputStream outStream) {
		File myFile = new File(fileToSend);
		byte[] mybytearray = new byte[(int) myFile.length()];

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(myFile);
		} catch (FileNotFoundException ex) {
			// Do exception handling
		}
		BufferedInputStream bis = new BufferedInputStream(fis);

		try {
			bis.read(mybytearray, 0, mybytearray.length);
			outStream.write(mybytearray, 0, mybytearray.length);
			outStream.flush();
		} catch (IOException ex) {
			// Do exception handling
		}
	}
}