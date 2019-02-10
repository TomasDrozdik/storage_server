package ftpserver.server.client_handler;

import ftpserver.user_manager.UserManager;

import java.net.Socket;

public class ClientHandler implements Runnable {
	private Socket sock;
	private UserManager um;

	public ClientHandler(Socket sock) {
		this.sock = sock;
		this.um = UserManager.getUserManager();
	}

	@Override
	public void run() {

	}
}
