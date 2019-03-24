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
		try
		{
			receivingSocket = serverSocket.accept();
			receivingSocket.setTcpNoDelay(true);
			while(true)
			{
				System.out.println("Awaiting input");
				System.out.println(receivingSocket.getInetAddress() + " " + receivingSocket.getLocalAddress() + " " + receivingSocket.getPort() + " " + receivingSocket .getLocalPort());
				Thread.sleep(1000);									//DEBUG ONLY, emulate network latency
				////////////
				InputStream is = receivingSocket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				String input = bufferedReaderToString(br);
				if(!input.isEmpty())
					System.out.println(input);
//				int flag = Character.getNumericValue(input.charAt(0));
				PacketSender ms;//the actual response message is crafted during the database operation

				//TODO implement actual operations
				
				ms = x.executeOperation(new EchoMessageOperation("WOOOHOO", InetAddress.getByName("10.53.142.162")));
				ms.send();
				System.out.println("SENT");
				String flag = String.valueOf(input.charAt(0));
				//cant use a switch statement, thats the way string switching works
				
				if(flag.equals(Constants.LOGIN_REQ))
						ms = x.executeOperation(new LoginOperation(input, receivingSocket.getInetAddress()));
				
				else if(flag.equals(Constants.REGISTRATION_REQ))
						ms  = x.executeOperation(new RegistrationOperation(input, receivingSocket.getInetAddress()));
				
				else if(flag.equals(Constants.FRIEND_ADD) || flag.equals(Constants.FRIEND_ACK) || flag.equals(Constants.FRIEND_NAK) || flag.equals(Constants.FRIEND_DEL))
						ms = x.executeOperation(new FriendOperation(input, receivingSocket.getInetAddress()));
				else
						ms = new PacketSender(InetAddress.getByName("127.0.0.1"),"error");
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static String bufferedReaderToString(BufferedReader br) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int c;
		while((c=br.read()) != -1 && c!='~')
		{
			sb.append((char)c);
		}
		System.out.println("&&");
		return sb.toString();
	}
	private static String foobar(InputStream inputStream)  throws IOException
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		if(inputStream.available()==0)
		while ((length = inputStream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		System.out.println("WOOW");
		return result.toString("UTF-8");
	}
	
	public static void main(String[] args)
	{
		Server server = new Server();
		server.listen();
	}
}