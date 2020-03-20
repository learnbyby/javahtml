package by.gsu.epamlab.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.CDL;
import org.json.JSONArray;

import by.gsu.epamlab.constants.ConstantsJSP;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.beans.User;

public class Utilities {
	
	public static String getPasswordHash(String password) {
		String generatedPassword = null;
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			// Add password bytes to digest
			md.update(password.getBytes());
			// Get the hash's bytes
			byte[] bytes = md.digest();
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			// Get complete hashed password in hex format
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	public static String getJsonTasks(List<Task> tasks) {
		if (tasks.size() == 0) {
			return "[]";
		}
		JSONArray ja = new JSONArray();
		ja.put("id");
		ja.put("text");
		ja.put("date");
		ja.put("file");
		ja.put("fileStatus");
		String strTasks = tasks.stream().map(Task::toString).collect(Collectors.joining(" \n"));
		JSONArray jsonTasks = CDL.toJSONArray(ja, strTasks);
		return jsonTasks.toString();
	}

	public static String getRedirectPath(HttpServletRequest request) {
		String view = request.getParameter(ConstantsJSP.VIEW_NAME);
		if (view == null) {
			view = ConstantsJSP.DEFAULT_VIEW;
		}
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(ConstantsJSP.USER_NAME);
		return request.getContextPath() + ConstantsJSP.MAIN_VIEW_URL + view + "&" + ConstantsJSP.USER_NAME + "="
				+ user.getAccount();
	}

	// get YYYY-MM-DD
	public static Date getTomorrow() {
		DateTimeFormatter sqlDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		return Date.valueOf(sqlDateFormat.format(tomorrow));
	}
}