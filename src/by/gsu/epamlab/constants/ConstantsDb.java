package by.gsu.epamlab.constants;

public class ConstantsDb {
	public static final String SQL_LOGIN_SELECT = "SELECT idUser, account FROM users WHERE account=? and passwordHash=?";
	public static final String SQL_REGISTRATE_SELECT = "SELECT idUser, account FROM users WHERE account=?";
	public static final String SQL_REGISTRATE_INSERT = "INSERT INTO users(account, passwordHash) VALUES(?, ?)";
	
	public static final String SQL_ADD_TASK = "INSERT INTO tasks(userId, text, date, status, file) VALUES(?, ?, ?, ?, ?)";
	public static final String SQL_GET_TASK = "SELECT userId, text, date, status, file FROM tasks WHERE idTask = ?";
	public static final String SQL_GET_TASKS = "SELECT idTask, text, date, status, file FROM tasks WHERE userId = ?";
	public static final String SQL_DELETE_TASKS = "DELETE FROM tasks WHERE idTask = ?";
	public static final String SQL_UPDATE_TASKS_1 = "UPDATE tasks SET status = ";
	public static final String SQL_UPDATE_TASKS_2 = " WHERE idTask = ?";
	public static final String SQL_ADD_FILE = "UPDATE tasks SET file = ? WHERE idTask = ?";
	public static final String SQL_DELETE_FILE = "UPDATE tasks SET file = null WHERE idTask = ?";

	public static final String ID_TASK_NAME = "idTask";
	public static final String USER_ID_NAME = "userId";
	public static final String TEXT_NAME = "text";
	public static final String DATE_NAME = "date";
	public static final String STATUS_NAME = "status";
	public static final String FILE_NAME = "file";

	public static final int ACCOUNT_INDEX = 1;
	public static final int PASSWORDHASH_INDEX = 2;
	
	public static final int USER_ID_INDEX = 1;
	public static final int TEXT_INDEX = 2;
	public static final int DATE_INDEX = 3;
	public static final int STATUS_INDEX = 4;
	public static final int FILE_INDEX = 5;
	
	public static final String NO_GENERATED_KEYS_ERROR = "Can not get generated keys";
}