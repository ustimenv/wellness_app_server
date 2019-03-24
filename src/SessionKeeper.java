import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public enum SessionKeeper
{
	INSTANCE;
	static Database database = new Database();
	
	PacketSender FAILSAFE_PACKET = new PacketSender(null, Constants.INTERNAL_ERROR);;
	public void init()
	{
		//TODO strictly for debugging
		database.init();
	}
	
	
	public PacketSender login(String input, InetAddress from)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(input, Constants.DELIMITER);
			String tmp = st.nextToken();                    //skip flag
			String username = st.nextToken();
			String password = st.nextToken();
			System.out.println(input);
			int clientID = Integer.valueOf(st.nextToken());
			Integer expectedPasswordHash = database.getPasswordHash(username, clientID);
			
			if (expectedPasswordHash == null)        //if the user does not exist
			{
				System.out.println("No such user");
				return new PacketSender(from, Constants.LOGIN_NAK, Constants.DELIMITER);
			} else if (password.hashCode() == expectedPasswordHash) {
				System.out.println("Login accepted");
				return new PacketSender(from, Constants.LOGIN_ACK);
			} else {
				System.out.println("Login denied");
				//TODO add actual number of attempts left + block further log in attemtps for some time
				return new PacketSender(from, Constants.LOGIN_NAK, Constants.DELIMITER);
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			FAILSAFE_PACKET.recipientAddress = from;
			FAILSAFE_PACKET.payload+=("|"+ Constants.LOGIN_NAK);
			return FAILSAFE_PACKET;
		}
	}
	public PacketSender register(String input, InetAddress from)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(input, Constants.DELIMITER);
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
				return new PacketSender(from, Constants.REGISTRATION_NAK, username);
			}
			else if(assignedID == -1)
			{
				FAILSAFE_PACKET.recipientAddress = from;
				FAILSAFE_PACKET.payload+=("|"+Constants.REGISTRATION_NAK);
				return FAILSAFE_PACKET;
			}
			else
			{
				return new PacketSender(from, Constants.REGISTRATION_ACK, String.valueOf(assignedID));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			FAILSAFE_PACKET.recipientAddress = from;
			FAILSAFE_PACKET.payload+=("|"+Constants.REGISTRATION_NAK);
			return FAILSAFE_PACKET;
		}
	}
	public PacketSender receivePacket(String input, InetAddress from)		//for debugging, a basic echo operation
	{
		return new PacketSender(from, input);
	}

	public PacketSender friendOperation(String input, InetAddress from)
	{
		String responseFlag = null;
		try
		{
			//first figure out what the operation is
			StringTokenizer st = new StringTokenizer(input, Constants.DELIMITER);
			String flag = st.nextToken();
			String ownUsername = st.nextToken();
			String friendUsername = st.nextToken();
			
			
			if(flag.equals(Constants.FRIEND_ADD))
				addFriend(ownUsername, friendUsername);
			else if(flag.equals(Constants.FRIEND_DEL))
				removeFriend(ownUsername, friendUsername);
			else if(flag.equals(Constants.FRIEND_ACK))
				acceptFriendRequest(ownUsername, friendUsername);
			else if(flag.equals(Constants.FRIEND_NAK))
				declineFriendRequest(ownUsername, friendUsername);
			else if(flag.equals(Constants.FRIEND_REQ_CANC))
				cancelFriendRequest(ownUsername, friendUsername);
			else
				return FAILSAFE_PACKET;
			
		} catch(Exception e)
		{
			e.printStackTrace();
			FAILSAFE_PACKET.recipientAddress = from;
			FAILSAFE_PACKET.payload+=("|"+Constants.INTERNAL_ERROR);
			return FAILSAFE_PACKET;
		}
		return null;
	}
	void addFriend(String user, String friendToAdd)		//'user' sends a friend request to 'friend', user status = FRIEND_REQUEST_SENT, friendToAdd status = FRIEND_REQUEST_RECEIVED
	{
		database.addFriend(user, friendToAdd);
	}
	void removeFriend(String user, String friendToRemove)	//'user' removes 'friend' from his friend list
	{
		database.removeFriend(user, friendToRemove);
	}
	void acceptFriendRequest(String user, String newFriend)	//'user' accepts a friend request from 'friend'
	{
		database.acceptFriendRequest(user, newFriend);
	}
	void declineFriendRequest(String user, String friendRejected)	//'user' declines a friend request from 'friend'
	{
		database.declineFriendRequest(user, friendRejected);
	}
	void cancelFriendRequest(String user, String friendNotToBe)
	{
		database.cancelFriendRequest(user, friendNotToBe);
	}
	/**Schedule operations
	 * */
	public PacketSender scheduleOperation(String input, InetAddress from)
	{
		return null;
	}
	
	
	
	
	//in case it is empty
	void updateEventSlot(String username, String day, String time, String duration, String description, String location)
	{
	
	}
	//in case the slot is already occupied
	void overwriteEventSlot(String username, String day, String time, String duration, String description, String location)
	{
	
	}
	Object getDaySchedule(String username, String day)
	{
		return new Object();
	}
	Object getWeekSchedule(String username)
	{
		return new Object();
	}
	Object getOverlappingTimesDay(String username, String friendname, String day)
	{
		//check if are freinds
		return new Object();
	}
	Object getOverlappingTimesWeek(String username, String friendname)
	{
		//check if are friends
		return new Object();
	}
	
}