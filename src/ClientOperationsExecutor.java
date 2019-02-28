

public class ClientOperationsExecutor
{
	public PacketSender executeOperation(ClientInteraction clientInteraction)
	{
		return clientInteraction.execute();
	}
}
