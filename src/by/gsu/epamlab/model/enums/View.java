package by.gsu.epamlab.model.enums;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.factories.TaskFactory;
import by.gsu.epamlab.utilities.Utilities;

public enum View {

	TODAY(t -> t.getDate().before(Utilities.getTomorrow()) & t.getStatus() == Task.TO_DO),
	TOMORROW(t -> t.getDate().equals(Utilities.getTomorrow()) & t.getStatus() == Task.TO_DO),
	SOMEDAY(t -> t.getDate().after(Utilities.getTomorrow()) & t.getStatus() == Task.TO_DO),
	FIXED(t -> t.getStatus() == Task.FIXED), BIN(t -> t.getStatus() == Task.BIN);

	private Predicate<Task> tasksFilter;

	View(Predicate<Task> tasksFilter) {
		this.tasksFilter = tasksFilter;
	}

	// selecting tasks by date or status from all user's tasks
	public List<Task> getTasks(int userId) throws DaoException {
		List<Task> selectedTasks = taskDao.getTasksByUserId(userId).stream().filter(tasksFilter)
				.collect(Collectors.toList());
		selectedTasks.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
		return selectedTasks;
	}

	private static TaskDao taskDao = TaskFactory.getClassFromFactory();
}