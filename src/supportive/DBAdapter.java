package supportive;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import core.SharedObjs;
import core.XmlMngr;
import customobjects.CrItem;
import customobjects.CrItemsList;
import customobjects.CustomFilterItem;
import customobjects.CustomFiltersList;


/**
 * DataBase Connection Class It has a constructor to create a connection and auxiliary methods to automatically generate query on database.
 *
 */
public class DBAdapter
{
	private static final String DB_DRIVER         = "com.mysql.jdbc.Driver";
	private String              DB_CONNECTION     = null;
	private String              DB_USER           = null;
	private String              DB_PASSWORD       = null;
	private Connection          dbConnection      = null;
	private PreparedStatement   preparedStatement = null;
	
	/**
	 * Constructor with connection parameter. For special cases when database connections will not be the default. Write the full path to
	 * database including 'jdbc', 'mysql' identifiers and the schema.
	 * <p>
	 * <b>Example:</b> jdbc:mysql://127.0.0.1/sat
	 * </p>
	 * 
	 * @param connection Connection String identifying the database connection
	 * @param user User String from database access
	 * @param password Password String from database access
	 * @throws SQLException If there is a database connection error
	 */
	public DBAdapter(String connection, String user, String password) throws SQLException
	{
		this.DB_CONNECTION = connection;
		this.DB_USER = user;
		this.DB_PASSWORD = password;
		
		this.dbConnection = getDBConnection();
		
	}
	
	/**
	 * Constructor with default connection. User and Password should come as parameters to create a valid Database connection. Default jdbc
	 * address is set in the XML DB file.
	 * 
	 * @param user User String from database access
	 * @param password Password String from database access
	 * @throws SQLException If there is a database connection error
	 */
	public DBAdapter(String user, String password) throws SQLException
	{
		this.DB_CONNECTION = "jdbc:mysql://" + XmlMngr.getSystemValueOf(new String[] {"configs", "db_server"}); // change hardcoded line to
		                                                                                                        // a default config in a xml
		                                                                                                        // file
		this.DB_USER = user;
		this.DB_PASSWORD = password;
		
		this.dbConnection = getDBConnection();
		
	}
	
	/**
	 * @throws SQLException
	 */
	public DBAdapter() throws SQLException
	{
		this.DB_CONNECTION = "jdbc:mysql://" + XmlMngr.getSystemValueOf(new String[] {"configs", "db_server"}); // change hardcoded line to
		                                                                                                        // a default config in a xml
		                                                                                                        // file
		this.DB_USER = "user";
		this.DB_PASSWORD = "user";
		
		this.dbConnection = getDBConnection();
		
	}
	
	/**
	 * Support method to work with constructor creating the DataBase connection
	 * 
	 * @return DataBase connection
	 * @throws SQLException If there is a database connection error
	 */
	private Connection getDBConnection() throws SQLException
	{
		try
		{
			Class.forName(DB_DRIVER);
		}
		catch (ClassNotFoundException e)
		{
			System.out.println(e.getMessage());
		}
		
		dbConnection = DriverManager.getConnection(this.DB_CONNECTION, this.DB_USER, this.DB_PASSWORD);
		
		return dbConnection;
	}
	
	/**
	 * Support method to set all fields in a CustomFilterItem. The ResultSet parameter should be already in the right row to ensure the
	 * CustomFilterItem will have the proper values. Fields that will be set:
	 * <p>
	 * - Filter Name - Header - Regex - Owner - Where to Main - Where to Kernel - Where to System - Where to Bugreport - Where to Report
	 * Output - Is Shared
	 * </p>
	 * 
	 * @param fitem A CustomFilterItem object to be filled
	 * @param rs A ResultSet containing all the values in the right row
	 * @return Object filled with desired values
	 * @throws SQLException When it occurred a problem to access fields in the ResultSet
	 */
	private CustomFilterItem setAllFilterFields(CustomFilterItem fitem, ResultSet rs) throws SQLException
	{
		SimpleDateFormat formater = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		
		fitem.setId(rs.getInt("f_id"));
		fitem.setName(rs.getString("Name"));
		fitem.setHeader(rs.getString("header"));
		fitem.setRegex(rs.getString("regex"));
		fitem.setOwner(rs.getString("user_key"));
		fitem.setKernel(byteToBool(rs.getByte("w_krnl")));
		fitem.setRadio(byteToBool(rs.getByte("w_radio")));
		fitem.setMain(byteToBool(rs.getByte("w_main")));
		fitem.setBugreport(byteToBool(rs.getByte("w_bugr")));
		fitem.setSystem(byteToBool(rs.getByte("w_syst")));
		fitem.setRoutput(byteToBool(rs.getByte("w_rout")));
		fitem.setShared(byteToBool(rs.getByte("shared")));
		fitem.setLastUpdate(formater.format(rs.getTimestamp("last_modified")));
		if (rs.getString("user_key") == null)
			fitem.setEditable(true);
		else
			fitem.setEditable(false);
		
		return fitem;
	}
	
