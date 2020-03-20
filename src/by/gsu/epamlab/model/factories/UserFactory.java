package by.gsu.epamlab.model.factories;

import javax.servlet.ServletContext;

import by.gsu.epamlab.exceptions.InitException;
import by.gsu.epamlab.ifaces.UserDao;
import by.gsu.epamlab.model.impl.ServiceDb;
import by.gsu.epamlab.model.impl.UserImplDb;
import by.gsu.epamlab.model.impl.UserImplMemory;

public class UserFactory {
	private static UserDao userImpl;

	private enum Sources {
		DB {
			@Override
			UserDao getImpl(ServletContext sc) throws InitException {
				String dbName = sc.getInitParameter("db.name");
				String user = sc.getInitParameter("db.user");
				String password = sc.getInitParameter("db.password");
				ServiceDb.init(dbName, user, password);
				return new UserImplDb();
			}
		},
		MEMORY {
			@Override
			UserDao getImpl(ServletContext sc) throws InitException {
				return new UserImplMemory();
			}
		};

		abstract UserDao getImpl(ServletContext sc) throws InitException;
	}

	public static void init(ServletContext sc) throws InitException {
		String sourceType = sc.getInitParameter("source.user");
		Sources source = Sources.valueOf(sourceType.toUpperCase());
		userImpl = source.getImpl(sc);
	}

	public static UserDao getClassFromFactory() {
		return userImpl;
	}
}