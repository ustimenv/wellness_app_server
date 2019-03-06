import java.net.InetAddress;

public class EchoMessageOperation implements ClientInteraction
{
	String userInput;
	InetAddress from;
	
	public EchoMessageOperation(String userInput, InetAddress from)
	{
		this.userInput = userInput;
		this.from = from;
	}
	
	@Override
	public PacketSender execute()
	{
		return SessionKeeper.INSTANCE.receivePacket(userInput, from);
	}
}