	private CrItem setAllCrAnalyzedFields(CrItem crcitem, ResultSet rs) throws SQLException
	{
		crcitem.setJiraID(rs.getString("cr_id"));
		crcitem.setB2gID(rs.getString("b2g_id"));
		crcitem.setAssignee(rs.getString("assignee"));
		crcitem.setProduct(rs.getString("product"));
		crcitem.setResolution(rs.getString("resolution"));
		crcitem.setClosureDate(rs.getTimestamp("date").toString());
		
		return crcitem;
	}
	
	/**
	 * Support method to convert "boolean" to "byte"
	 * 
	 * @param boolVar Boolean variable to convert
	 * @return Desired byte
	 */
	private byte boolToByte(boolean boolVar)
	{
		byte bVar = (byte) ((boolVar) ? 1 : 0);
		
		return bVar;
	}
	
	/**
	 * Support method to convert "byte" to "boolean"
	 * 
	 * @param bVar Byte variable to convert
	 * @return Desired boolean
	 */
	private boolean byteToBool(byte bVar)
	{
		boolean boolVar = false;
		if (bVar == 1)
			boolVar = true;
		
		return boolVar;
	}
	
	/**
	 * Support method only to print all filter names on a SELECT *
	 * 
	 */
	public void printNamesSelectAll()
	{
		String selectSQL = "SELECT * FROM Filters";
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				
				String userid = rs.getString("Name");
				
				System.out.println("userid : " + userid);
				
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
	}
	
