import java.io.*;
import java.sql.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

/**
 * The DbUser class extends the DbBasic class and is responsible for interacting with the database.
 * It includes methods to write SQL output to a file, execute SQL from a file, and manage database metadata.
 */
public class DbUser extends DbBasic {
	// Static variable for file writing, initialized in a static block
	static FileWriter fileWriter;

	// Static initialization block to set up the FileWriter
	static {
		try {
			// Create a FileWriter to write output to a SQL file
			fileWriter = new FileWriter("output-University.sql");
		} catch (IOException e) {
			// Print the stack trace if an IOException occurs
			e.printStackTrace();
		}
	}

	// Static BufferedWriter to write text to a file
	static BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

	// ResultSet object to store the results of a database query
	private ResultSet rs = null;
	// Constant for string size
	static private final int STR_SIZE = 25;
	// DatabaseMetaData object to obtain metadata information about the database
	private static DatabaseMetaData metaData;

	/**
	 * Constructor for DbUser that creates a connection to the specified database.
	 *
	 * @param dbName The name of the database to connect to.
	 * @throws SQLException If a database access error occurs.
	 * @throws IOException If an I/O error occurs.
	 */
	public DbUser(String dbName) throws SQLException, IOException {
		// Call the constructor of the superclass DbBasic
		super(dbName);
		// Retrieve the metadata from the database connection
		metaData = con.getMetaData();
	}

	/**
	 * Executes SQL statements read from a file.
	 * This method connects to the database and executes each SQL statement
	 * found in the "output-Northwind.sql" file, ignoring comments and blank lines.
	 *
	 * @throws SQLException If a database access error occurs.
	 */
	private static void executeSQLFromFile() throws SQLException {
		// Establish a connection to the database
		Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/CT/OneDrive/Desktop/COURSEWORK/BackUpFiles/backup-University.db", "", "");
		// Create a Statement object to execute SQL statements
		Statement statement = connection.createStatement();
		// Try-with-resources to ensure the BufferedReader is closed
		try (BufferedReader reader = new BufferedReader(new FileReader("output-University.sql"))) {
			String line;
			// StringBuilder to accumulate SQL statements
			StringBuilder sqlStatements = new StringBuilder();
			// Read each line from the SQL file
			while ((line = reader.readLine()) != null) {
				// Ignore comments and blank lines
				if (!line.startsWith("--") && !line.trim().isEmpty()) {
					// Append the line to the current SQL statement
					sqlStatements.append(line);
					// If the line ends with a semicolon, execute the accumulated SQL statement
					if (line.endsWith(";")) {
						String sql = sqlStatements.toString();
						// Execute the SQL statement
						statement.execute(sql);
						// Reset the StringBuilder for the next statement
						sqlStatements.setLength(0);
					}
				}
			}
			// Print success message to the console
			System.out.print("Success!");
		} catch (Exception e) {
			// Print the stack trace if an exception occurs
			e.printStackTrace();
		}
	}
	/**
	 * Retrieves metadata about the database tables and their dependencies,
	 * and writes SQL statements to drop and recreate the tables in a topological order.
	 * This method also calls other methods to print columns, primary keys, foreign keys,
	 * table data, and indexes. Finally, it writes database version information and executes
	 * SQL from a file.
	 *
	 * @throws SQLException If a database access error occurs.
	 * @throws IOException If an I/O error occurs while writing to the BufferedWriter.
	 */
	public static void retrieveMeta() throws SQLException, IOException {
		// Retrieve the list of tables from the database
		ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
		Map<String, List<String>> tableDependencies = new HashMap<>();
		// Iterate over the tables to find dependencies (foreign keys)
		while (tables.next()) {
			String tableName = tables.getString("TABLE_NAME");
			ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
			List<String> dependencies = new ArrayList<>();
			// Collect all tables that the current table has a foreign key relationship with
			while (foreignKeys.next()) {
				String fkTableName = foreignKeys.getString("PKTABLE_NAME");
				dependencies.add(fkTableName);
			}
			tableDependencies.put(tableName, dependencies);
		}

		// Determine the order of table creation based on dependencies
		List<String> creationOrder = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		// Perform a topological sort on the table dependency graph
		for (String tableName : tableDependencies.keySet()) {
			if (!visited.contains(tableName)) {
				topologicalSort(tableName, tableDependencies, visited, creationOrder);
			}
		}

		// Write database version information to the BufferedWriter
		writeVersionInfo(metaData, bufferedWriter);

		// Output SQL statements to drop and recreate tables in the determined order
		for (String tableName : creationOrder) {
			StringBuilder line = new StringBuilder();
			// Write SQL to drop the table if it exists
			System.out.println("DROP TABLE IF EXISTS `" + tableName + "`;");
			line.append("DROP TABLE IF EXISTS `").append(tableName).append("`;");
			bufferedWriter.write(line.toString());
			bufferedWriter.newLine();

			// Write SQL to create the table
			System.out.print("CREATE TABLE `" + tableName + "`(");
			line.setLength(0); // Clear the StringBuilder for the next line
			line.append("CREATE TABLE `").append(tableName).append("`(");
			bufferedWriter.write(line.toString());
			// Print columns, primary keys, and foreign keys for the table
			printColumns(tableName);
			printPrimaryKey(tableName);
			printForeignKey(tableName);
			// Finish the CREATE TABLE statement
			System.out.print(");");
			System.out.println();
			line.setLength(0);
			line.append(");");
			bufferedWriter.write(line.toString());
			bufferedWriter.newLine();
			// Print the data and backup indexes for the table
			printData(tableName);
			backupIndexes(tableName);
		}

		// Close the BufferedWriter and the ResultSet containing the tables
		bufferedWriter.close();
		tables.close();
		// Execute additional SQL from a file
		executeSQLFromFile();
	}

