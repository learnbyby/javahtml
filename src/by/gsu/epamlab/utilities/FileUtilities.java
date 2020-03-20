package by.gsu.epamlab.utilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.factories.TaskFactory;

public class FileUtilities {
	private static final Logger LOGGER = Logger.getLogger(FileUtilities.class.getName());
	private static String uploadPath;

	public static String getUploadPath() {
		return uploadPath;
	}

	public static void setUploadPath(String path) {
		uploadPath = path;
	}

	public static String addTemporaryFile(HttpServletRequest request) throws IOException, ServletException {
		return addFile(request, "temp");
	}

	public static String addPermanentFile(HttpServletRequest request, int idTask) throws IOException, ServletException {
		return addFile(request, String.valueOf(idTask));
	}

	public static void deleteTemporaryFile(String fileName) {
		File file = new File(uploadPath + File.separator + "temp_" + fileName);
		file.delete();
	}

	public static void renameTemporaryFile(String fileName, int idTask) {
		File file = new File(uploadPath + File.separator + "temp_" + fileName);
		File newFile = new File(uploadPath + File.separator + idTask + "_" + fileName);
		file.renameTo(newFile);
	}

	public static void deleteFileById(int idTask) {
		TaskDao taskDao = TaskFactory.getClassFromFactory();
		try {
			Optional<Task> task = taskDao.getTaskById(idTask);
			if (task.isPresent()) {
				deleteFile(task.get().getId() + "_" + task.get().getFile());
			}
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}

	}

	public static void deleteFile(String fileName) {
		File file = new File(uploadPath + File.separator + fileName);
		file.delete();
	}

	public static void deleteFilesById(List<Integer> idTasks) {
		for (Integer idTask : idTasks) {
			deleteFileById(idTask);
		}
	}

	private static String addFile(HttpServletRequest request, String prefix) throws IOException, ServletException {
		String fileName = null;
		for (Part part : request.getParts()) {
			String currentName = extractFileName(part);
			if (currentName != null) {
				fileName = currentName;
				try {
					part.write(uploadPath + File.separator + prefix + "_" + fileName);
				} catch (IOException e) {
					fileName = null;
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
			}
		}
		return fileName;
	}

	private static String extractFileName(Part part) {
		String fileName = null;
		String contentDisposition = part.getHeader("content-disposition");
		String[] items = contentDisposition.split(";");
		for (String item : items) {
			if (item.trim().startsWith("filename")) {
				fileName = item.substring(item.indexOf("=") + 2, item.length() - 1);
			}
		}
		return fileName;
	}
}