import java.net.InetAddress;

public class ScheduleOperation implements ClientInteraction
{
	String userInput;
	InetAddress from;
	
	public ScheduleOperation(String userInput, InetAddress from)
	{
		this.userInput = userInput;
		this.from = from;
	}
	
	@Override
	public PacketSender execute()
	{
		return SessionKeeper.INSTANCE.scheduleOperation (userInput, from);
	}
}
