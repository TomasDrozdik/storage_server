package ftpserver.structure.files;

import ftpserver.structure.StructureException;
import ftpserver.user_manager.users.BaseUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HomeDir {
	Path path;
	private BaseUser user;
	private List<UploadedFile> files;

	private HomeDir(Path p, List<UploadedFile> files) {
		this.path = p;
		this.files = files;
	}

	/**
	 * Creates HomeDir interface to user home directory.
	 * <p>
	 * Checks integrity according to provided user property dir.
	 *
	 * @param p            path to the home dir
	 * @param userProperty reference to the corresponding user property file
	 * @return returns non null HomeDir otherwise throws exception
	 * @throws StructureException to signal that something went wrong check exception type for further info.
	 */
	public static HomeDir createHomeDir(Path p, UserProperty userProperty) throws StructureException {
		List<UploadedFile> files = new ArrayList<>();

		if (!Files.isDirectory(p))
			throw new StructureException(StructureException.Type.NOT_DIRECTORY);

		checkIntegrity(p, userProperty);

		try {
			for (Path dirEnt : Files.newDirectoryStream(p)) {
				files.add(new UploadedFile(p));
			}
		} catch (SecurityException e) {
			throw new StructureException(StructureException.Type.SECURITY_EXCEPTION);
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}

		return new HomeDir(p, files);
	}

	private static void checkIntegrity(Path p, UserProperty userProperty) {
		//TODO
	}
}
