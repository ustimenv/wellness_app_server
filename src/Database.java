
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
	static final String DB_URL = "jdbc:h2:tcp://localhost/~/TestDB";
	
	JdbcConnectionPool connectionPool;
	
	public Database()
	{
		System.out.println("Established connection");
		connectionPool = JdbcConnectionPool.create(DB_URL, "sa", "");
	}
	
	
	String getName(int ID)             							   //get the alias of the client with the given ID
	{
		String name = "Client does not exist";
		final String sql = "SELECT * FROM CLIENTS WHERE CLIENT_ID=?";
		
		try(Connection dbConnection = connectionPool.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)){
			
			preparedStatement.setInt(1, ID);
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
	
	
	int registerClient(String name, int passwordHash, String IP)		//a successful registration operation returns ID assigned to the client, -1 if the username is taken or some sort of failure occurs
	{
		int clientID = -1;           													//ID assigned to this client
		final String uniquenessQuery = "SELECT * FROM CLIENTS WHERE NAME=?";	   		//determine if the username is taken
		final String insertOperation = "INSERT INTO CLIENTS(NAME, PASSWORD_HASH, IP) VALUES(?, ?, ?)";	//if username is free, update the records
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement uniquenessQueryStatement = dbConnection.prepareStatement(uniquenessQuery))
		{
			uniquenessQueryStatement.setString(1, name);
			
			try (ResultSet clientsWithGivenUsername = uniquenessQueryStatement.executeQuery();
				 PreparedStatement insertUserStatement = dbConnection.prepareStatement(insertOperation, Statement.RETURN_GENERATED_KEYS);)
			{
				if (!clientsWithGivenUsername.next()) 	//username okay, add the relevant client info to the database
				{
					
					insertUserStatement.setString(1, name);
					insertUserStatement.setInt(2, passwordHash);
					insertUserStatement.setString(3, IP);
					insertUserStatement.executeUpdate();
					
					ResultSet rs = insertUserStatement.getGeneratedKeys();
					if (rs.next())
					{
						clientID = rs.getInt(1);
					}
					rs.close();
				}
			}
		}catch (SQLException e)
		{
			e.printStackTrace();
		}
		System.out.println("Assigned:" + clientID);
		return clientID;
	}
	
	public void initTest()
	{
		final String sql = 	"DROP TABLE IF EXISTS CLIENTS;" +
							"DROP TABLE IF EXISTS SLEEP;" +
				
							"CREATE TABLE CLIENTS(" +
							"CLIENT_ID int auto_increment PRIMARY KEY," +
							"NAME VARCHAR(32), " +
							"PASSWORD_HASH INT," +
							"LOGIN_ATTEMPTS_LEFT INT DEFAULT 3," +
							"IP VARCHAR(32)); " +
				
							"CREATE TABLE SLEEP(" +
							"MESSAGE_ID INT auto_increment PRIMARY KEY," +
							"MESSAGE_TEXT VARCHAR(255) NOT NULL," +
							"DATE DATETIME DEFAULT CURRENT_TIMESTAMP()," +
							"FOREIGN KEY (CLIENT_ID) references CLIENTS(CLIENT_ID)" + ");";
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement statement = dbConnection.prepareStatement(sql);)
		{
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error initialising database");
			e.printStackTrace();
		}
	}
	
	
	public Integer getPasswordHash(String username, int clientID)
	{
		Integer passwordHash = null;
		
		final String query = "SELECT * FROM CLIENTS WHERE NAME=?";
		
		try (Connection dbConnection = connectionPool.getConnection();
			 PreparedStatement preparedStatement = dbConnection.prepareStatement(query);)
		{
			preparedStatement.setString(1, username);
//			preparedStatement.setInt(2, clientID);
			ResultSet rs = preparedStatement.executeQuery();
			
			if (rs.next())
			{
				passwordHash = new Integer((int) rs.getInt("PASSWORD_HASH"));
			}
			rs.close();
			return passwordHash;
		}catch(Exception e){e.printStackTrace(); return null;}//if anything at all goes wrong fail the password check
	}
	
	
}

