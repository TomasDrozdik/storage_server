package ftpserver.structure;

import ftpserver.structure.files.*;
import ftpserver.user_manager.Authenticator;
import ftpserver.user_manager.users.BaseUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

/**
 * Singleton class used as interface to the file structure behind the server.
 * <p>
 * Supports loading existing coherent file structure or creating new one.
 */
public class Structure implements IUserStructure, IAdminStructure {
	private static String rootDirName = "storage", passwdName = "passwd", logName = "log", propertiesName = "properties",
			homeName = "home";
	private static Structure s = null;
	private Path rootDir;
	private LogFile log;
	private HomesDir home;
	private PropertyDir properties;
	private PasswdFile passwd;
	private SecureRandom random = new SecureRandom();

	private Structure(Path rootDir, PasswdFile passwdFile, LogFile logFile, HomesDir homeDir,
	                  PropertyDir properties) {
		this.rootDir = rootDir;
		this.passwd = passwdFile;
		this.log = logFile;
		this.home = homeDir;
		this.properties = properties;
	}

	/**
	 * Creation of singleton class Structure.
	 *
	 * @param rootDir root directory of the database structure
	 * @return found structure or new created one
	 */
	public static Structure createStructure(Path rootDir) throws StructureException {
		assert (s == null);

		Structure s = tryExistingStructure(rootDir);

		if (s == null)
			s = initDefault(rootDir);

		return s;
	}

	/**
	 * Get Structure
	 *
	 * @return non null Structure
	 */
	public static Structure getStructure() {
		assert (s != null);

		return s;
	}

	private static Structure tryExistingStructure(Path path) throws StructureException {
		Path p;
		PasswdFile passwdFile;
		LogFile logFile;
		HomesDir homesDir;
		PropertyDir propertyDir;

		if (!Files.isDirectory(path))
			return null;

		/* Check for correct structure starting with passwd file. */
		p = Paths.get(path.toString(), passwdName);
		if (!Files.exists(p))
			return null;
		passwdFile = PasswdFile.createPasswdFile(p);

		/* Check for log directory. */
		p = Paths.get(path.toString(), logName);
		if (!Files.exists(p))
			return null;
		logFile = LogFile.createLogFile(p);

		/* Check for properties directory. */
		p = Paths.get(path.toString(), propertiesName);
		if (!Files.exists(p))
			return null;
		propertyDir = PropertyDir.createPropertyDir(p, passwdFile);

		/* Check for home directory. */
		p = Paths.get(path.toString(), homeName);
		if (!Files.exists(p))
			return null;
		homesDir = HomesDir.createHomesDir(p, passwdFile, propertyDir);

		return new Structure(path, passwdFile, logFile, homesDir, propertyDir);
	}

	/**
	 * If no existing structure is found create new one in dir path.
	 *
	 * @param path Path to dir in which the storage should be created.
	 * @return Not null Newly created structure.
	 * @throws StructureException if creation was not possible, reason specified inside exception Type.
	 */
	private static Structure initDefault(Path path) throws StructureException {
		Path rootDir, p;
		PasswdFile passwdFile;
		LogFile logFile;
		HomesDir homesDir;
		PropertyDir propertyDir;

		if (Files.isDirectory(path))
			throw new StructureException(StructureException.Type.NOT_DIRECTORY);

		try {
			/* Create root dir */
			rootDir = Files.createDirectory(Paths.get(path.toString(), rootDirName));

			/* Create passwd file */
			passwdFile = PasswdFile.createPasswdFile(Files.createFile(Paths.get(path.toString(), passwdName)));

			/* Create log file */
			logFile = LogFile.createLogFile(Files.createFile(Paths.get(path.toString(), logName)));

			/* Create properties dir */
			propertyDir = PropertyDir.createPropertyDir(Files.createDirectory(Paths.get(path.toString(),
					propertiesName)), passwdFile);

			/* Create home dir */
			homesDir = HomesDir.createHomesDir(Files.createDirectory(Paths.get(path.toString(), homeName)),
					passwdFile, propertyDir);
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		} catch (SecurityException e) {
			throw new StructureException(StructureException.Type.SECURITY_EXCEPTION);
		}

		return new Structure(rootDir, passwdFile, logFile, homesDir, propertyDir);
	}

	/* ---------------------------------------- IUserStructure ---------------------------------------- */

	/**
	 * Get users home folder.
	 *
	 * @param u user
	 * @return non null HomeDir
	 */
	public HomeDir getHome(BaseUser u) {
		return home.getHomedir(u.getUsername());
	}

	/**
	 * Get user property for given user.
	 *
	 * @param u user
	 * @return non null UserProperty
	 */
	public UserProperty getUserProperty(BaseUser u) {
		return properties.getUserProperty(u.getUsername());
	}

	/* ---------------------------------------- IAuthenticatorStructure ---------------------------------------- */

	/**
	 * Get hashed passwd for given user.
	 *
	 * @param username username
	 * @return hashed password of given user
	 */
	public String getUserPasswd(String username) {
		return passwd.getPasswd(username);
	}

	/**
	 * Is given user a admin?
	 *
	 * @param username username
	 * @return whether given user is admin
	 */
	public boolean isAdmin(String username) {
		return passwd.isAdmin(username);
	}

	/* ---------------------------------------- IAdminStructure ---------------------------------------- */

	/**
	 * Add user to the structure.
	 *
	 * @param username username
	 * @param password password
	 * @param isAdmin  bool specifying admin privileges
	 * @return true if operation was successful, false otherwise
	 * @throws StructureException
	 */
	public boolean addUser(String username, String password, boolean isAdmin) throws StructureException {
		UserProperty up;

		return (passwd.addUser(username, Authenticator.hashPasswd(password), isAdmin, random.nextLong()) &&
				(up = properties.addNewPropertyFile(username)) != null &&
				home.addNewHomeDir(username, up) != null);
	}

}
