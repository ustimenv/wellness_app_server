import java.net.InetAddress;

public class FriendOperation implements ClientInteraction
{
	String userInput;
	InetAddress from;
	
	public FriendOperation(String userInput, InetAddress from)
	{
		this.userInput = userInput;
		this.from = from;
	}
	
	@Override
	public PacketSender execute()
	{
		return SessionKeeper.INSTANCE.friendOperation(userInput, from);
	}
}