	/**
	 * Get all filters and return them on a CustomFiltersList
	 * 
	 * @return A CustomFiltersList (ArrayList) with CustomFilterItem objects containing all filters
	 */
	public CustomFiltersList selectAllFilters()
	{
		String selectSQL = "SELECT * FROM Filters";
		CustomFilterItem aux = new CustomFilterItem();
		CustomFiltersList flist = new CustomFiltersList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CustomFilterItem();
				flist.add(setAllFilterFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return flist;
		
	}
	
	/**
	 * Search and returns an extended ArrayList customized for Filters containing all Filter Items that user is the owner. <b>Uses
	 * SharedObjs.getUser() to determine username</b>.
	 * 
	 * @return An ArrayList containing all Filter Items under the user name.
	 */
	public CustomFiltersList myFilters()
	{
		String selectSQL = "SELECT * FROM Filters WHERE user_key = '" + SharedObjs.getUser() + "';";
		System.out.println("DB My Filters Query: " + selectSQL);
		CustomFilterItem aux = new CustomFilterItem();
		CustomFiltersList flist = new CustomFiltersList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CustomFilterItem();
				flist.add(setAllFilterFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return flist;
	}
	
	/**
	 * Search and returns an extended ArrayList customized for Filters containing all Filter Items that shared flag is enabled.
	 * 
	 * @return An ArrayList containing all Filter Items with shared flag enabled.
	 */
	public CustomFiltersList sharedFilters()
	{
		String selectSQL = "SELECT * FROM Filters WHERE shared = " + 1 + " AND user_key != '" + SharedObjs.getUser() + "';";
		CustomFilterItem aux = new CustomFilterItem();
		CustomFiltersList flist = new CustomFiltersList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CustomFilterItem();
				flist.add(setAllFilterFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return flist;
	}
	
	public CustomFiltersList publicFilters()
	{
		String selectSQL = "SELECT * FROM Filters WHERE user_key = 'public';";
		CustomFilterItem aux = new CustomFilterItem();
		CustomFiltersList flist = new CustomFiltersList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CustomFilterItem();
				flist.add(setAllFilterFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return flist;
	}
	
	public CustomFiltersList activeFilters()
	{
		String selectSQL = "SELECT Filters.* FROM Filters, ActiveFilters WHERE (ActiveFilters.user = '" + SharedObjs.getUser()
		                   + "' OR  ActiveFilters.user = 'Public') AND ActiveFilters.filter_id = Filters.f_id;";
		CustomFilterItem aux = new CustomFilterItem();
		CustomFiltersList flist = new CustomFiltersList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CustomFilterItem();
				flist.add(setAllFilterFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return flist;
	}
	
	/**
	 * Check if the Filter exists under the owner name
	 * 
	 * @param filterName The name of desired filter
	 * @param userName The name of owner of the filter
	 * @return [True] if found a result [False] if not
	 */
	public int existsFilterWithOwner(String filterName, String userName)
	{
		String selectSQL = "SELECT f_id FROM Filters WHERE name = '" + filterName + "' AND user_key = '" + userName + "';";
		int id = -1;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			// if a line exists (found a result) then found receives true
			if (rs.next())
			{
				System.out.println("---id: " + id);
				id = rs.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return id;
	}
	
	/**
	 * Inserts Filter Item on Data Base.
	 * 
	 * @param filter Filter Item containing all fields that should be inserted.
	 * @return [0] if insert failed (Invalid fields) [1] if insert succeeded.
	 */
	public int insertFilter(CustomFilterItem filter)
	{
		int insertDone = 0;
		
		try
		{
			// Visual query example for reference:
			// INSERT INTO Filters VALUES (null, 'Test_Filter', '- TestHeader' , '[A-z]', 1, 1, 0, 0, 1, 1, 1, 0, 'testuser', null);
			String insertSQL = "INSERT INTO Filters VALUES (null, '" + filter.getName() + "', '" + filter.getHeader() + "', '" + filter.getRegex() + "', "
			                   + boolToByte(filter.isMain()) + ", " + boolToByte(filter.isSystem()) + ", " + boolToByte(filter.isKernel()) + ", "
			                   + boolToByte(filter.isRadio()) + ", " + boolToByte(filter.isBugreport()) + ", " + boolToByte(filter.isRoutput()) + ", "
			                   + boolToByte(filter.isShared()) + ", '" + (filter.isPublic() ? "Public" : SharedObjs.getUser()) + "', null);";
			
			preparedStatement = dbConnection.prepareStatement(insertSQL);
			
			// Execute insert SQL statement
			insertDone = insertDone + preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		if (filter.isActive() && insertDone > 0)
		{
			int id = -1;
			
			id = existsFilterWithOwner(filter.getName(), filter.getOwner());
			
			if (id >= 0)
			{
				String activeInsertSQL = "INSERT INTO ActiveFilters VALUES (null, '" + SharedObjs.getUser() + "', " + id + ")";
				
				try
				{
					preparedStatement = dbConnection.prepareStatement(activeInsertSQL);
					
					// Execute insert SQL statement
					insertDone = insertDone + preparedStatement.executeUpdate();
					
				}
				catch (SQLException e)
				{
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}
		}
		
		return insertDone;
	}
	
	/**
	 * @param filtersList
	 * @return Filters inserted {@link int}
	 */
	public int insertFilters(CustomFiltersList filtersList)
	{
		// Visual query example for reference:
		// INSERT INTO Filters VALUES (null, 'Test_Filter', '- TestHeader' , '[A-z]', 1, 1, 0, 0, 1, 1, 1, 0, 'testuser', null);
		int inserted = 0;
		
		for (CustomFilterItem filter : filtersList)
		{
			inserted = inserted + insertFilter(filter);
		}
		
		return inserted;
	}
	
	/**
	 * This method will update filters only under the name of the user. If the user is not the owner he won't be able to edit it. <b>Uses
	 * SharedObjs.getUser() to determine username.</b> Also, this method attempts to update every field on Filters table whether it has
	 * changed or not.
	 * 
	 * @param oldFilterName The name of the filter that should be updated
	 * @param editedFilter New Filter item containing all fields with updated values
	 * @return [0] if update failed (User is not the owner or filter name does not exist or a field has an invalid value) [1] if update
	 *         succeeded.
	 */
	public int updateFilter(CustomFilterItem editedFilter)
	{
		// Visual query example for reference:
		// UPDATE Filters SET name = 'Test_Filter', header = '- TestHeader', regex = '[A-z]', w_main = 1, w_syst = 1, w_krnl = 0, w_radio
		// =
		// 1, w_bugr = 0, w_rout = 1, shared = 1, active = 0, user_key = 'testuser' WHERE name = 'Test_Adapter' AND user_key =
		// 'testuser';
		
		String updateSQL = "UPDATE Filters SET name = '" + editedFilter.getName() + "', header = '" + editedFilter.getHeader() + "', regex = '"
		                   + editedFilter.getRegex() + "', w_main = " + boolToByte(editedFilter.isMain()) + ", w_syst = " + boolToByte(editedFilter.isSystem())
		                   + ", w_krnl = " + boolToByte(editedFilter.isKernel()) + ", w_radio = " + boolToByte(editedFilter.isRadio()) + ", w_bugr = "
		                   + boolToByte(editedFilter.isBugreport()) + ", w_rout = " + boolToByte(editedFilter.isRoutput()) + ", shared = "
		                   + boolToByte(editedFilter.isShared()) + ", user_key = '" + editedFilter.getOwner() + "' WHERE f_id = " + editedFilter.getId() + ";";
		
		int updateDone = 0;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(updateSQL);
			
			// Execute insert SQL statement
			updateDone = preparedStatement.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		// Check if filter is an active one.
		if (editedFilter.isActive())
		{
			System.out.println("--------- Is Active");
			try
			{
				// Execute select SQL statement
				String findOcc = "SELECT * FROM ActiveFilters WHERE user = '" + SharedObjs.getUser() + "' AND filter_id = " + editedFilter.getId() + ";";
				preparedStatement = dbConnection.prepareStatement(findOcc);
				ResultSet rs = preparedStatement.executeQuery();
				
				// Check if filter exists in ActiveFilters tab
				// if a line does not exist, add it.
				if (!rs.next())
				{
					System.out.println("--------- Nao encontrado - " + SharedObjs.getUser() + " - filter_id = " + editedFilter.getId());
					try
					{
						// Execute insert (update) SQL statement
						String activeInsertSQL = "INSERT INTO ActiveFilters VALUES (null, '" + SharedObjs.getUser() + "', " + editedFilter.getId() + ")";
						preparedStatement = dbConnection.prepareStatement(activeInsertSQL);
						System.out.println("-------- Active Insert: " + preparedStatement.executeUpdate());
					}
					catch (SQLException e)
					{
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
				
			}
		}
		else
		{
			try
			{
				// Execute select SQL statement
				String findOcc = "SELECT * FROM ActiveFilters WHERE user = '" + SharedObjs.getUser() + "' AND filter_id = " + editedFilter.getId() + ";";
				preparedStatement = dbConnection.prepareStatement(findOcc);
				ResultSet rs = preparedStatement.executeQuery();
				
				// Check if filter exists in ActiveFilters tab
				// if a line does not exist, add it.
				if (rs.next())
				{
					try
					{
						// Execute insert (update) SQL statement
						// DELETE FROM `sat_db`.`ActiveFilters` WHERE `idActiveFilters`='5';
						String activeInsertSQL = "DELETE FROM ActiveFilters WHERE idActiveFilters = " + rs.getInt(1) + ";";
						preparedStatement = dbConnection.prepareStatement(activeInsertSQL);
						preparedStatement.executeUpdate();
					}
					catch (SQLException e)
					{
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
				
			}
		}
		
		return updateDone;
		
	}
	
	/**
	 * Delete filter query adapter. Only will work if the owner of the filter is the same user trying to delete it. <b>Uses
	 * SharedObjs.getUser() to determine username.</b>
	 * 
	 * @param filterName The filter name that will be deleted
	 * @return [0] if delete failed (User is not the owner or filter name does not exist) [1] if delete succeeded.
	 */
	public int deleteFilter(CustomFilterItem filter)
	{
		// Visual query example for reference:
		// DELETE from Filters where name = 'Test_Filter';
		String deleteSQL = "DELETE from Filters where f_id = " + filter.getId() + ";";
		
		int deleteDone = 0;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(deleteSQL);
			
			// Execute delete SQL statement
			deleteDone = preparedStatement.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return deleteDone;
	}
	
	/**
	 * Delete all filters from user. This is only a support method and should be used only for tests purposes
	 * 
	 * @return [0] if delete failed [1] if delete succeeded.
	 */
	public int deleteAllMyFilters()
	{
		// Visual query example for reference:
		// DELETE from Filters where user_key = 'testuser';
		String deleteSQL = "DELETE from Filters where user_key = '" + SharedObjs.getUser() + "';";
		
		int deleteDone = 0;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(deleteSQL);
			
			// Execute delete SQL statement
			deleteDone = preparedStatement.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return deleteDone;
	}
	
	public int insertAnalyzedCR(CrItem crc_item)
	{
		// Visual query example for reference:
		// INSERT into Analyzed_CRs VALUES ('IKUT-1112', '8888888', 'testuser', 'Product','Tethering', '2015-08-05 18:19:03');
		int insertDone = 0;
		
		try
		{
			String insertSQL = "INSERT INTO Analyzed_CRs VALUES ('" + crc_item.getJiraID() + "', '" + crc_item.getB2gID() + "', '" + crc_item.getAssignee()
			                   + "', '" + crc_item.getProduct() + "', '" + crc_item.getResolution() + "', null);";
			
			preparedStatement = dbConnection.prepareStatement(insertSQL);
			
			// Execute insert SQL statement
			insertDone = insertDone + preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return insertDone;
	}
	
	public int updateAnalyzedCR(CrItem crc_item)
	{
		// Visual query example for reference:
		// INSERT into Analyzed_CRs VALUES ('IKUT-1112', '8888888', 'testuser', 'Product','Tethering', '2015-08-05 18:19:03');
		int insertDone = 0;
		
		try
		{
			String insertSQL = "UPDATE Analyzed_CRs " + "SET assignee='" + crc_item.getAssignee() + "', " + "resolution='" + crc_item.getResolution() + "' "
			                   + "WHERE cr_id='" + crc_item.getJiraID() + "';";
			
			preparedStatement = dbConnection.prepareStatement(insertSQL);
			
			// Execute insert SQL statement
			insertDone = insertDone + preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return insertDone;
	}
	
	public int existsAnalyzedCR(String cr_id)
	{
		String selectSQL = "SELECT cr_id FROM Analyzed_CRs WHERE cr_id = '" + cr_id + "';";
		int found = 0;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			// if a line exists (found a result) then found receives true
			if (rs.next())
			{
				System.out.println("---id found: " + cr_id);
				found = 1;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return found;
	}
	
	public int deleteAnalyzedCR(CrItem crc_item)
	{
		// Visual query example for reference:
		// DELETE from Analyzed_CRs where cr_id = 'IKUT-1111';
		String deleteSQL = "DELETE from Analyzed_CRs where cr_id = " + crc_item.getJiraID() + ";";
		
		int deleteDone = 0;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(deleteSQL);
			
			// Execute delete SQL statement
			deleteDone = preparedStatement.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return deleteDone;
	}
	
	/**
	 *
	 * Usage example '2013-08-05' or '2014-08-05 18:19:04' on parameters 'from' and 'to'
	 * @param from String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param to String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @return
	 */
	public CrItemsList AnalyzedCRsInRange(String from, String to)
	{
		String selectSQL = "SELECT * FROM Analyzed_CRs WHERE date >= '" + from + "' and date <= '" + to + "';";
		CrItem aux = new CrItem();
		CrItemsList crc_list = new CrItemsList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CrItem();
				crc_list.add(setAllCrAnalyzedFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return crc_list;
	}
	
	/**
	 * 
	 * Usage example '2013-08-05' or '2014-08-05 18:19:04' on parameters 'from' and 'to'
	 * @param from String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param to String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param assignee
	 * @return
	 */
	public CrItemsList analyzedCRsInRangeWithAssignee(String from, String to, String assignee)
	{
		String selectSQL = "SELECT * FROM Analyzed_CRs WHERE date >= '" + from + "' and date <= '" + to + "' and assignee = '" + assignee + "';";
		CrItem aux = new CrItem();
		CrItemsList crc_list = new CrItemsList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CrItem();
				crc_list.add(setAllCrAnalyzedFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return crc_list;
	}
	
	/**
	 * 
	 * Usage example '2013-08-05' or '2014-08-05 18:19:04' on parameters 'from' and 'to'
	 * @param from String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param to String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param resolution
	 * @return
	 */
	public CrItemsList analyzedCRsInRangeWithResolution(String from, String to, String resolution)
	{
		String selectSQL = "SELECT * FROM Analyzed_CRs WHERE date >= '" + from + "' and date <= '" + to + "' and resolution = '" + resolution + "';";
		CrItem aux = new CrItem();
		CrItemsList crc_list = new CrItemsList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CrItem();
				crc_list.add(setAllCrAnalyzedFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return crc_list;
	}
	
	/**
	 * 
	 * Usage example '2013-08-05' or '2014-08-05 18:19:04' on parameters 'from' and 'to'
	 * @param from String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param to String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param assignee
	 * @param resolution
	 * @return
	 */
	public CrItemsList analyzedCRsInRangeWithAssigneeAndResolution(String from, String to, String assignee, String resolution)
	{
		String selectSQL = "SELECT * FROM Analyzed_CRs WHERE date >= '" + from + "' and date <= '" + to + "' and assignee = '" + assignee
		                   + "' and resolution = '" + resolution + "';";
		CrItem aux = new CrItem();
		CrItemsList crc_list = new CrItemsList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CrItem();
				crc_list.add(setAllCrAnalyzedFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return crc_list;
	}
	
	/**
	 * 
	 * Usage example '2013-08-05' or '2014-08-05 18:19:04' on parameters 'from' and 'to'
	 * @param from String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @param to String date field 'yyyy-mm-dd' or 'yyyy-mm-dd hh:mm:ss'
	 * @return
	 */
	public CrItemsList closedCRsInRangeWithResolution(String from, String to)
	{
		String selectSQL = "SELECT * FROM Analyzed_CRs WHERE date >= '" + from + "' and date <= '" + to + "' and resolution = 'Unresolved';";
		CrItem aux = new CrItem();
		CrItemsList crc_list = new CrItemsList();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next())
			{
				aux = new CrItem();
				crc_list.add(setAllCrAnalyzedFields(aux, rs));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		}
		
		return crc_list;
	}
	
	public int addUidProcess(String uid, String process)
	{
		int insertDone = 0;
		
		String insertSQL = "INSERT INTO uids_processes VALUES ('" + uid + "', '" + process + "');";
		
		try
        {
	        preparedStatement = dbConnection.prepareStatement(insertSQL);

			// Execute insert SQL statement
			insertDone = insertDone + preparedStatement.executeUpdate();
        }
		catch (MySQLIntegrityConstraintViolationException dup)
		{
			return -1;
		}
        catch (SQLException e)
        {
	        e.printStackTrace();
        }
		
		return insertDone;
	}
	
	public String existsUid(String uid)
	{
		String selectSQL = "SELECT process FROM uids_processes WHERE uid = '" + uid + "';";
		String process = null;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			// if a line exists (found a result) then found receives true
			if (rs.next())
			{
				process = rs.getString(1);
				return process;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	public HashMap<String, String> getAllUids()
	{
		String selectSQL = "SELECT * FROM uids_processes;";
		HashMap<String, String> map = new HashMap<String, String>();
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			// execute select SQL statement
			ResultSet rs = preparedStatement.executeQuery();
			
			// if a line exists (found a result) then found receives true
			while (rs.next())
			{
				map.put(rs.getString(1), rs.getString(2));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return map;
	}
	
	public int updateUid(String uid, String process)
	{
		// Visual query example for reference:
		// INSERT into Analyzed_CRs VALUES ('IKUT-1112', '8888888', 'testuser', 'Product','Tethering', '2015-08-05 18:19:03');
		int insertDone = 0;
		
		try
		{
			String insertSQL = "UPDATE uids_processes " + "SET process='" + process + "' WHERE uid='" + uid + "';";
			
			preparedStatement = dbConnection.prepareStatement(insertSQL);
			
			// Execute insert SQL statement
			insertDone = insertDone + preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return insertDone;
	}
	
	public int deleteUid(String uid)
	{
		// Visual query example for reference:
		// DELETE from Analyzed_CRs where cr_id = 'IKUT-1111';
		String deleteSQL = "DELETE from uids_processes where uid = '" + uid + "';";
		
		int deleteDone = 0;
		
		try
		{
			preparedStatement = dbConnection.prepareStatement(deleteSQL);
			
			// Execute delete SQL statement
			deleteDone = preparedStatement.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return deleteDone;
	}
	
	/**
	 * Close Data Base connection
	 * 
	 * @throws SQLException If occurs a failure during connection closure.
	 */
	public void close() throws SQLException
	{
		if (preparedStatement != null)
		{
			preparedStatement.close();
		}
		
		if (dbConnection != null)
		{
			dbConnection.close();
		}
	}
	
}
