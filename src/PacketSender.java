

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class PacketSender
{
	boolean needsSending = true;			//if the IP address of the client is not known store the message instead of sending it immediatedly
	String payload = "";
	InetAddress recipientAddress;
	
	public PacketSender(InetAddress recipientAddress, String... contents)
	{
		this.recipientAddress = recipientAddress;
		StringBuilder sb = new StringBuilder();
		for(String s : contents)
		{
			sb.append(s);
			sb.append(Constants.DELIMITER);
		}
		payload = sb.toString();
	}
	
	void send()
	{
		try(Socket sendingSocket = new Socket(recipientAddress, 8080);
			PrintWriter os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sendingSocket.getOutputStream()))))
		{
			System.out.println("Sending");
			os.write(payload);
			os.flush();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void setNeedsSending(boolean needsSending)
	{
		this.needsSending = needsSending;
	}
}
