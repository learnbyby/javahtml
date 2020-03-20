package by.gsu.epamlab.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import by.gsu.epamlab.constants.ConstantsJSP;
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.factories.TaskFactory;
import by.gsu.epamlab.utilities.FileUtilities;
import by.gsu.epamlab.utilities.Utilities;

@WebServlet("/download")
public class DownloadController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(DownloadController.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idTaskStr = request.getParameter(ConstantsJSP.ID_TASK_NAME);

		int idTask = Integer.parseInt(idTaskStr);
		TaskDao taskDao = TaskFactory.getClassFromFactory();
		String redirectPath = Utilities.getRedirectPath(request) + "&" + ConstantsJSP.ERROR_NAME + "=";
		try {
			Optional<Task> optionalTask = taskDao.getTaskById(idTask);
			Task task = optionalTask.get();
			if (task.getFileStatus() == 1) {
				// download file from server
				String fileName = task.getFile();
				String uploadPath = FileUtilities.getUploadPath();
				File file = new File(uploadPath + File.separator + task.getId() + "_" + fileName);
				if (file.exists()) {
					ServletContext ctx = getServletContext();
					InputStream fis = new FileInputStream(file);
					String mimeType = ctx.getMimeType(file.getAbsolutePath());
					response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
					response.setContentLength((int) file.length());
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
					ServletOutputStream os = response.getOutputStream();
					byte[] bufferData = new byte[1024];
					int read = 0;
					while ((read = fis.read(bufferData)) != -1) {
						os.write(bufferData, 0, read);
					}
					os.flush();
					os.close();
					fis.close();
				} else {
					response.sendRedirect(redirectPath + ConstantsJSP.FILE_NOT_FOUND_MESSAGE);
				}
			} else {
				response.sendRedirect(redirectPath + ConstantsJSP.NO_FILE_IN_TASK_MESSAGE);
			}
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			response.sendRedirect(redirectPath + ConstantsJSP.CONNECTION_ERROR_MESSAGE);
		}
	}
}