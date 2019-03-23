
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Packet
{
    private String senderName;
    private int senderID;
    private String payload;
    
    Packet(String received)
    {
        StringTokenizer st = new StringTokenizer(received, "#");
        st.nextToken();                                             //skip flag
        senderID = Integer.parseInt(st.nextToken());
        payload = st.nextToken();
    }
    Packet()     //todo delete this
    {
        senderID=0;
        senderName="Collin";
        payload="is nigh";
    }
    private static String dateToString(Date date)
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    public String getSenderName()
    {
        return senderName;
    }

    public String getPacketPayload()
    {
        return payload;
    }
    public int getSenderID()
    {
        return senderID;
    }
    
}
