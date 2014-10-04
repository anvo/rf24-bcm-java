import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

import com.github.stanleyseow.RF24;

public class GettingStarted {

	static {
		System.loadLibrary("rf24bcmjava");
	}

	public static void main(String[] args) throws InterruptedException {

		RF24 radio = new RF24((short) 22/* RPI_V2_GPIO_P1_15 */,
				(short) 8/* RPI_V2_GPIO_P1_24 */, (long) 32/* BCM2835_SPI_SPEED_8MHZ */);
		
		BigInteger pipes[] = { new BigInteger("110010101100100011011110100111000110001",2) /*"1Node"*/, new BigInteger("110010101100100011011110100111000110010",2) /*"2Node"*/};

		boolean role_ping_out = true, role_pong_back = false;
		boolean role = role_pong_back;

		// Print preamble:
		System.out.println("RF24/examples/pingtest/\n");

		// Setup and configure rf radio
		radio.begin();

		// optionally, increase the delay between retries & # of retries
		radio.setRetries((short) 15, (short) 15);
		// Dump the configuration of the rf unit for debugging
		radio.printDetails();

		/********* Role chooser ***********/

		System.out.println("\n ************ Role Setup ***********\n");

		System.out
				.print("Choose a role: Enter 0 for pong_back, 1 for ping_out (CTRL+C to exit) \n>");
		Scanner sin = new Scanner(System.in);
		if (sin.nextInt() == 0) {
			System.out.println("Role: Pong Back, awaiting transmission ");
		} else {
			System.out.println("Role: Ping Out, starting transmission ");
			role = role_ping_out;
		}
		sin.close();
		
		/***********************************/
		// This simple sketch opens two pipes for these two nodes to communicate
		// back and forth.

		if (role == role_ping_out) {
			radio.openWritingPipe(pipes[0]);
			radio.openReadingPipe((short) 1, pipes[1]);
		} else {
			radio.openWritingPipe(pipes[1]);
			radio.openReadingPipe((short) 1, pipes[0]);
			radio.startListening();

		}
		//Arduino expects an unsigned long = 4 bytes
		//Java long = 8 bytes. We have to use a int = 4 bytes
		//Caution: All primitive datatypes in Java (<8) are unsigned!
		ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE/Byte.SIZE);
		buf.order(ByteOrder.LITTLE_ENDIAN); //Arduino is Little Endian
	// forever loop
		int counter = 0; //Increment a counter instead of sending the time
		while (true)
		{
			if (role == role_ping_out)
			{
				// First, stop listening so we can talk.
				radio.stopListening();

				// Increment the counter, and send it.  This will block until complete				
				counter++;
				System.out.println("Now sending..." + counter);
				buf.clear();
				buf.putInt(counter);
				boolean ok = radio.write(buf.array(), (short)(buf.capacity()));

				if (!ok){
					System.out.println("failed.");
				}
				// Now, continue listening
				radio.startListening();

				// Wait here until we get a response, or timeout (250ms)
				long started_waiting_at = System.currentTimeMillis();
				boolean timeout = false;
				while ( ! radio.available() && ! timeout ) {
					if (System.currentTimeMillis() - started_waiting_at > 200 )
						timeout = true;
				}


				// Describe the results
				if ( timeout )
				{
					System.out.println("Failed, response timed out.");
				}
				else
				{
					// Grab the response, compare, and send to debugging spew
					int response;
					buf.clear();
					radio.read( buf.array(), (short)(buf.capacity()));
					response = buf.getInt();
					
					// Spew it
					System.out.format("Got response %d,\n",response);
				}

				// Try again 1s later
				// delay(1000);

				Thread.sleep(1000);

			}

			//
			// Pong back role.  Receive each packet, dump it out, and send it back
			//

			if ( role == role_pong_back )
			{
				
				// if there is data ready
				//printf("Check available...\n");

				if ( radio.available() )
				{
					// Dump the payloads until we've gotten everything
					int payload;


					// Fetch the payload, and see if this was the last one.
					buf.clear();
					radio.read(buf.array(), (short)(buf.capacity()));
					payload = buf.getInt();

					radio.stopListening();
					
					buf.clear();
					buf.putInt(payload);
					radio.write(buf.array(), (short)(buf.capacity()));

					// Now, resume listening so we catch the next packets.
					radio.startListening();

					// Spew it - Convert from unsigned int to Java long
					System.out.println("Got payload (" +buf.capacity()+ ") " + (payload&0xffffffffL));
				
					Thread.sleep(925); //Delay after payload responded to, minimize RPi CPU time
					
				}
			
			}

		} // forever loop
}
}