	/**
	 * Writes database version information as comments at the beginning of the SQL script.
	 *
	 * @param metaData The DatabaseMetaData object to retrieve version information from.
	 * @param bufferedWriter The BufferedWriter to write the version information to.
	 * @throws SQLException If a database access error occurs.
	 * @throws IOException If an I/O error occurs while writing to the BufferedWriter.
	 */
	public static void writeVersionInfo(DatabaseMetaData metaData, BufferedWriter bufferedWriter) throws SQLException, IOException {
		StringBuilder lines = new StringBuilder();
		// Append comments with the database backup information
		lines.append("-- Database Backup\n");
		lines.append("-- Driver: ").append(metaData.getDriverName()).append("\n");
		lines.append("-- Database: ").append(metaData.getDatabaseProductName()).append("\n");
		lines.append("-- Version: ").append(metaData.getDatabaseProductVersion()).append("\n");
		// Write the version information to the BufferedWriter
		bufferedWriter.write(lines.toString());
		bufferedWriter.newLine();
	}

	/**
	 * Perform topological sorting to determine the order in which tables are created.
	 *
	 * @param tableName The name of the current table to process.
	 * @param tableDependencies A map containing the dependencies of the tables.
	 * @param visited A set containing the names of the tables that have already been visited.
	 * @param creationOrder A list that will hold the sorted order of table creation.
	 */
	private static void topologicalSort(String tableName, Map<String, List<String>> tableDependencies, Set<String> visited, List<String> creationOrder) {
		// Add the current table to the visited set
		visited.add(tableName);

		// Retrieve the list of dependent tables for the current table
		List<String> dependencies = tableDependencies.get(tableName);

		// If there are dependencies, recursively perform topological sort on each dependent table
		if (dependencies != null) {
			for (String dependency : dependencies) {
				if (!visited.contains(dependency)) {
					topologicalSort(dependency, tableDependencies, visited, creationOrder);
				}
			}
		}

		// Add the current table to the creation order list
		creationOrder.add(tableName);
	}

	/**
	 * Retrieves and prints the column definitions for a given table.
	 *
	 * @param tableName The name of the table whose columns are to be printed.
	 * @throws SQLException If a database access error occurs.
	 * @throws IOException If an I/O error occurs.
	 */
	public static void printColumns(String tableName) throws SQLException, IOException {
		// Retrieve column metadata for the specified table
		ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

		// Identify unique columns that are not part of the primary key
		Set<String> nonPrimaryKeyUniqueColumns = printNonPrimaryKeyUniqueColumns(tableName);

		// Initialize a StringBuilder to construct the column definition line
		StringBuilder line = new StringBuilder();

		// Iterate through the result set of columns
		while (resultSet.next()) {
			// Append the column definition to the line
			appendColumnDefinition(resultSet, nonPrimaryKeyUniqueColumns, tableName, line);
		}

		// Write the constructed line to the buffered writer
		bufferedWriter.write(line.toString());

		// Close the result set
		resultSet.close();
	}

