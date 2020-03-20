package by.gsu.epamlab.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import by.gsu.epamlab.constants.ConstantsJSP;
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.factories.TaskFactory;
import by.gsu.epamlab.utilities.FileUtilities;
import by.gsu.epamlab.utilities.Utilities;

@WebServlet("/deleteFile")
public class DeleteFileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(DeleteFileController.class.getName());

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idTaskStr = request.getParameter(ConstantsJSP.ID_TASK_NAME);
		int idTask = Integer.parseInt(idTaskStr);
		String redirectPath = Utilities.getRedirectPath(request);
		TaskDao taskDao = TaskFactory.getClassFromFactory();
		try {
			FileUtilities.deleteFileById(idTask);
			taskDao.deleteFile(idTask);
			response.sendRedirect(redirectPath);
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			response.sendRedirect(
					redirectPath + "&" + ConstantsJSP.ERROR_NAME + "=" + ConstantsJSP.CONNECTION_ERROR_MESSAGE);
		}
	}
}