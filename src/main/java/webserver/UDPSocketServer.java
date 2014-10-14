package webserver;

import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPSocketServer {

	private String clientHostName;
	private int clientPortID;
	private DatagramSocket socket;
	private String filePath;
	
	public void start(String hostname, int port, String filename, final int packetSize) throws Exception {
		
		socket = new DatagramSocket();
		
		clientHostName = hostname;
		clientPortID = port;
		filePath = filename;
		ExecutorService service = Executors.newFixedThreadPool(4);
		service.submit(new Runnable() {
			
			@Override
			public void run() {
								
				try {
					
					InetAddress address = InetAddress.getByName(clientHostName);
					
					Path path = Paths.get(filePath);
					byte [] allBytes = Files.readAllBytes(path);
					
					// send UDP-MAX size packets until all file is sent
					byte [] bytesToSend = new byte[packetSize];
					byte [] lastBytesToSend = null;
					ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes);
					
					int numBytesSent = 0;
					while (byteBuffer.remaining() > 0) {

						int numToTransfer = Math.min(byteBuffer.remaining(), packetSize);
						DatagramPacket packetToSend = null;
						if (numToTransfer == packetSize)
						{
							byteBuffer.get(bytesToSend);
							packetToSend = new DatagramPacket(bytesToSend, numToTransfer);
						}
						else {
							lastBytesToSend = new byte[numToTransfer];
							byteBuffer.get(lastBytesToSend);
							packetToSend = new DatagramPacket(lastBytesToSend, numToTransfer);
							// at this point, should be LAST packet
							assert byteBuffer.remaining() == 0;
						}
						
						numBytesSent += numToTransfer;
						//System.out.println("Server sent " + numBytesSent);
						packetToSend.setPort(clientPortID);
						packetToSend.setAddress(address);
						
						// dump the bytes being sent
						/*
						int column = 0;
						for (int i=0; i<numToTransfer; i++) {
							column++;
							System.out.format("%02x ", bytesToSend[i]);
							if (column == 16) {
								System.out.println();
								column = 0;
							}
						}
						*/
						
						socket.send(packetToSend);
						
						// throttle send a bit
						Thread.sleep(1);

					}
					
					// once done sending, close out
					socket.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void start(String hostname, int port, String filename) throws Exception {
			
		socket = new DatagramSocket();
		
		clientHostName = hostname;
		clientPortID = port;
		filePath = filename;
		ExecutorService service = Executors.newFixedThreadPool(4);
		service.submit(new Runnable() {
			
			@Override
			public void run() {
								
				try {
					
					InetAddress address = InetAddress.getByName(clientHostName);
					
					Path path = Paths.get(filePath);
					byte [] allBytes = Files.readAllBytes(path);
					
					// send UDP-MAX size packets until all file is sent
					int UDP_MAX_PAYLOAD_SIZE = 65507;
					byte [] bytesToSend = new byte[UDP_MAX_PAYLOAD_SIZE];
					byte [] lastBytesToSend = null;
					ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes);
					
					int numBytesSent = 0;
					while (byteBuffer.remaining() > 0) {

						int numToTransfer = Math.min(byteBuffer.remaining(), UDP_MAX_PAYLOAD_SIZE);
						DatagramPacket packetToSend = null;
						if (numToTransfer == UDP_MAX_PAYLOAD_SIZE)
						{
							byteBuffer.get(bytesToSend);
							packetToSend = new DatagramPacket(bytesToSend, numToTransfer);
						}
						else {
							lastBytesToSend = new byte[numToTransfer];
							byteBuffer.get(lastBytesToSend);
							packetToSend = new DatagramPacket(lastBytesToSend, numToTransfer);
							// at this point, should be LAST packet
							assert byteBuffer.remaining() == 0;
						}
						
						numBytesSent += numToTransfer;
						System.out.println("Server sent " + numBytesSent);
						packetToSend.setPort(clientPortID);
						packetToSend.setAddress(address);
						socket.send(packetToSend);
						
						
						// throttle send a bit
						Thread.sleep(10);

					}
					
					// once done sending, close out
					socket.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void start(final String hostname, int port) throws Exception {
		
		socket = new DatagramSocket();
		
		clientPortID = port;
		ExecutorService service = Executors.newFixedThreadPool(4);
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// create new thread to send bytes
				byte[] bytesToSend = new byte[10];
				for (int i=0; i<10; i++)
					bytesToSend[i] = (byte) i;
				
				try {
					DatagramPacket p = new DatagramPacket(bytesToSend, bytesToSend.length);
					p.setPort(clientPortID);
					InetAddress address = InetAddress.getByName(hostname);
					p.setAddress(address);
					socket.send(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
	}

	public void stop() {
		socket.close();
	}

	
}