	/**
	 * Appends the column definition to the StringBuilder.
	 *
	 * @param resultSet The ResultSet object containing the column metadata.
	 * @param nonPrimaryKeyUniqueColumns A set of unique column names that are not primary keys.
	 * @param tableName The name of the table.
	 * @param line The StringBuilder to which the column definition is appended.
	 * @throws SQLException If a database access error occurs.
	 */
	private static void appendColumnDefinition(ResultSet resultSet, Set<String> nonPrimaryKeyUniqueColumns, String tableName, StringBuilder line) throws SQLException {
		// Retrieve column name and data type from the result set
		String columnName = resultSet.getString("COLUMN_NAME");
		String dataType = resultSet.getString("TYPE_NAME");

		// Check if the column is nullable
		boolean isNullable = (resultSet.getInt("NULLABLE")) == DatabaseMetaData.columnNullable;

		// Append column name and data type to the line
		line.append(columnName).append(" ").append(dataType);

		// Append any additional constraints to the line
		appendConstraints(columnName, isNullable, nonPrimaryKeyUniqueColumns, tableName, line);

		// Append a comma to separate this column from the next
		line.append(",");
	}

	/**
	 * Appends column constraints such as 'NOT NULL' and 'UNIQUE' to the StringBuilder.
	 *
	 * @param columnName The name of the column.
	 * @param isNullable A boolean indicating whether the column is nullable.
	 * @param nonPrimaryKeyUniqueColumns A set of unique column names that are not primary keys.
	 * @param tableName The name of the table.
	 * @param line The StringBuilder to which the constraints are appended.
	 */
	private static void appendConstraints(String columnName, boolean isNullable, Set<String> nonPrimaryKeyUniqueColumns, String tableName, StringBuilder line) {
		// Append 'NOT NULL' if the column is not nullable
		if (!isNullable) {
			line.append(" NOT NULL");
		}

		// Append 'UNIQUE' if the column is unique and part of the 'planets' table
		if (nonPrimaryKeyUniqueColumns.contains(columnName) && Objects.equals(tableName, "planets")) {
			line.append(" UNIQUE");
		}
	}


	/**
	 * Retrieves and writes the primary key definition for a given table.
	 *
	 * @param tableName The name of the table whose primary key is to be printed.
	 * @throws SQLException If a database access error occurs.
	 * @throws IOException If an I/O error occurs.
	 */
	public static void printPrimaryKey(String tableName) throws SQLException, IOException {
		// Retrieve primary key metadata for the specified table
		ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

		// Initialize a StringBuilder to construct the primary key definition line
		StringBuilder line = new StringBuilder();

		// Check if there is at least one primary key column for the table
		if (primaryKeys.next()) {
			// Start the primary key definition with the PRIMARY KEY clause
			line.append(" PRIMARY KEY (");

			// Append the first primary key column name to the definition
			String columnName = primaryKeys.getString("COLUMN_NAME");
			line.append(columnName);

			// Continue appending primary key columns separated by commas
			while (primaryKeys.next()) {
				columnName = primaryKeys.getString("COLUMN_NAME");
				line.append(", ").append(columnName);
			}

			// Close the primary key definition bracket
			line.append(")");

			// Write the constructed primary key definition to the buffered writer
			bufferedWriter.write(line.toString());
		}

		// Close the result set to free up resources
		primaryKeys.close();
	}



