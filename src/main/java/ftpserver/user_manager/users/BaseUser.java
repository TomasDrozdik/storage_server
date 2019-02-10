package ftpserver.user_manager.users;

import ftpserver.structure.IUserStructure;
import ftpserver.structure.Structure;

public class BaseUser extends User {
	private String username;
	private IUserStructure storage;

	public BaseUser(String username) {
		this.username = username;
		this.storage = Structure.getStructure();
	}

	public String getUsername() {
		return username;
	}

}
