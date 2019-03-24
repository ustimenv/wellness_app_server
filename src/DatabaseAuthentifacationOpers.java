import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;

abstract class DatabaseAuthentifacationOpers
{
	static int register(JdbcConnectionPool connectionPool, String username, String name, int passwordHash)  			//-1:: error|| -2::name is taken,
	{
		int clientID = -1;           																	//ID assigned to this client
		final String uniquenessQuery = "select * from CLIENTS where USERNAME=?;";	   					//determine if the username is taken
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
	static Integer getPasswordHash(JdbcConnectionPool connectionPool, String username, int clientID)		//return the hash of password if the given user, null if the user does not exist
	{
		Integer passwordHash = null;
		
		final String query = "select * from CLIENTS where USERNAME=?;";
		
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
}
