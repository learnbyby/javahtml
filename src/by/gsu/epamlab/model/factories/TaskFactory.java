package by.gsu.epamlab.model.factories;

import javax.servlet.ServletContext;

import by.gsu.epamlab.exceptions.InitException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.impl.ServiceDb;
import by.gsu.epamlab.model.impl.TaskImplDb;
import by.gsu.epamlab.model.impl.TaskImplMemory;

public class TaskFactory {
	private static TaskDao taskImpl;

	private enum Sources {
		DB {
			@Override
			TaskDao getImpl(ServletContext sc) throws InitException {
				String dbName = sc.getInitParameter("db.name");
				String user = sc.getInitParameter("db.user");
				String password = sc.getInitParameter("db.password");
				ServiceDb.init(dbName, user, password);
				return new TaskImplDb();
			}
		},
		MEMORY {
			@Override
			TaskDao getImpl(ServletContext sc) throws InitException {
				return new TaskImplMemory();
			}
		};

		abstract TaskDao getImpl(ServletContext sc) throws InitException;
	}

	public static void init(ServletContext sc) throws InitException {
		String sourceType = sc.getInitParameter("source.task");
		Sources source = Sources.valueOf(sourceType.toUpperCase());
		taskImpl = source.getImpl(sc);
	}

	public static TaskDao getClassFromFactory() {
		return taskImpl;
	}
}