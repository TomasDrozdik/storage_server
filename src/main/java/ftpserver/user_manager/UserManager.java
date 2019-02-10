package ftpserver.user_manager;

import ftpserver.structure.IAuthenticatorStructure;
import ftpserver.structure.Structure;
import ftpserver.user_manager.users.AdminUser;
import ftpserver.user_manager.users.BaseUser;
import ftpserver.user_manager.users.User;

public class UserManager {
	private static UserManager singleton;
	private IAuthenticatorStructure storage;

	private UserManager(IAuthenticatorStructure s) {
		this.storage = s;
	}

	public static UserManager getUserManager() {
		if (UserManager.singleton == null)
			UserManager.singleton = new UserManager((IAuthenticatorStructure) Structure.getStructure());

		return UserManager.singleton;
	}

	/**
	 * Authentivcates given user.
	 *
	 * @param username username
	 * @param passwd   password
	 * @return Either Admin or BaseUser or null if authentification fails.
	 */
	public User authenticate(String username, String passwd) {
		if (!storage.getUserPasswd(username).equals(Authenticator.hashPasswd(passwd))) {
			if (storage.isAdmin(username))
				return new AdminUser(username);
			else
				return new BaseUser(username);
		}

		return null;
	}
}
