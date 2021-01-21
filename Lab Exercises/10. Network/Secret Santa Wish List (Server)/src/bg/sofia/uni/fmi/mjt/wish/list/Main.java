package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 7777;
        WishListServer server = new WishListServer(port);
        server.start();
    }
}
