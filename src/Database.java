
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;
import org.h2.jdbcx.JdbcConnectionPool;

import java.net.InetAddress;
import java.sql.*;
import java.util.*;

import org.h2.jdbcx.JdbcConnectionPool;

import java.net.InetAddress;
import java.sql.*;
import java.util.Date;

class Database
{
	private static final String DB_URL = "jdbc:h2:tcp://localhost/~/DB";
	private JdbcConnectionPool connectionPool;
	
	Database()
	{
		System.out.println("Established connection");
		connectionPool = JdbcConnectionPool.create(DB_URL, "sa", "");
	}
	void addFriend(String username, String friendname)		//
	{
		DatabaseFriendOpers.addFriend(connectionPool, username, friendname);
	}
	void acceptFriendRequest(String username, String friendname)
	{
		DatabaseFriendOpers.acceptFriendRequest(connectionPool, username, friendname);
	}
	void declineFriendRequest(String username, String friendname)
	{
		DatabaseFriendOpers.declineFriendRequest(connectionPool, username, friendname);
	}
	void cancelFriendRequest(String username, String friendname)
	{
		DatabaseFriendOpers.cancelFriendRequest(connectionPool, username, friendname);
	}
	void removeFriend(String username, String friendname)
	{
		DatabaseFriendOpers.removeFriend(connectionPool, username, friendname);
	}
	int register(String username, String name, int passwordHash)
	{
		return DatabaseAuthentifacationOpers.register(connectionPool, username, name, passwordHash);
	}
	Integer getPasswordHash(String username, int clientID)		//return the hash of password if the given user, null if the user does not exist
	{
		return DatabaseAuthentifacationOpers.getPasswordHash(connectionPool, username, clientID);
	}
	
	
	void init()
	{
		final String sql = 	"drop table if exists CLIENTS;" +
							"create table CLIENTS(" +
							"CLIENT_ID int auto_increment primary key," +
							"USERNAME varchar(32), " +
							"NAME varchar(64)," +
							"PASSWORD int," +
							"LOGIN_ATTEMPTS_LEFT int default 3);"+
				
							"drop table if exists FRIENDS;" +
							"create table FRIENDS("+
							"ENTRY_ID int auto_increment primary key," +
							"CLIENT_ID int,"+
							"FRIEND_ID int,"+
							"STATUS int default 0);";
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement statement = dbConnection.prepareStatement(sql);)
		{
			statement.executeUpdate();
		} catch (SQLException e)
		{
			System.out.println("Error initialising database");
			e.printStackTrace();
		}
	}
	
	
	
	void initSchedule(String username) 				//create a schedule table just for this user
	{
		Integer id = getIdByUsername(username);
		String tableName = "SCHEDULE"+id;
		
		String createTableStatement = "create table " + tableName+";";
		
		try(Connection dbConnection = connectionPool.getConnection();
			Statement statement = dbConnection.createStatement();)
		{
			statement.executeUpdate(createTableStatement);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		
	}

	
	
	
	String getUsernameByID(int id)
	{
		String username = null;
		final String sql = "select * from CLIENTS where CLIENT_ID=?;";
		
		try(Connection dbConnection = connectionPool.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql))
		{
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
			{
				username = rs.getString("USERNAME");
			}
			rs.close();
		}catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		return username;
	}
	String getNameByID(int id)
	{
		String name = null;
		final String sql = "select * from CLIENTS where CLIENT_ID=?;";
		
		try(Connection dbConnection = connectionPool.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql))
		{
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
			{
				name = rs.getString("NAME");
			}
			rs.close();
		}catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		return name;
	}
	private Integer getIdByUsername(String username)
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
	
}

