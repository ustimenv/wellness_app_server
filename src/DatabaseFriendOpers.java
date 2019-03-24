import org.h2.jdbc.JdbcConnectionBackwardsCompat;
import org.h2.jdbcx.JdbcConnectionPool;
import org.omg.CORBA.INTERNAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

abstract public class DatabaseFriendOpers
{
	private static Integer getIdByUsername(JdbcConnectionPool connectionPool, String username)
	{
		Integer id = null;
		final String sql = "select * from CLIENTS where USERNAME=?;";
		
		try(Connection dbConnection = connectionPool.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql))
		{
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
			{
				id = rs.getInt("CLIENT_ID");
			}
			rs.close();
		}catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		return id;
	}
	static void addFriend(JdbcConnectionPool connectionPool, String username, String friendname)//username sends friend request to friendname
	{
		//check if the request is valid
		Integer userID = getIdByUsername(connectionPool, username);
		Integer friendID = getIdByUsername(connectionPool, friendname);
		if(userID == null || friendID == null)
			return;
		
		//and finally add friends
		final String insertOperation1 =  "insert into FRIENDS(CLIENT_ID, FRIEND_ID, STATUS) values(?, ?, ?);";
		final String insertOperation2 =  "insert into FRIENDS(FRIEND_ID, CLIENT_ID, STATUS) values(?, ?, ?);";
		
		try (Connection dbConnection1 = connectionPool.getConnection();
			 Connection dbConnection2 = connectionPool.getConnection();
			 PreparedStatement insertStatement1 = dbConnection1.prepareStatement(insertOperation1);
			 PreparedStatement insertStatement2 = dbConnection2.prepareStatement(insertOperation2))
		{
			insertStatement1.setInt(1, userID);
			insertStatement1.setInt(2, friendID);
			insertStatement1.setInt(3, Constants.FRIEND_REQUEST_SENT);
			
			insertStatement2.setInt(1, friendID);
			insertStatement2.setInt(2, userID);
			insertStatement2.setInt(3, Constants.FRIEND_REQUEST_RECEIVED);
			
			insertStatement1.executeUpdate();
			insertStatement2.executeUpdate();
			
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	static void cancelFriendRequest(JdbcConnectionPool connectionPool, String username, String friendname)//username cancel friend request previosly sent to friendname
	{
		//check if the request is valid
		Integer userID = getIdByUsername(connectionPool, username);
		Integer friendID = getIdByUsername(connectionPool, friendname);
		if(userID == null || friendID == null)
			return;
		
		//and finally cancel friend req
		final String removeOperation1 =  "remove * from FRIENDS where CLIENT_ID=? and FRIEND_ID=? and STATUS=?";
		final String removeOperation2 =  "remove * from FRIENDS where CLIENT_ID=? and FRIEND_ID=? and STATUS=?";
		
		try (Connection dbConnection1 = connectionPool.getConnection();
			 Connection dbConnection2 = connectionPool.getConnection();
			 PreparedStatement removeStatement1 = dbConnection1.prepareStatement(removeOperation1);
			 PreparedStatement removeStatement2 = dbConnection2.prepareStatement(removeOperation2))
		{
			removeStatement1.setInt(1, userID);
			removeStatement1.setInt(2, friendID);
			removeStatement1.setInt(3, Constants.FRIEND_REQUEST_SENT);
			
			removeStatement2.setInt(1, friendID);
			removeStatement2.setInt(2, userID);
			removeStatement2.setInt(3, Constants.FRIEND_REQUEST_RECEIVED);
			
			removeStatement1.executeUpdate();
			removeStatement2.executeUpdate();
			
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	static void removeFriend(JdbcConnectionPool connectionPool, String username, String friendname)//username removes friendname
	{
		//check if the request is valid
		Integer userID = getIdByUsername(connectionPool, username);
		Integer friendID = getIdByUsername(connectionPool, friendname);
		if(userID == null || friendID == null)
			return;
		
		//and finally remove friends
		final String removeOperation =  "delete from FRIENDS where CLIENT_ID=?;"+
										"delete from FRIENDS where CLIENT_ID=?;";
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement removeStatement = dbConnection.prepareStatement(removeOperation))
		{
			removeStatement.setInt(1, userID);
			removeStatement.setInt(2, friendID);
			removeStatement.executeUpdate();
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	static void acceptFriendRequest(JdbcConnectionPool connectionPool, String username, String friendname)	///username accepts a pending friend request from friendname
	{
		//check if the request is valid
		Integer userID = getIdByUsername(connectionPool, username);
		Integer friendID = getIdByUsername(connectionPool, friendname);
		if(userID == null || friendID == null)
			return;
		HashMap <Integer, Integer> currentFriendshipStatus = getFriendshipStatus(connectionPool, userID, friendID);
		if(currentFriendshipStatus == null || currentFriendshipStatus.values().contains(1))	//if no record of a friend request exists or already friends, do nothing
			return;
		
		//and finally add friends
		final String updateOper1 = "update FRIENDS set STATUS=? where CLIENT_ID=?;";
		final String updateOper2 = "update FRIENDS set STATUS=? where CLIENT_ID=?;";

		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement updateStatement1 = dbConnection.prepareStatement(updateOper1);
			 PreparedStatement updateStatement2 = dbConnection.prepareStatement(updateOper2);)
		{
			updateStatement1.setInt(1, Constants.FRIEND_NORMAL);
			updateStatement1.setInt(2, userID);
			
			updateStatement2.setInt(1, Constants.FRIEND_NORMAL);
			updateStatement2.setInt(2, friendID);

			updateStatement1.executeUpdate();
			updateStatement2.executeUpdate();
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	static void declineFriendRequest(JdbcConnectionPool connectionPool, String username, String friendname)//username cancel friend request previosly sent to friendname
	{
		//check if the request is valid
		Integer userID = getIdByUsername(connectionPool, username);
		Integer friendID = getIdByUsername(connectionPool, friendname);
		if(userID == null || friendID == null)
			return;
		
		//and finally cancel friend req
		final String removeOperation1 =  "remove * from FRIENDS where CLIENT_ID=? and FRIEND_ID=? and STATUS=?";
		final String removeOperation2 =  "remove * from FRIENDS where CLIENT_ID=? and FRIEND_ID=? and STATUS=?";
		
		try (Connection dbConnection1 = connectionPool.getConnection();
			 Connection dbConnection2 = connectionPool.getConnection();
			 PreparedStatement removeStatement1 = dbConnection1.prepareStatement(removeOperation1);
			 PreparedStatement removeStatement2 = dbConnection2.prepareStatement(removeOperation2))
		{
			removeStatement1.setInt(1, userID);
			removeStatement1.setInt(2, friendID);
			removeStatement1.setInt(3, Constants.FRIEND_REQUEST_RECEIVED);
			
			removeStatement2.setInt(1, friendID);
			removeStatement2.setInt(2, userID);
			removeStatement2.setInt(3, Constants.FRIEND_REQUEST_SENT);
			
			removeStatement1.executeUpdate();
			removeStatement2.executeUpdate();
			
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	/**If table looks like this (meaning A sent a friend request to B and is now awaiting confirmation, and is also friends with C
	 * clientID(A)    clientID(B)    2
	 * clientID(A)    clientID(C)    1
	 *
	 * Returned Map would be:
	 * clientID(B) -> 2
	 * clientID(C) -> 1
	 * */
	
	static HashMap<Integer, Integer> getFriends(JdbcConnectionPool connectionPool, Integer userID)
	{
		final String getFriends = "select * from FRIENDS where CLIENT_ID=?;";
		HashMap<Integer, Integer> friends = new HashMap<>();
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement getFriendsStatement = dbConnection.prepareStatement(getFriends);)
		{
			getFriendsStatement.setInt(1, userID);
			
			ResultSet friendList = getFriendsStatement.executeQuery();
			
			while(friendList.next())
			{
				friends.put(friendList.getInt("FRIEND_ID"),friendList.getInt("STATUS") );
			}
			friendList.close();
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
		return friends;
	}
	
	
	/**If table looks like this (meaning A sent a friend request to B and is now awaiting confirmation
	 * clientID(A)    clientID(B)    2
	 * clientID(B)    clientID(A)    3
	 *
	 * Returned Map would be:
	 * clientID(A) -> 2
	 * clientID(B) -> 3
	 * */
	
	static HashMap<Integer, Integer> getFriendshipStatus(JdbcConnectionPool connectionPool, Integer userID, Integer friendID)
	{
		final String getUserSide   = "select * from FRIENDS where CLIENT_ID=? AND FRIEND_ID=?;";
		final String getFriendSide = "select * from FRIENDS where CLEINT_ID=? AND FRIEND_ID=?;";
		
		HashMap<Integer, Integer> friendshipSummary = new HashMap<>();
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement getUserSideStatement = dbConnection.prepareStatement(getUserSide);
			 PreparedStatement getFriendSideStatement = dbConnection.prepareStatement(getFriendSide))
		{
			getUserSideStatement.setInt(1, userID);
			getUserSideStatement.setInt(2, friendID);
			
			getFriendSideStatement.setInt(1, friendID);
			getFriendSideStatement.setInt(2, userID);
			
			ResultSet userSide = getUserSideStatement.executeQuery();
			ResultSet friendSide = getFriendSideStatement.executeQuery();
			
			if(userSide.next())
			{
				friendshipSummary.put(userID, userSide.getInt("STATUS"));
			}
			userSide.close();
			if(friendSide.next())
			{
				friendshipSummary.put(friendID, friendSide.getInt("STATUS"));
			}
			friendSide.close();
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
		if(friendshipSummary.keySet().size()<2)//must be 2, better have a proper null pointer than a 1 element disaster
			return null;
		else
			return friendshipSummary;
	}
}
