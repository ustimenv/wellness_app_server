
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Packet
{
    private String senderName;
    private String recipientName;
    private int senderID;
    private int recipientID;
    
    private String messageText;
    private Date creationDate;
    
    
    public Packet(String received)
    {
        StringTokenizer st = new StringTokenizer(received, "#");
        st.nextToken();                                             //skip flag
        senderID = Integer.parseInt(st.nextToken());
        recipientName = st.nextToken();
        messageText = st.nextToken();
    }
    public Packet()     //todo delete this
    {
        senderID=0;
        recipientID=0;
        senderName="Collin";
        creationDate=new Date(22);
        recipientName="Potatoman";
        messageText="is nigh";
    }
    private static String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    public String getSenderName() {
        return senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getMessageText() {
        return messageText;
    }

    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getSenderID() {
        return senderID;
    }
    
    public int getRecipientID() {
        return recipientID;
    }
    
    public void setSenderName(String name)
    {
        senderName = name;
    }
    public void setRecipientID(int id)
    {
        recipientID = id;
    }
    
}
