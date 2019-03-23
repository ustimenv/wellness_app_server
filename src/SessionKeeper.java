import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public enum SessionKeeper
{
	INSTANCE;
	static Database database = new Database();
	
	final String REGISTRATION_ACK  = Integer.toString(1);
	final String REGISTRATION_NAK  = Integer.toString(2);
	final String LOGIN_ACK 		   = Integer.toString(3);
	final String LOGIN_NAK 		   = Integer.toString(4);
	
	final String DATA_RECEIVED_ACK = Integer.toString(5);
	final String INTERNAL_ERROR    = Integer.toString(6);

	final String FRIEND_REQ		   = Integer.toString(7);
	final String FRIEND_ACK    	   = Integer.toString(8);
	final String FRIEND_NAK    	   = Integer.toString(9);
	final String FRIEND_DEL        = Integer.toString(10);
	
	final String DELIMITER		   = Integer.toString(13);
	
	PacketSender FAILSAFE_PACKET;
	
	public void init()
	{
		//TODO strictly for debugging
		database.init();
		FAILSAFE_PACKET = new PacketSender(null, INTERNAL_ERROR);
	}
	
	
	public PacketSender login(String input, InetAddress from)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(input, DELIMITER);
			String tmp = st.nextToken();                    //skip flag
			String username = st.nextToken();
			String password = st.nextToken();
			System.out.println(input);
			int clientID = Integer.valueOf(st.nextToken());
			Integer expectedPasswordHash = database.getPasswordHash(username, clientID);
			
			if (expectedPasswordHash == null)        //if the user does not exist
			{
				System.out.println("No such user");
				return new PacketSender(from, LOGIN_NAK, DELIMITER);
			} else if (password.hashCode() == expectedPasswordHash) {
				System.out.println("Login accepted");
				return new PacketSender(from, LOGIN_ACK);
			} else {
				System.out.println("Login denied");
				//TODO add actual number of attempts left + block further log in attemtps for some time
				return new PacketSender(from, LOGIN_NAK, DELIMITER);
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			FAILSAFE_PACKET.recipientAddress = from;
			FAILSAFE_PACKET.payload+=("|"+LOGIN_NAK);
			return FAILSAFE_PACKET;
		}
	}
	public PacketSender register(String input, InetAddress from)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(input, DELIMITER);
			String tmp = st.nextToken();                    //skip flag
			String username = st.nextToken();
			String name = st.nextToken();
			String password = st.nextToken();
			System.out.println("Registering " + tmp + " " + username + " |" + name + " " + password);
			
			//TODO write own hash function
			int passwordHash = password.hashCode();
			
			Integer assignedID = database.register(username, name, Integer.parseInt(password));    //add client to the database
			
			if (assignedID == -2)                                                                //registration failed, most likely because username is taken
			{
				return new PacketSender(from, REGISTRATION_NAK, username);
			}
			else if(assignedID == -1)
			{
				FAILSAFE_PACKET.recipientAddress = from;
				FAILSAFE_PACKET.payload+=("|"+REGISTRATION_NAK);
				return FAILSAFE_PACKET;
			}
			else
			{
				return new PacketSender(from, REGISTRATION_ACK, String.valueOf(assignedID));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			FAILSAFE_PACKET.recipientAddress = from;
			FAILSAFE_PACKET.payload+=("|"+REGISTRATION_NAK);
			return FAILSAFE_PACKET;
		}
	}
	public PacketSender receivePacket(String input, InetAddress from)		//for debugging, a basic echo operation
	{
		return new PacketSender(from, input);
	}

	public PacketSender friendOperation(String input, InetAddress from)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(input, DELIMITER);
			String tmp = st.nextToken();                    //skip flag
			String username = st.nextToken();
			String password = st.nextToken();
			System.out.println(input);
			int clientID = Integer.valueOf(st.nextToken());
			Integer expectedPasswordHash = database.getPasswordHash(username, clientID);
			
			if (expectedPasswordHash == null)        //if the user does not exist
			{
				System.out.println("No such user");
				return new PacketSender(from, LOGIN_NAK, DELIMITER);
			} else if (password.hashCode() == expectedPasswordHash) {
				System.out.println("Login accepted");
				return new PacketSender(from, LOGIN_ACK);
			} else {
				System.out.println("Login denied");
				//TODO add actual number of attempts left + block further log in attemtps for some time
				return new PacketSender(from, LOGIN_NAK, DELIMITER);
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			FAILSAFE_PACKET.recipientAddress = from;
			FAILSAFE_PACKET.payload+=("|"+INTERNAL_ERROR);
			return FAILSAFE_PACKET;
		}
	}
	
	
}