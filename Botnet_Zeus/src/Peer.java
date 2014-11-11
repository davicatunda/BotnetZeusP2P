import java.util.HashMap;
import java.util.Scanner;

public class Peer {
	int port;
	String host;
	TCPClient tcp;
	UDPClient udp;

	public Peer(String port, String host) {
		this.port = Integer.valueOf(port);
		this.tcp = new TCPClient(host, this.port);
		this.udp = new UDPClient(host, this.port);
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public int getVersion() {
		Object obj = udp.sendRequest("getVersion");
		if (obj == null) // it is NOT connected
			return -1;
		return Integer.valueOf((String) obj);
	}

	public Object getFile() {
		return tcp.sendRequest("getConfFile");
	}

	public HashMap<Integer, Peer> getPeerList() {
		HashMap<Integer, Peer> peerList = new HashMap<Integer, Peer>();
		String response = (String) udp.sendRequest("peerListRequest");
		if(response.equals("peerListIsEmpty"))
			return null;
		Scanner in = new Scanner(response);
		while (in.hasNext()) {
			int id = in.nextInt();
			String port = in.next();
			String host = in.next();
			peerList.put(id, new Peer(port, host));
		}
		in.close();
		return peerList;
	}

}