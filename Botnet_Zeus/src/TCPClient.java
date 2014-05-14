import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
 
public class TCPClient {
	String IP;
	int port;
	public TCPClient (String IP, int port){
		this.IP=IP;
		this.port=port;
	}

    public Object sendRequest(String request){
        ObjectOutputStream outStream;
        ObjectInputStream inStream;
        Socket connection;
        String message = "";
        Object obj = null;
        try {
            connection = new Socket(IP, port);
            System.out.println("Conected to server " + IP + ", on port: " + port);
            
            // making in and out connections
            outStream = new ObjectOutputStream(connection.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(connection.getInputStream());
 
            //reading server message
            message = (String) inStream.readObject();
            System.out.println("Server>> "+message);
 
            outStream.writeObject(request);
            outStream.flush();
            
            //reading message from server
            switch (request) {
			case "getVersion":
				obj =  inStream.readObject();	
				break;
			case "getConfFile":
				byte[] aByte = new byte[1];
            	int bytesRead;
            	
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bytesRead = inStream.read(aByte, 0, aByte.length);
                    do {
                            baos.write(aByte);
                            bytesRead = inStream.read(aByte);
                    } while (bytesRead != -1);
            	obj = baos;
				break;
			default:
				obj =  inStream.readObject();	
				break;
			}
            //outStream.close();
           // inStream.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("error: " + e.toString());
        }
		return obj;
    }
 
}