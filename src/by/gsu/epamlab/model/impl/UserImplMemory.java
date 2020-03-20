package by.gsu.epamlab.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.UserDao;
import by.gsu.epamlab.model.beans.User;

public class UserImplMemory implements UserDao {
	private static List<User> users = new ArrayList<User>();
	private static int userCounter = 0;

	@Override
	public Optional<User> getUser(String account, String passwordHash) throws DaoException {
		User user = null;
		for (User us : users) {
			if (account.equals(us.getAccount()) & passwordHash.equals(us.getPasswordHash())) {
				user = us;
			}
		}
		return Optional.ofNullable(user);
	}

	@Override
	public Optional<User> addAndGetUser(String account, String passwordHash) throws DaoException {
		User user = null;
		boolean userExists = false;
		synchronized (this) {
			for (User us : users) {
				if (account.equals(us.getAccount())) {
					userExists = true;
				}
			}
			if (!userExists) {
			userCounter++;
			user = new User(userCounter, account, passwordHash);
			users.add(user);
			}			
		}
		return Optional.ofNullable(user);
	}
}