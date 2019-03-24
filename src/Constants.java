import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract  class Constants
{
	
	public final static String DELIMITER		 = Integer.toString(13);	//separate distinct units of data within the message
	public final static String INTERNAL_ERROR    = Integer.toString(20);	//hopefully never appears
	public final static String DATA_RECEIVED_ACK = Integer.toString(21);//confirm client successfully transmitted their data
	
	//May receive
	
	public final static String REGISTRATION_REQ  = Integer.toString(1);//client wishes to create a profile
	public final static String LOGIN_REQ 	     = Integer.toString(2);//client is attempting to log in
	
	
	public final static String FRIEND_ADD		 = Integer.toString(3);//user sends a friend a request
	public final static String FRIEND_ACK    	 = Integer.toString(4);//user accepts friend's request
	public final static String FRIEND_NAK    	 = Integer.toString(5);//user rejects friend's request
	public final static String FRIEND_DEL        = Integer.toString(6);//user removes friend
	public final static String FRIEND_REQ_CANC   = Integer.toString(7);//a user wishes to cancel a previously sent friend request
	
	//May send/////////////////////////////////////////////////////////////////////////////////////////////////////
	public final static String REGISTRATION_ACK  = Integer.toString(1);//profile created
	public final static String  REGISTRATION_NAK = Integer.toString(2);//profile not created
	
	public final static String LOGIN_ACK 		 = Integer.toString(3);//logged in successfully
	public final static String LOGIN_NAK 		 = Integer.toString(4);//login failed
	
	
	public final static String FRIEND_ADD_REQUEST   	= Integer.toString(5);//tell a friend that user wishes to be friends with them
	public final static String FRIEND_REQUEST_ACCEPTED  = Integer.toString(6);//tell friend user accepted their friend request
	public final static String FRIEND_REQUEST_DENIED	= Integer.toString(7);//tell user they were rejected
	public final static String FRIEND_REMOVE        	= Integer.toString(8);//tell the friend user removed them
	public final static String FRIEND_REQUEST_CANCEL    = Integer.toString(9);//tell a friend that the user changed their mind???????????
	
	
	//Friend status codes
	public final static int FRIEND_NOT = 0;				//not friend?????
	public final static int FRIEND_NORMAL = 1;			//full on friend
	public final static int FRIEND_REQUEST_SENT = 2;	//user is waiting to be accepted
	public final static int FRIEND_REQUEST_RECEIVED = 3;//user may now accept friend request
	
	
	
	/* eg
	*A sends B a friend req:
	*A->B status is set to 2
	* B->A status is set to 3
	* when B accepts, both set to 1
	* */
}
