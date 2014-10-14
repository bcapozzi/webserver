package webserver.test;

import static org.junit.Assert.*;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.junit.Test;

public class ByteBufferTest {

	@Test public void canTransferDataInChunks() {
		
		byte [] bytes = new byte[10];
		for (int i=0; i<bytes.length; i++)
			bytes[i] = (byte) i;
		
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		assertEquals(0,buffer.position());
		assertEquals(10,buffer.limit());
		
		// now we will try to copy the first N bytes
		byte [] dest = new byte[4];
		buffer.get(dest);
		
		assertEquals(4,buffer.position());
		assertEquals(10,buffer.limit());
		for (int i=0; i<dest.length; i++) {
			assertEquals(bytes[i],dest[i]);
		}
		
		int numToRead = Math.min(4, buffer.remaining());
		assertEquals(4,numToRead);
		buffer.get(dest);
		assertEquals(8,buffer.position());
		assertEquals(10,buffer.limit());
		for (int i=0; i<dest.length; i++) {
			assertEquals(bytes[i+4],dest[i]);
		}
		
		numToRead = Math.min(4, buffer.remaining());
		assertEquals(2,numToRead);

		byte [] lastDest = new byte[numToRead];
		buffer.get(lastDest);
		assertEquals(10,buffer.position());
		assertEquals(10,buffer.limit());
		for (int i=0; i<lastDest.length; i++) {
			assertEquals(bytes[i+8],lastDest[i]);
		}
		
	}
}

