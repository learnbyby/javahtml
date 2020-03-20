package by.gsu.epamlab.ifaces;

import java.util.Optional;

import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.model.beans.User;

public interface UserDao {
	Optional<User> getUser(String account, String passwordHash) throws DaoException;
	Optional<User> addAndGetUser(String account, String passwordHash) throws DaoException;
}