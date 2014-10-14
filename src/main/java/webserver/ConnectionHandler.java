package webserver;

import java.net.Socket;

public interface ConnectionHandler {

	public void serve(Socket socket);

}
