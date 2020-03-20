package by.gsu.epamlab.model.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;

public class TaskImplMemory implements TaskDao {
	private static Map<Integer, Task> tasks = new HashMap<Integer, Task>();
	private static int taskCounter = 0;

	@Override
	public Task addTask(int userId, String text, Date date, String file) throws DaoException {
		Task task = null;
		synchronized (this) {
			taskCounter++;
			task = new Task(taskCounter, userId, text, date, Task.TO_DO, file);
			tasks.put(taskCounter, task);
		}
		return task;
	}

	@Override
	public Optional<Task> getTaskById(int idTask) throws DaoException {
		return Optional.ofNullable(tasks.get(idTask));
	}

	@Override
	public List<Task> getTasksByUserId(int userId) {
		return tasks.values().stream().filter(t -> t.getUserId() == userId).collect(Collectors.toList());
	}

	@Override
	public void updateStatus(List<Integer> idTasks, int status) throws DaoException {
		for (Integer id : idTasks) {
			tasks.get(id).setStatus(status);
		}
	}

	@Override
	public void deleteTasks(List<Integer> idTasks) throws DaoException {
		for (Integer id : idTasks) {
			tasks.remove(id);
		}
	}

	@Override
	public void addFile(int idTask, String file) throws DaoException {
		tasks.get(idTask).setFile(file);
	}

	@Override
	public void deleteFile(int idTask) throws DaoException {
		tasks.get(idTask).setFile(null);
	}
}