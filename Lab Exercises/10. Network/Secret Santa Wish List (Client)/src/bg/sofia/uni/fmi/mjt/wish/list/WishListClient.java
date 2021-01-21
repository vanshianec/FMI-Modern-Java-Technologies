package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WishListClient {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String NETWORK_ERROR = "There is a problem with the network communication";
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(512);


    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            while (true) {
                String command = scanner.nextLine(); // read a line from the console

                command += System.lineSeparator();

                buffer.clear(); // switch to writing mode
                buffer.put(command.getBytes()); // buffer fill
                buffer.flip(); // switch to reading mode
                socketChannel.write(buffer); // buffer drain

                buffer.clear(); // switch to writing mode
                socketChannel.read(buffer); // buffer fill
                buffer.flip(); // switch to reading mode

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8); // buffer drain

                // if buffer is a non-direct one, is has a wrapped array and we can get it
                //String reply = new String(buffer.array(), 0, buffer.position(), "UTF-8"); // buffer drain

                System.out.println(reply);

                if (DISCONNECT_COMMAND.equalsIgnoreCase(command.strip())) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println(NETWORK_ERROR);
            e.printStackTrace();
        }
    }
}
