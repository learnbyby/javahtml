package by.gsu.epamlab.model.enums;

import java.util.List;

import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.factories.TaskFactory;
import by.gsu.epamlab.utilities.FileUtilities;

public enum Operation {

	FIX (Task.FIXED),
	REMOVE (Task.BIN),
	RESTORE (Task.TO_DO),
	DELETE (0);
	
	private int newStatus;
	
	private Operation(int newStatus) {
		this.newStatus = newStatus;
	}

	public void doOperation(List<Integer> idTasks) throws DaoException{
		if (this != DELETE) {
			taskDao.updateStatus(idTasks, newStatus);
		}
		else {
			FileUtilities.deleteFilesById(idTasks);
			taskDao.deleteTasks(idTasks);
		}	
	};

	private static TaskDao taskDao = TaskFactory.getClassFromFactory();
}
