
import java.io.*;
import java.net.*;

public class Server
{
	private ServerSocket serverSocket;
	private Socket receivingSocket;
	private final int ownPort;
	
	public Server()
	{
		ownPort = 50000;
		SessionKeeper sessionKeeper = SessionKeeper.INSTANCE;
		sessionKeeper.init();
		try
		{
			serverSocket = new ServerSocket(ownPort);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void listen()
	{
		ClientOperationsExecutor x = new ClientOperationsExecutor();
		while(true)
		{
			try
			{
				System.out.println("Awaiting input");
				receivingSocket = serverSocket.accept();
				System.out.println(receivingSocket.getInetAddress() + " " + receivingSocket.getLocalAddress() + " " + receivingSocket.getPort() + " " + receivingSocket .getLocalPort());
				Thread.sleep(1000);									//DEBUG ONLY, emulate network latency
				
				InputStreamReader isr = new InputStreamReader(receivingSocket.getInputStream());
				BufferedReader reader = new BufferedReader(isr);
				String line = reader.readLine();
				while (!line.isEmpty()) {
					System.out.println(line);
				line = reader.readLine(); }
				
				
//				String input = bufferedReaderToString( new BufferedReader(new InputStreamReader(receivingSocket.getInputStream())));
//				System.out.println("Received<" + input+">");
				
//				int flag = Character.getNumericValue(input.charAt(0));
//				PacketSender ms;//the actual response message is crafted during the database operation
				//TODO implement actual operations
				
//				System.out.println("RECEIVED "+input);
//				ms = x.executeOperation(new RegistrationOperation(input, receivingSocket.getInetAddress()));
//				ms.send();
				System.out.println("SENT");
//				switch (flag)
//				{
//					case 1:
//						ms = x.executeOperation(new LoginOperation(input, receivingSocket.getInetAddress()));
//						break;
//					case 2:
//						ms  = x.executeOperation(new RegistrationOperation(input, receivingSocket.getInetAddress()));
//						break;
//					case 5:
//						ms = x.executeOperation(new ReceiveMessageOperation(input, receivingSocket.getInetAddress()));
//						break;
//					default:
//						System.out.println("What is " + flag);
//						ms = new PacketSender(InetAddress.getByName("127.0.0.1"),"error");
//				}
//				if(ms.needsSending)
//				{
//					ms.send();
//				}
				
			}catch(Exception e){ e.printStackTrace();}
		}
	}
	private static String bufferedReaderToString(BufferedReader br) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		int c;
		while((c=br.read()) != -1)
		{
			sb.append((char)c);
		}
		return sb.toString();
	}
	public static void main(String[] args)
	{
		Server server = new Server();
		server.listen();
	}
}