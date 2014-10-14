package webserver;

public class UDPSocketServerMain {

	private static void usage() {
		System.out.println("Usage:\nUDPSocketServerMain -host <hostID> -port <portID>");
		System.exit(-1);
	}
	
	public static void main(String[] args) {
		
		// -host hostID -port portID
		String hostname = null;
		int port = -1;
		for (int i=0; i<args.length; i++) {
			
			if (args[i].equalsIgnoreCase("-host")) {
				hostname = args[i+1];
				i++;
			}
			else if (args[i].equalsIgnoreCase("-port")) {
				port = Integer.parseInt(args[i+1]);
				i++;
			}
		}
		
		if (hostname == null) {
			usage();
		}
		
		if (port < 0) {
			usage();
		}
		
		
		try {
			String filename = "src/test/resources/HD_MP2_06011_TS_ASYN_V1_001.mpg";
			System.out.println("Streaming video from file: " + filename + " to host: " + hostname + " on port: " + port);
			
			UDPSocketServer server = new UDPSocketServer();
			server.start("localhost", port, filename, 1316);
			
			Thread.sleep(240*1000L);
			server.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