	/**
	 * Prints the foreign key constraints of a specified table.
	 * The method retrieves foreign key information from the database metadata
	 * and constructs a string representation of the foreign key constraints,
	 * which is then written to a BufferedWriter.
	 *
	 * @param tableName The name of the table for which to print foreign key constraints.
	 * @throws SQLException If a database access error occurs or this method is called on a closed connection.
	 * @throws IOException If an I/O error occurs.
	 */
	public static void printForeignKey(String tableName) throws SQLException, IOException {
		// StringBuilder is used to construct the final string
		StringBuilder line = new StringBuilder();
		// Retrieve foreign key information
		try (ResultSet importedKeys = metaData.getImportedKeys(null, null, tableName)) {
			while (importedKeys.next()) {
				String fkColumnName = importedKeys.getString("FKCOLUMN_NAME");
				String pkTableName = importedKeys.getString("PKTABLE_NAME");
				String pkColumnName = importedKeys.getString("PKCOLUMN_NAME");
				// Build and print the foreign key constraint
				String foreignKeyConstraint = String.format(", FOREIGN KEY (%s) REFERENCES %s(%s)", fkColumnName, pkTableName, pkColumnName);
				System.out.print(foreignKeyConstraint);
				line.append(foreignKeyConstraint);
			}
		}
		// Write the constructed string to the BufferedWriter
		bufferedWriter.write(line.toString());
	}


	/**
	 * Prints SQL insert statements for all rows in the specified table.
	 * This method constructs and executes a SQL query to retrieve all data from the table,
	 * then formats and prints out the data as a series of SQL INSERT statements.
	 *
	 * @param tableName The name of the table from which to retrieve and print data.
	 * @throws SQLException If a database access error occurs or this method is called on a closed connection.
	 * @throws IOException If an I/O error occurs while writing to the BufferedWriter.
	 */
	public static void printData(String tableName) throws SQLException, IOException {
		// Construct the SQL query to select all rows from the specified table
		String query = "SELECT * FROM `" + tableName + "`";
		// Use try-with-resources to ensure automatic closure of resources
		try (Statement statement = con.createStatement();
			 ResultSet resultSet = statement.executeQuery(query)) {
			// Retrieve metadata about the result set
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			// Determine the number of columns in the result set
			int columnCount = resultSetMetaData.getColumnCount();
			// Iterate over each row in the result set
			while (resultSet.next()) {
				// Initialize StringBuilder for constructing the INSERT statement
				StringBuilder line = new StringBuilder("INSERT INTO `").append(tableName).append("` VALUES (");
				// Process each column's data
				for (int i = 1; i <= columnCount; i++) {
					// Append formatted value for the current column
					line.append(formatValue(resultSet.getObject(i)));
					// Add a comma separator if not the last column
					if (i < columnCount) {
						line.append(", ");
					}
				}
				// Complete the INSERT statement
				line.append(");");
				// Print and write the INSERT statement to BufferedWriter
				System.out.println(line);
				bufferedWriter.write(line.toString());
				bufferedWriter.newLine();
			}
		}
	}

	/**
	 * Formats a SQL value, handling null, strings, and binary data.
	 *
	 * @param value The value to be formatted.
	 * @return The formatted SQL value as a string.
	 */
	private static String formatValue(Object value) {
		// Handle null values
		if (value == null) {
			return "NULL";
		}
		// Handle string values
		else if (value instanceof String) {
			// Escape single quotes in the string value
			return "'" + ((String) value).replace("'", "''") + "'";
		}
		// Handle binary data (e.g., images)
		else if (value instanceof byte[]) {
			// Convert binary data to a hexadecimal string
			byte[] blobValue = (byte[]) value;
			StringBuilder hexValue = new StringBuilder("X'");
			for (byte b : blobValue) {
				hexValue.append(String.format("%02X", b));
			}
			hexValue.append("'");
			return hexValue.toString();
		}
		// Handle other data types
		else {
			return value.toString();
		}
	}


	/**
	 * Backs up the indexes of a specified table.
	 * This method retrieves index information from the database metadata
	 * and generates SQL statements to drop and recreate each index.
	 * The SQL statements are printed to the console and written to a BufferedWriter.
	 *
	 * @param tableName The name of the table whose indexes are to be backed up.
	 * @throws SQLException If a database access error occurs or this method is called on a closed connection.
	 * @throws IOException If an I/O error occurs while writing to the BufferedWriter.
	 */
	public static void backupIndexes(String tableName) throws SQLException, IOException {
		StringBuilder line = new StringBuilder();
		// Retrieve index information for the specified table
		try (ResultSet indexes = metaData.getIndexInfo(null, null, tableName, false, false)) {
			while (indexes.next()) {
				String indexName = indexes.getString("INDEX_NAME");
				// Skip auto-generated indexes
				if (indexName.contains("sqlite_autoindex")) {
					continue;
				}
				boolean unique = !indexes.getBoolean("NON_UNIQUE");
				// Drop existing index if it exists
				executeUpdate("DROP INDEX IF EXISTS " + indexName + ";", line);
				// Recreate the index
				recreateIndex(tableName, indexName, unique, line);
			}
		}
		// Add an extra newline for separation
		bufferedWriter.newLine();
	}

