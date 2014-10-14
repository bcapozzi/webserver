package webserver.test;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import webserver.SocketServer;
import webserver.ConnectionHandler;

public class SocketServerTest {

	private int port;
	private SocketServer server;
	private FakeConnectionHandler connectionHandler;
	
	@Before public void setUp() throws Exception {
		
		port = 8042;
		connectionHandler = new FakeConnectionHandler();
		server = new SocketServer(port, connectionHandler);
	}

	@After public void tearDown() throws Exception {
		server.stop();
	}
	
	@Test public void canInstantiate() throws Exception {
		
		assertEquals(port, server.getPort());
		assertEquals(connectionHandler, server.getConnectionHandler());
	}
	
	@Test public void canStartAndStopServer() throws Exception {
		
		assertEquals(false, server.isRunning());
		
		server.start();
		assertEquals(true, server.isRunning());
		
		server.stop();
		assertEquals(false, server.isRunning());
		
	}
	
	@Test public void canAcceptIncomingConnections() throws Exception {
		
		server.start();
		
		Socket socket = new Socket("localhost", port);
		
		// wait for a bit ... prior to stop
		//Thread.sleep(200);
		
		socket.close();
		server.stop();
		
		
		
		assertEquals(1, connectionHandler.numConnections);
		
	}
	
	@Test public void canSendPacketsFromServerToConnectedClientSocket() throws Exception {
	
		server.start();
		Socket socket = new Socket("localhost", port);
		
		boolean connected = waitForClientConnectionToServer();
		assertEquals(true, connected);
		
		byte [] bytesSent = emulateServerSendingDataToClientOnceConnected();
		assertBytesReadEqual(socket, bytesSent);
		
		server.stop();
	}
	
	private byte [] emulateServerSendingDataToClientOnceConnected() throws Exception{
		byte [] bytesToSend = generateBytes(10);
		connectionHandler.sendBytes(bytesToSend);
		return bytesToSend;
	}

	private boolean waitForClientConnectionToServer() throws Exception {
		int numChecks = 0;
		while (true) {
	
			if (connectionHandler.numConnections > 0)
				return true;
			
			numChecks++;
			if (numChecks > 1000)
				break;
			
			Thread.sleep(10);
		}
		
		return false;
	}

	private byte[] generateBytes(int numBytes) {
		byte[] bytes = new byte[numBytes];
		
		for (int i=0; i<numBytes; i++)
			bytes[i] = (byte) i;
		
		return bytes;
	}
	
	private void assertBytesReadEqual(Socket socket, byte[] bytesExpected) throws Exception {
		InputStream inputStream = socket.getInputStream();
		assertTrue(inputStream.available() > 0);
		assertEquals(10, inputStream.available());

		byte[] bytesReceived = new byte[inputStream.available()];
		int numRead = inputStream.read(bytesReceived);
		assertEquals(10, numRead);
		
		for (byte b: bytesReceived)
			System.out.println(b);
		
		for (int i=0; i<numRead; i++) {
			assertEquals(bytesExpected[i], bytesReceived[i]);
		}

	}
	
	private static class FakeConnectionHandler implements ConnectionHandler {

		public int numConnections;
		private Socket socket;

		@Override
		public void serve(Socket socket) {
			numConnections++;
			this.socket = socket;
		}

		public void sendBytes(byte [] bytes) throws Exception {
			
			socket.getOutputStream().write(bytes);
		}

	}
}
