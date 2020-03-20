package by.gsu.epamlab.model.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.gsu.epamlab.constants.ConstantsDb;
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.UserDao;
import by.gsu.epamlab.model.beans.User;

public class UserImplDb implements UserDao {
	private static final Logger LOGGER = Logger.getLogger(UserImplDb.class.getName());

	@Override
	public Optional<User> getUser(String account, String passwordHash) throws DaoException {
		User user = null;
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement ps = connection.prepareStatement(ConstantsDb.SQL_LOGIN_SELECT)) {
			ps.setString(1, account);
			ps.setString(2, passwordHash);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int idUser = rs.getInt(1);
					user = new User(idUser, account);
				}
				return Optional.ofNullable(user);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
				throw new DaoException(e);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public Optional<User> addAndGetUser(String account, String passwordHash) throws DaoException {
		User user = null;
		boolean registrateStatus = false;
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement psSelect = connection.prepareStatement(ConstantsDb.SQL_REGISTRATE_SELECT);
				PreparedStatement psInsert = connection.prepareStatement(ConstantsDb.SQL_REGISTRATE_INSERT)) {
			psSelect.setString(ConstantsDb.ACCOUNT_INDEX, account);
			psInsert.setString(ConstantsDb.ACCOUNT_INDEX, account);
			psInsert.setString(ConstantsDb.PASSWORDHASH_INDEX, passwordHash);
			synchronized (this) {
				try (ResultSet resultSet = psSelect.executeQuery()) {
					if (!resultSet.next()) {
						psInsert.executeUpdate();
						registrateStatus = true;
					}
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
					throw new DaoException(e);
				}
			}
			if (registrateStatus) {
				try (ResultSet resultSet = psSelect.executeQuery()) {
					if (resultSet.next()) {
						int idUser = resultSet.getInt(1);
						user = new User(idUser, account);
					}
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
					throw new DaoException(e);
				}
			}
			return Optional.ofNullable(user);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new DaoException(e);
		}
	}
}