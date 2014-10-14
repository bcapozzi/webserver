package webserver.test;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import webserver.UDPSocketServer;

public class UDPSocketServerTest {

	private UDPSocketServer server;
	protected int numPacketsReceived;
	private long numBytesReceived;
	private DatagramSocket clientSocket;
	private boolean isFinished;
	
	@Before public void setUp() {
		
		server = new UDPSocketServer();	
	}
	
	@After public void tearDown() {
		
		server.stop();
	}
	
	@Test public void serverStreamsDataToHostAndPort() throws Exception {
		
		// setup "client" socket and receive buffer / packet
		DatagramSocket clientSocket = new DatagramSocket(8043);
		byte[] receiveBuffer = new byte[256];
		DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
		
		server.start("localhost",8043);
		
		// receive blocks until bytes received
		clientSocket.receive(receivedPacket);
		
		// wait for some data to be streamed
		//Thread.sleep(100);		
		
		byte [] bytesReceived = receivedPacket.getData();
		assertEquals(10, receivedPacket.getLength());
		
		for (int i=0; i<receivedPacket.getLength(); i++)
			System.out.println(bytesReceived[i]);
		
		clientSocket.close();
	}
	
	@Test public void serverStreamsCompleteVideoFile() throws Exception {
		
		String filename = "src/test/resources/HD_MP2_06011_TS_ASYN_V1_001.mpg";
		server.start("localhost", 8044, filename);
		
		numPacketsReceived = 0;
		numBytesReceived = 0;
		
		clientSocket = new DatagramSocket(8044);
		isFinished = false;
		ExecutorService clientService = Executors.newFixedThreadPool(4);
		clientService.submit(new Runnable() {
			
			@Override
			public void run() {
				int UDP_MAX_PAYLOAD_SIZE = 65507;
				byte[] receiveBuffer = new byte[UDP_MAX_PAYLOAD_SIZE];
				DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				try {
					while (!isFinished) {
						clientSocket.receive(receivedPacket);
						numPacketsReceived++;
						numBytesReceived += receivedPacket.getLength();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// wait for packets to be transmitted by server
		Thread.sleep(12000);
		
		assertEquals(1017, numPacketsReceived);
		System.out.format("Client received %d bytes", numBytesReceived);
		assertEquals(66592420L, numBytesReceived);
		
		isFinished = true;
		clientSocket.close();
	}
}
