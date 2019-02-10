package ftpserver.user_manager.users;

import ftpserver.structure.IAdminStructure;
import ftpserver.structure.Structure;

public class AdminUser extends User {
	private String username;
	private IAdminStructure storage;

	public AdminUser(String username) {
		this.username = username;
		this.storage = Structure.getStructure();
	}

	public String getUsername() {
		return username;
	}

}
