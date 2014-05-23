import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class UDPServer extends Thread {
	int port;
	XMLHandler handle;

	public UDPServer(int port, XMLHandler handle) {
		this.port = port;
		this.handle = handle;
	}

	public void run() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];

			while (sendData != receiveData) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				// It will be stuck here until some client connect
				serverSocket.receive(receivePacket);

				String request = new String(receivePacket.getData(), 0,
						receivePacket.getLength());
				String response = "";
				switch (request) {
				case "peerListRequest":
					response = sendPeerList();
					break;
				case "getVersion":
					response = "" + handle.getVersion();
					break;
				default:
					System.out.println("Cliente>> " + request);
					response = request;
					break;
				}

				System.out.println("RECEIVED: " + request);
				InetAddress IPAddress = receivePacket.getAddress();
				sendData = response.getBytes();

				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, IPAddress, receivePacket.getPort());
				serverSocket.send(sendPacket);

			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String sendPeerList() {
		String response = "";
		HashMap<Integer, Peer> updatedPeerList = handle.getUpdatedPeerList();
		if (updatedPeerList == null)
			return "peerListIsEmpty";
		for (Map.Entry<Integer, Peer> entry : updatedPeerList.entrySet()) {
			Peer peer = entry.getValue();
			int id = entry.getKey();
			response += id + " " + peer.port + " " + peer.host + "\n";
		}
		return response;

	}
}