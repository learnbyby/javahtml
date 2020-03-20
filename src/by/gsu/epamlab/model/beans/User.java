package by.gsu.epamlab.model.beans;

public class User {

	private int id;
	private String account;
	private String passwordHash;

	public User() {
		super();
	}

	public User(int id, String account, String passwordHash) {
		super();
		this.id = id;
		this.account = account;
		this.passwordHash = passwordHash;
	}

	public User(int id, String account) {
		super();
		this.id = id;
		this.account = account;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}