package by.gsu.epamlab.model.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.gsu.epamlab.exceptions.InitException;

public class ServiceDb {
	private static final Logger LOGGER = Logger.getLogger(ServiceDb.class.getName());

	private String dbUrl;
	private String user;
	private String password;

	private static ServiceDb serviceDb = null;

	private ServiceDb(String dbName, String user, String password) {
		super();
		this.dbUrl = "jdbc:mysql://localhost:3306/" + dbName + "?serverTimezone=Europe/Minsk";
		this.user = user;
		this.password = password;
	}

	public static void init(String dbName, String user, String password) throws InitException {
		if (serviceDb != null) {
			return;
		}
		try {
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
			serviceDb = new ServiceDb(dbName, user, password);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new InitException(e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(serviceDb.dbUrl, serviceDb.user, serviceDb.password);
	}
}