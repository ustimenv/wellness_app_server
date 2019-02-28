

import java.net.InetAddress;

public class RegistrationOperation implements ClientInteraction
{
	String userInput;
	InetAddress from;
	
	public RegistrationOperation(String userInput, InetAddress from)
	{
		this.userInput = userInput;
		this.from = from;
	}
	
	@Override
	public PacketSender execute()
	{
		return SessionKeeper.INSTANCE.register(userInput, from);
	}
	
}
