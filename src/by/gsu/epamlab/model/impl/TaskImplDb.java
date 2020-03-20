package by.gsu.epamlab.model.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.gsu.epamlab.constants.ConstantsDb;
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;

public class TaskImplDb implements TaskDao {
	private static final Logger LOGGER = Logger.getLogger(TaskImplDb.class.getName());

	@Override
	public Task addTask(int userId, String text, Date date, String file) throws DaoException {
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement ps = connection.prepareStatement(ConstantsDb.SQL_ADD_TASK,
						PreparedStatement.RETURN_GENERATED_KEYS)) {
			ps.setInt(ConstantsDb.USER_ID_INDEX, userId);
			ps.setString(ConstantsDb.TEXT_INDEX, text);
			ps.setDate(ConstantsDb.DATE_INDEX, date);
			ps.setInt(ConstantsDb.STATUS_INDEX, Task.TO_DO);
			ps.setString(ConstantsDb.FILE_INDEX, file);
			ps.executeUpdate();
			int idTask = 0;
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					idTask = rs.getInt(1);
				} else {
					LOGGER.log(Level.SEVERE, ConstantsDb.NO_GENERATED_KEYS_ERROR);
					throw new DaoException(ConstantsDb.NO_GENERATED_KEYS_ERROR);
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
				throw new DaoException(e);
			}
			return new Task(idTask, userId, text, date, Task.TO_DO, file);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public Optional<Task> getTaskById(int idTask) throws DaoException {
		Task task = null;
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement ps = connection.prepareStatement(ConstantsDb.SQL_GET_TASK)) {
			ps.setInt(1, idTask);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt(ConstantsDb.USER_ID_NAME);
					String text = rs.getString(ConstantsDb.TEXT_NAME);
					Date date = rs.getDate(ConstantsDb.DATE_NAME);
					int status = rs.getInt(ConstantsDb.STATUS_NAME);
					String file = rs.getString(ConstantsDb.FILE_NAME);
					task = new Task(idTask, userId, text, date, status, file);
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
				throw new DaoException(e);
			}
			return Optional.ofNullable(task);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<Task> getTasksByUserId(int userId) throws DaoException {
		List<Task> tasks = new ArrayList<Task>();
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement ps = connection.prepareStatement(ConstantsDb.SQL_GET_TASKS)) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int idTask = rs.getInt(ConstantsDb.ID_TASK_NAME);
					String text = rs.getString(ConstantsDb.TEXT_NAME);
					Date date = rs.getDate(ConstantsDb.DATE_NAME);
					int status = rs.getInt(ConstantsDb.STATUS_NAME);
					String file = rs.getString(ConstantsDb.FILE_NAME);
					Task task = new Task(idTask, userId, text, date, status, file);
					tasks.add(task);
				}
				return tasks;
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
	public void deleteTasks(List<Integer> idTasks) throws DaoException {
		executeSQL(idTasks, ConstantsDb.SQL_DELETE_TASKS);
	}

	public void updateStatus(List<Integer> idTasks, int newStatus) throws DaoException {
		String sql = ConstantsDb.SQL_UPDATE_TASKS_1 + newStatus + ConstantsDb.SQL_UPDATE_TASKS_2;
		executeSQL(idTasks, sql);
	}

	private void executeSQL(List<Integer> idTasks, String sql) throws DaoException {
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			for (Integer idTask : idTasks) {
				ps.setInt(1, idTask);
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public void addFile(int idTask, String file) throws DaoException {
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement ps = connection.prepareStatement(ConstantsDb.SQL_ADD_FILE)) {
			ps.setString(1, file);
			ps.setInt(2, idTask);
			ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public void deleteFile(int idTask) throws DaoException {
		try (Connection connection = ServiceDb.getConnection();
				PreparedStatement ps = connection.prepareStatement(ConstantsDb.SQL_DELETE_FILE)) {
			ps.setInt(1, idTask);
			ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new DaoException(e);
		}
	}	
}