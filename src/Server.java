
import java.io.*;
import java.net.*;

/**
 * Copyright 2019, Vladimir Ustimenko, All rights reserved
 *
 */

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
				receivingSocket.setTcpNoDelay(true);
				System.out.println(receivingSocket.getInetAddress() + " " + receivingSocket.getLocalAddress() + " " + receivingSocket.getPort() + " " + receivingSocket.getLocalPort());
				Thread.sleep(1000);                                    //DEBUG ONLY, emulate network latency
				
				System.out.println("Received");
				String input = bufferedReaderToString(new BufferedReader(new InputStreamReader(receivingSocket.getInputStream())));
				System.out.println("|" + input + "|");
				String flag = input.substring(0, 1);
				PacketSender packetSender;		//the actual response message is crafted during the database operation
				if(flag.equals(Constants.LOGIN_REQ))
				{
					packetSender = x.executeOperation(new LoginOperation(input, receivingSocket.getInetAddress()));
				}
				else if(flag.equals(Constants.REGISTRATION_REQ))
				{
					packetSender  = x.executeOperation(new RegistrationOperation(input, receivingSocket.getInetAddress()));
				}
				else if(flag.equals(Constants.FRIEND_ADD) || flag.equals(Constants.FRIEND_ACK) || flag.equals(Constants.FRIEND_NAK)
						|| flag.equals(Constants.FRIEND_DEL) || flag.equals(Constants.FRIEND_REQ_CANC))
				{
					packetSender = x.executeOperation(new FriendOperation(input, receivingSocket.getInetAddress()));
				}
				//TODO schedule operation
				else
				{
					System.out.println("What is " + flag);
					packetSender = new PacketSender(InetAddress.getByName("127.0.0.1"), "error");
				}
				
				if(packetSender != null && packetSender.needsSending)
					packetSender.send();
				
				
			} catch(Exception e)
			{
				e.printStackTrace();
			}
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
	
	public static void main(String[] args) throws Exception
	{
		Server server = new Server();
		server.listen();
	}
}