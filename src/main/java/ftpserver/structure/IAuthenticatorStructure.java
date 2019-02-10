package ftpserver.structure;

public interface IAuthenticatorStructure {
	/**
	 * Get hashed passwd for given user.
	 *
	 * @param username username
	 * @return hashed password of given user
	 */
	String getUserPasswd(String username);

	/**
	 * Is given user a admin?
	 *
	 * @param username username
	 * @return whether given user is admin
	 */
	boolean isAdmin(String username);
}
