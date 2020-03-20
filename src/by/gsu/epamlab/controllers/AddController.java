package by.gsu.epamlab.controllers;

import java.io.IOException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import by.gsu.epamlab.constants.ConstantsJSP;
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.beans.User;
import by.gsu.epamlab.model.factories.TaskFactory;
import by.gsu.epamlab.utilities.FileUtilities;
import by.gsu.epamlab.utilities.Utilities;

@WebServlet("/add")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
		maxFileSize = 1024 * 1024 * 50, // 50 MB
		maxRequestSize = 1024 * 1024 * 100) // 100 MB
public class AddController extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(AddController.class.getName());
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String text = request.getParameter(ConstantsJSP.TEXT_NAME);
		String dateStr = request.getParameter(ConstantsJSP.DATE_NAME);
		String[] dateParts = dateStr.split("/");
		Date date = Date.valueOf(dateParts[2] + "-" + dateParts[0] + "-" + dateParts[1]);
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(ConstantsJSP.USER_NAME);
		int userId = user.getId();
		TaskDao taskDao = TaskFactory.getClassFromFactory();
		Task task = null;
		String fileName = FileUtilities.addTemporaryFile(request);
		String redirectPath = Utilities.getRedirectPath(request);
		try {
			task = taskDao.addTask(userId, text, date, fileName);
			if (fileName != null) {
				int idTask = task.getId();
				FileUtilities.renameTemporaryFile(fileName, idTask);
			}
			response.sendRedirect(redirectPath);
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			FileUtilities.deleteTemporaryFile(fileName);
			response.sendRedirect(
					redirectPath + "&" + ConstantsJSP.ERROR_NAME + "=" + ConstantsJSP.CONNECTION_ERROR_MESSAGE);
		}
	}
}