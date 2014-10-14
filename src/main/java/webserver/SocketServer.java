package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketServer {

	private int port;
	private ConnectionHandler connectionHandler;
	private boolean isRunning = false;
	private ServerSocket serverSocket;
	private ExecutorService service;

	public SocketServer(int port, ConnectionHandler connectionHandler) throws Exception {
		this.port = port;
		this.connectionHandler = connectionHandler;
		this.serverSocket = new ServerSocket(port);
		this.service = Executors.newFixedThreadPool(4);
	}

	public int getPort() {
		return port;
	}

	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void start() throws Exception {
		
		System.out.println("Creating server socket, listening for connections on port: " + port + " ...");
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					Socket serviceSocket = serverSocket.accept();
					connectionHandler.serve(serviceSocket);
				} catch (Exception e) {
					if (isRunning)
						e.printStackTrace();
				}
			}
		});
		
		isRunning = true;
	}

	public void stop() throws Exception {
		
		service.shutdown();
		service.awaitTermination(100, TimeUnit.MILLISECONDS);
		if (isRunning)
		{
			System.out.println("Closing server socket.");
		}
		isRunning = false;
		serverSocket.close();
	}

	
}
