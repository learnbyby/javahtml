package by.gsu.epamlab.model.beans;

import java.sql.Date;

public class Task {

	public static final int TO_DO = 1;
	public static final int FIXED = 2;
	public static final int BIN = 3;

	private int id;
	private int userId;
	private String text;
	private Date date;
	private int status; // 1 - To do, 2 - Fixed, 3 - Bin
	private String file;

	public Task(int id, int userId, String text, Date date, int status) {
		super();
		this.id = id;
		this.userId = userId;
		this.text = text;
		this.date = date;
		this.status = status;
	}

	public Task(int id, int userId, String text, Date date, int status, String file) {
		super();
		this.id = id;
		this.userId = userId;
		this.text = text;
		this.date = date;
		this.status = status;
		this.file = file;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getFileStatus() {
		boolean result = false;
		if (file != null) {
			result = !file.equals("");
		}
		return result ? 1 : 0;
	}

	@Override
	public String toString() {
		return String.format("%d, %s, %s, %s, %d", id, text, date, file, getFileStatus());
	}
}