import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {
	XMLHandler handle = null;
	private int port = 0;

	public TCPServer(int port, XMLHandler handle) {
		this.handle = handle;
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
						handle.sendFile(outStream);
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

}