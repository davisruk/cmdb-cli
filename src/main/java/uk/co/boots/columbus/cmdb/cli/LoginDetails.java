package uk.co.boots.columbus.cmdb.cli;

public class LoginDetails {

	private String password;
	private String username;

	public LoginDetails(String username, String password) {
		super();
		this.password = password;
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "LoginDetails [password=" + password + ", username=" + username + "]";
	}
	
}
