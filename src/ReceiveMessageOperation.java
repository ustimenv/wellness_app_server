

import java.net.InetAddress;

public class ReceiveMessageOperation implements ClientInteraction
{
	String userInput;
	InetAddress from;
	
	public ReceiveMessageOperation(String userInput, InetAddress from)
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
