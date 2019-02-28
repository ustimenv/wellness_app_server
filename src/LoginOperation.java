
import java.net.InetAddress;

public class LoginOperation implements ClientInteraction
{
	String userInput;
	InetAddress from;
	
	public LoginOperation(String userInput, InetAddress from)
	{
		this.userInput = userInput;
		this.from = from;
	}
	
	@Override
	public PacketSender execute()
	{
		return SessionKeeper.INSTANCE.login(userInput, from);
	}
	
	
	
}
