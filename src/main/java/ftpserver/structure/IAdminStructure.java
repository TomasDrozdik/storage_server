package ftpserver.structure;

public interface IAdminStructure {
	/**
	 * Add user to the structure.
	 *
	 * @param username username
	 * @param password password
	 * @param isAdmin  bool specifying admin privileges
	 * @return true if operation was successful, false otherwise
	 * @throws StructureException
	 */
	boolean addUser(String username, String password, boolean isAdmin) throws StructureException;
}
