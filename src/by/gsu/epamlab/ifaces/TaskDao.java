package by.gsu.epamlab.ifaces;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.model.beans.Task;

public interface TaskDao {
	
	Task addTask(int userId, String text, Date date, String file) throws DaoException;

	Optional<Task> getTaskById(int idTask) throws DaoException;

	List<Task> getTasksByUserId(int userId) throws DaoException;

	void updateStatus(List<Integer> idTasks, int status) throws DaoException;

	void deleteTasks(List<Integer> idTasks) throws DaoException;

	void addFile(int idTask, String file) throws DaoException;

	void deleteFile(int idTask) throws DaoException;
}