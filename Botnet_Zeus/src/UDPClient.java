import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
<<<<<<< HEAD
import java.net.SocketTimeoutException;

class UDPClient {
	String IP;
	int port;

	public UDPClient(String host, int port) {
		this.IP = host;
		this.port = port;
	}

	public Object sendRequest(String request) {
		try {
			DatagramSocket clientSocket;
			clientSocket = new DatagramSocket();
			InetAddress IPAddress;
			IPAddress = InetAddress.getByName(IP);
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			sendData = request.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, port);
			// sending packet
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			// receiving packet
			clientSocket.setSoTimeout(10000);
			try {
				clientSocket.receive(receivePacket);
			} catch (SocketTimeoutException e) {
				clientSocket.close();
				return null;
			}
=======
import java.net.SocketException;
import java.net.UnknownHostException;

class UDPClient {
	String IP;
	int port;

	public UDPClient(String host, int port) {
		this.IP = host;
		this.port = port;
	}

	public Object sendRequest(String request) {
		try {
			DatagramSocket clientSocket;
			clientSocket = new DatagramSocket();
			InetAddress IPAddress;
			IPAddress = InetAddress.getByName(IP);
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			sendData = request.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, port);
			// sending packet
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			// receiving packet
			clientSocket.receive(receivePacket);
>>>>>>> branch 'master' of https://github.com/davicm/BotnetZeusP2P.git
			String modifiedSentence = new String(receivePacket.getData(), 0,
					receivePacket.getLength());
			System.out.println("FROM SERVER:" + modifiedSentence);
			clientSocket.close();
			return modifiedSentence;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
