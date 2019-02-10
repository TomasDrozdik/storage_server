package ftpserver.structure;

import ftpserver.structure.files.HomeDir;
import ftpserver.structure.files.UserProperty;
import ftpserver.user_manager.users.BaseUser;

public interface IUserStructure {
	/**
	 * Get users home folder.
	 *
	 * @param u user
	 * @return non null HomeDir
	 */
	HomeDir getHome(BaseUser u);

	/**
	 * Get user property for given user.
	 *
	 * @param u user
	 * @return non null UserProperty
	 */
	UserProperty getUserProperty(BaseUser u);

}
