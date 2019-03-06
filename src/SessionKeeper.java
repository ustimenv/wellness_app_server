import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public enum SessionKeeper
{
	INSTANCE;
	private String LOGIN_ACK = "1";
	private String LOGIN_NAK = "2";
	private String REGISTRATION_ACK = "3";
	private String REGISTRATION_NAK = "4";
	private String MESSAGE_FLAG = "5";
	
	private Map<Integer, InetAddress> routingTable = new ConcurrentHashMap<>();
	private Map<String, Integer> nameMappings = new ConcurrentHashMap<>();
	private static String DELIMITER = "#";
	
	static Database database = new Database();
	
	public void init()
	{
		// database.initTest();
	}
	
	public void addClient(String name, int clientID, String IP)
	{
		try
		{
			addClient(name, clientID, InetAddress.getByName(IP));
		}catch(UnknownHostException e){System.out.println("Unable to resolve" + IP);}
	}
	
	public void addClient(String name, int clientID, InetAddress associatedIP)
	{
		routingTable.put((int) clientID, associatedIP);
		nameMappings.put(name, (int) clientID);
	}
	
	public InetAddress getAddress(String name)
	{
		Integer x = nameMappings.get(name);
		if(x!=null)
		{
			return getAddress(x);
		}
		return null;
	}
	
	public InetAddress getAddress(int clientID)
	{
		return routingTable.get((int)clientID);
	}
	
	
	public PacketSender login(String input, InetAddress from)
	{
		
		StringTokenizer st = new StringTokenizer(input, DELIMITER);
		String tmp  = st.nextToken();					//skip flag
		String username = st.nextToken();
		String password = st.nextToken();
		System.out.println(input);
		int clientID = Integer.valueOf(st.nextToken());
		Integer expectedPasswordHash = database.getPasswordHash(username, clientID);
		
		if(expectedPasswordHash == null)		//if the user does not exist
		{
			System.out.println("BOOm");
			return new PacketSender(from, LOGIN_NAK, "9");
		}
		
		else if(password.hashCode() == expectedPasswordHash)
		{
			System.out.println("Login accepted");
			return new PacketSender(from, LOGIN_ACK);
		}
		else
		{
			System.out.println("Login denied");
			//TODO add actual number of attempts left + block further log in attemtps for some time
			return new PacketSender(from, LOGIN_NAK, "4");
		}
		
	}
	
	public PacketSender register(String input, InetAddress from)
	{
		StringTokenizer st = new StringTokenizer(input, DELIMITER);
		String tmp = st.nextToken();					//skip flag
		String username = st.nextToken();
		String password = st.nextToken();
		
		System.out.println("Registering " + tmp + " " + username + " " + password);
		//TODO write own hash function
		int passwordHash = password.hashCode();
		
		int assignedID = database.registerClient(username, passwordHash, from.toString());	//add client to the database
		
		if(assignedID == -1)																//registration failed, most likely because username is taken
		{
			return new PacketSender(from, REGISTRATION_NAK, username);
		}
		else
		{
			addClient(username, assignedID, from);                                          //add client to cached routing table
			return new PacketSender(from,  REGISTRATION_ACK, String.valueOf(assignedID));
		}
	}
	
	public PacketSender receiveMessage(String input, InetAddress from)
	{
		System.out.println("Received message " + input);
		Packet msg = new Packet(input);   //at this point we know senderID and recipientName
		
		
		InetAddress recipientAddress = getAddress(msg.getRecipientName());
		PacketSender ms = new PacketSender(recipientAddress, MESSAGE_FLAG, String.valueOf(msg.getSenderID()), msg.getRecipientName(), msg.getMessageText());
		if(recipientAddress == null)    //if the client is not in the cached routing table, check the database
		{
			ms.setNeedsSending(false);  //recipient is currently unknown to the system, store the message and send it later
		}
		return ms;
	}
	public PacketSender receivePacket(String input, InetAddress from)//for debugging, a basic echo operation
	{
		return  new PacketSender(from, input);
	}
}