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

import javax.servlet.annotation.MultipartConfig;

@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
		maxFileSize = 1024 * 1024 * 50, // 50 MB
		maxRequestSize = 1024 * 1024 * 100) // 100 MB
public class UploadController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(UploadController.class.getName());

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idTaskStr = request.getParameter(ConstantsJSP.ID_TASK_NAME);
		int idTask = Integer.parseInt(idTaskStr);
		String fileName = FileUtilities.addPermanentFile(request, idTask);
		TaskDao taskDao = TaskFactory.getClassFromFactory();
		String redirectPath = Utilities.getRedirectPath(request);
		try {
			taskDao.addFile(idTask, fileName);
			response.sendRedirect(redirectPath);
		} catch (DaoException e) {
			if (fileName != null) {
				FileUtilities.deleteFile(idTask + "_" + fileName);
			}
			LOGGER.log(Level.SEVERE, e.toString(), e);
			response.sendRedirect(redirectPath + "&" + ConstantsJSP.ERROR_NAME + "=" + ConstantsJSP.CONNECTION_ERROR_MESSAGE);
		}
	}
}