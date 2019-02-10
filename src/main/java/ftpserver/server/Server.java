package ftpserver.server;

import ftpserver.server.client_handler.ClientHandler;
import ftpserver.structure.Structure;
import ftpserver.structure.StructureException;
import ftpserver.user_manager.UserManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class Server {
	private ServerSocket sock;
	private int port;
	private UserManager um;

	private Server(ServerSocket sock, int p, UserManager um) {
		this.sock = sock;
		this.port = p;
		this.um = um;
	}

	public static Server createServer(int port, UserManager um, Path storageDir) {
		try {
			Structure s = Structure.createStructure(storageDir);
			ServerSocket sock = new ServerSocket(port);

			return new Server(sock, port, um);
		} catch (StructureException | IOException e) {
			System.err.println(e);
			return null;
		}
	}

	public void start() throws IOException {
		while (true) {
			Socket socket = sock.accept();
			new Thread(new ClientHandler(socket));
		}
	}
}
