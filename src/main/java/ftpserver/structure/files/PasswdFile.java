package ftpserver.structure.files;

import ftpserver.structure.StructureException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PasswdFile is used as a interface to the file storing registered users and their passwords.
 * <p>
 * Format:
 * [user_name]:[hashed_password]
 */
public class PasswdFile {
	Map<String, PasswdRecord> data;
	private Path path;

	private PasswdFile(Path p, Map<String, PasswdRecord> data) {
		this.path = p;
		this.data = data;
	}

	/**
	 * Creates interface to passwd file.
	 *
	 * @param p path to the passwd file
	 * @return non null PasswdFile otherwise exception is thrown
	 * @throws StructureException to signal that something went wrong check exception type for further info.
	 */
	public static PasswdFile createPasswdFile(Path p) throws StructureException {
		String line;
		String[] tokens;
		Map<String, PasswdRecord> data = new HashMap<>();

		if (Files.isDirectory(p))
			throw new StructureException(StructureException.Type.INTEGRITY_ERROR);

		try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
			while ((line = br.readLine()) != null) {
				tokens = line.split(":");
				if (tokens.length != 4)
					throw new StructureException(StructureException.Type.INTEGRITY_ERROR);
				data.put(tokens[0], new PasswdRecord(tokens[1], tokens[2].equals("admin"),
						Integer.parseInt(tokens[3])));
			}
		} catch (NumberFormatException e) {
			throw new StructureException(StructureException.Type.INTEGRITY_ERROR);
		} catch (FileNotFoundException e) {
			throw new StructureException(StructureException.Type.FILE_NOT_FOUND_EXCEPTION);
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}

		return new PasswdFile(p, data);
	}

	/**
	 * Add a record of user to passwd file.
	 *
	 * @param username username
	 * @param passwd   hashed password
	 * @throws StructureException to signal that something went wrong check exception type for further info.
	 */
	public boolean addUser(String username, String passwd, boolean isAdmin, long salt) throws StructureException {
		try {
			Files.write(path, (username + ":" + passwd + ":" +
					(isAdmin ? "admin" : "user") + ":" + salt).getBytes(), StandardOpenOption.APPEND);
			data.put(username, new PasswdRecord(passwd, isAdmin, salt));
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}

		return true;
	}

	/**
	 * Get set of all users currently in passwd file.
	 *
	 * @return set of all users
	 */
	public Set<String> getUsers() {
		return data.keySet();
	}

	/**
	 * Get passwd of given user.
	 *
	 * @param username username
	 * @return hashed password
	 */
	public String getPasswd(String username) {
		return data.get(username).hashedPasswd;
	}

	/**
	 * Is given user a admin.
	 *
	 * @param username username
	 * @return if given username is admin
	 */
	public boolean isAdmin(String username) {
		return data.get(username).isAdmin;
	}

	/**
	 * Get number of registered users.
	 *
	 * @return number of registered users
	 */
	public int size() {
		return data.size();
	}
}