	/**
	 * Executes an update operation and writes the SQL statement to a BufferedWriter.
	 *
	 * @param sql The SQL statement to execute.
	 * @param line The StringBuilder used to build the SQL statement.
	 * @throws IOException If an I/O error occurs while writing to the BufferedWriter.
	 */
	private static void executeUpdate(String sql, StringBuilder line) throws IOException {
		System.out.println(sql);
		line.setLength(0);
		line.append(sql).append("\n");
		bufferedWriter.write(line.toString());
	}

	/**
	 * Recreates an index for a specified table.
	 * This method queries the database metadata to find all columns included in the index
	 * and generates a SQL statement to create the index, which is then executed and written to a BufferedWriter.
	 *
	 * @param tableName The name of the table on which the index is to be created.
	 * @param indexName The name of the index to recreate.
	 * @param unique Indicates whether the index is unique.
	 * @param line The StringBuilder used to build the SQL statement.
	 * @throws SQLException If a database access error occurs while querying index information.
	 * @throws IOException If an I/O error occurs while writing to the BufferedWriter.
	 */
	private static void recreateIndex(String tableName, String indexName, boolean unique, StringBuilder line) throws SQLException, IOException {
		// Query all columns included in the index
		try (ResultSet indexesbackup = metaData.getIndexInfo(null, null, tableName, false, false)) {
			StringBuilder columns = new StringBuilder();
			while (indexesbackup.next()) {
				if (indexesbackup.getString("INDEX_NAME").equals(indexName)) {
					if (columns.length() > 0) {
						columns.append(", ");
					}
					columns.append(indexesbackup.getString("COLUMN_NAME"));
				}
			}
			// Construct the SQL statement to create the index
			String createIndexSql = "CREATE " + (unique ? "UNIQUE " : "") + "INDEX '" + indexName + "' ON " + tableName + " (" + columns + ");";
			executeUpdate(createIndexSql, line);
		}
	}

	/**
	 * Retrieves and prints the names of columns that have a unique constraint but are not primary keys.
	 * This method queries the database metadata to find all unique constraints on the specified table,
	 * filters out the primary key columns, and returns a set of column names that are unique but not primary keys.
	 *
	 * @param tableName The name of the table to query for unique constraints.
	 * @return A set of column names that are unique and not part of the primary key.
	 * @throws SQLException If a database access error occurs.
	 * @throws IOException If an I/O error occurs.
	 */
	public static Set<String> printNonPrimaryKeyUniqueColumns(String tableName) throws SQLException, IOException {
		// Retrieve the primary key columns for the table
		ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
		Set<String> primaryKeyColumns = new HashSet<>();
		while (primaryKeys.next()) {
			String columnName = primaryKeys.getString("COLUMN_NAME");
			primaryKeyColumns.add(columnName);
		}
		primaryKeys.close();

		// Retrieve the unique constraint columns that are not primary keys
		Set<String> nonPrimaryKeyUniqueColumns = new HashSet<>();
		ResultSet uniqueColumns = metaData.getIndexInfo(null, null, tableName, true, false);
		while (uniqueColumns.next()) {
			String columnName = uniqueColumns.getString("COLUMN_NAME");
			// Check if the column is a primary key
			boolean isPrimaryKey = primaryKeyColumns.contains(columnName);
			// If it's not a primary key, add it to the set
			if (!isPrimaryKey) {
				nonPrimaryKeyUniqueColumns.add(columnName);
			}
		}
		uniqueColumns.close();

		// Return the set of non-primary key unique columns
		return nonPrimaryKeyUniqueColumns;
	}
}
