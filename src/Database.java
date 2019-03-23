
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;
import org.h2.jdbcx.JdbcConnectionPool;

import java.net.InetAddress;
import java.sql.*;
import java.util.StringTokenizer;
import org.h2.jdbcx.JdbcConnectionPool;

import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Database
{
	static final String DB_URL = "jdbc:h2:tcp://localhost/~/DB";
	JdbcConnectionPool connectionPool;
	
	public Database()
	{
		System.out.println("Established connection");
		connectionPool = JdbcConnectionPool.create(DB_URL, "sa", "");
	}
	
	public void init()
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
							"FRIEND_ID int);";
		
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
	
	int register(String username, String name, int passwordHash)  			//-1:: error|| -2::name is taken,
	{
		int clientID = -1;           																	//ID assigned to this client
		final String uniquenessQuery = "select * from CLIENTS where USERNAME=?";	   					//determine if the username is taken
		final String insertOperation = "insert into CLIENTS(USERNAME, NAME, PASSWORD) values(?, ?, ?)";	//if username is free, update the records
		final String createScheduleTableOperation = "create table ?";
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement uniquenessQueryStatement = dbConnection.prepareStatement(uniquenessQuery);
			 PreparedStatement insertStatement = dbConnection.prepareStatement(insertOperation, Statement.RETURN_GENERATED_KEYS);
			 PreparedStatement createScheduleStatement = dbConnection.prepareStatement(createScheduleTableOperation))
		{
			uniquenessQueryStatement.setString(1, name);
			
			insertStatement.setString(1, username);
			insertStatement.setString(2, name);
			insertStatement.setInt(3, passwordHash);
			
			createScheduleStatement.setString(1, username);
			
			ResultSet clientsWithGivenUsername = uniquenessQueryStatement.executeQuery();
			if(clientsWithGivenUsername.next())		//username taken
			{
				clientsWithGivenUsername.close();
				return -2;
			}
			
			insertStatement.executeUpdate();
			ResultSet rsInsert = insertStatement.getGeneratedKeys();
			if (rsInsert.next())
			{
				clientID = rsInsert.getInt(1);
			}
			rsInsert.close();
			//now create a schedule table just for this user
			createScheduleStatement.executeUpdate();
		}catch(SQLException e)
		{
			return -1;
		}
		System.out.println("Assigned:" + clientID);
		return clientID;
	}
	
	void initSchedule(String username) 				//create a schedule table just for this user
	{
		Integer id = getIdByUsername(username);
		String tableName = "SCHEDULE"+id;
		
		String createTableStatement = "create table " + tableName;
		
		try(Connection dbConnection = connectionPool.getConnection();
			Statement statement = dbConnection.createStatement();)
		{
			statement.executeUpdate(createTableStatement);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		
	}

	
	
	
//	int registerClient(String name, int passwordHash, String IP)		//a successful registration operation returns ID assigned to the client, -1 if the username is taken or some sort of failure occurs
//	{
//		int clientID = -1;           													//ID assigned to this client
//		final String uniquenessQuery = "SELECT * FROM CLIENTS WHERE NAME=?";	   		//determine if the username is taken
//		final String insertOperation = "INSERT INTO CLIENTS(NAME, PASSWORD_HASH, IP) VALUES(?, ?, ?)";	//if username is free, update the records
//
//		try (Connection dbConnection = connectionPool.getConnection();
//			 PreparedStatement uniquenessQueryStatement = dbConnection.prepareStatement(uniquenessQuery))
//		{
//			uniquenessQueryStatement.setString(1, name);
//
//			try (ResultSet clientsWithGivenUsername = uniquenessQueryStatement.executeQuery();
//				 PreparedStatement insertUserStatement = dbConnection.prepareStatement(insertOperation, Statement.RETURN_GENERATED_KEYS);)
//			{
//				if (!clientsWithGivenUsername.next()) 	//username okay, add the relevant client info to the database
//				{
//
//					insertUserStatement.setString(1, name);
//					insertUserStatement.setInt(2, passwordHash);
//					insertUserStatement.setString(3, IP);
//					insertUserStatement.executeUpdate();
//
//					ResultSet rs = insertUserStatement.getGeneratedKeys();
//					if (rs.next())
//					{
//						clientID = rs.getInt(1);
//					}
//					rs.close();
//				}
//			}
//		}catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//		System.out.println("Assigned:" + clientID);
//		return clientID;
//	}
	
	Integer getPasswordHash(String username, int clientID)		//return the hash of password if the given user, null if the user does not exist
	{
		Integer passwordHash = null;
		
		final String query = "select * from CLIENTS where USERNAME=?";
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement preparedStatement = dbConnection.prepareStatement(query);)
		{
			preparedStatement.setString(1, username);
//			preparedStatement.setInt(2, clientID);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
			{
				passwordHash =  rs.getInt("PASSWORD_HASH");
			}
			rs.close();
			return passwordHash;
		}catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}//if anything at all goes wrong fail the password check
	}
	String getUsernameByID(int id)
	{
		String username = null;
		final String sql = "select * from CLIENTS where CLIENT_ID=?";
		
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
		}
		return username;
	}
	String getNameByID(int id)
	{
		String name = null;
		final String sql = "select * from CLIENTS where CLIENT_ID=?";
		
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
		}
		return name;
	}
	Integer getIdByUsername(String username)
	{
		Integer id = null;
		final String sql = "select * from CLIENTS where USERNAME=?";
		
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
		}
		return id;
	}
	
}

