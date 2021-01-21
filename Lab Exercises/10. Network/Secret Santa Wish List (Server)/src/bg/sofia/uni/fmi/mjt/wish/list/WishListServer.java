package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WishListServer {
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private static final String GET_WISH_COMMAND = "get-wish";
    private static final String POST_WISH_COMMAND = "post-wish ";
    private static final int POST_COMMAND_PARAMS = 3;
    private static final int STUDENT = 1;
    private static final int WISH = 2;
    private static final String WHITE_SPACE_REGEX = "\\s+";
    private static final String SEMI_COLUMN_DELIMITER = ", ";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String NETWORK_ERROR_MESSAGE = "There is a problem with the network communication";
    private static final String SERVER_INITIALIZATION_ERROR = "There is a problem initializing the server";
    private static final String SERVER_CLOSING_ERROR = "There is a problem closing the server";
    private static final String DISCONNECTED_MESSAGE = "[ Disconnected from server ]";
    private static final String WISH_EXISTS_STRING_FORMAT = "[ The same gift for student %s was already submitted ]";
    private static final String POST_SUCCESS_STRING_FORMAT = "[ Gift %s for student %s submitted successfully ]";
    private static final String INVALID_COMMAND_MESSAGE = "[ Unknown command ]";
    private static final String NO_STUDENTS_PRESENT_MESSAGE = "[ There are no students present in the wish list ]";
    private static final String GET_WISH_STRING_FORMAT = "[ %s: [%s] ]";

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buffer;
    private boolean isRunning;
    private Map<String, Set<String>> wishesByStudent;

    public WishListServer(int port) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, port));
            selector = Selector.open();
        } catch (IOException e) {
            System.out.println(SERVER_INITIALIZATION_ERROR);
            e.printStackTrace();
        }

        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        wishesByStudent = new HashMap<>();
    }

    public void start() {
        isRunning = true;

        try {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.out.println(SERVER_INITIALIZATION_ERROR);
            e.printStackTrace();
        }

        while (isRunning) {
            try {
                manageKeys();
            } catch (IOException e) {
                System.out.println(NETWORK_ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        close();
    }

    public void stop() {
        isRunning = false;
    }

    private void manageKeys() throws IOException {
        int readyChannels = selector.select();
        if (readyChannels == 0) {
            return;
        }

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                read(key);
            } else if (key.isAcceptable()) {
                accept(key);
            }
            keyIterator.remove();
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int r = socketChannel.read(buffer);
        if (r <= 0) {
            socketChannel.close();
            return;
        }

        buffer.flip();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        String message = new String(byteArray, StandardCharsets.UTF_8);
        String result = executeCommand(message) + System.lineSeparator();
        System.out.println(result);

        buffer.clear();
        buffer.put(result.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private String executeCommand(String command) {
        command = command.strip();

        if (command.equalsIgnoreCase(DISCONNECT_COMMAND)) {
            return DISCONNECTED_MESSAGE;
        } else if (command.equalsIgnoreCase(GET_WISH_COMMAND)) {
            return getWishCommandResult();
        } else if (command.toLowerCase().startsWith(POST_WISH_COMMAND)) {
            return getPostCommandResult(command);
        }

        return INVALID_COMMAND_MESSAGE;
    }

    private String getWishCommandResult() {
        if (wishesByStudent.isEmpty()) {
            return NO_STUDENTS_PRESENT_MESSAGE;
        }

        String randomStudent = wishesByStudent.keySet().iterator().next();
        String wishes = String.join(SEMI_COLUMN_DELIMITER, wishesByStudent.get(randomStudent));
        wishesByStudent.remove(randomStudent);
        return String.format(GET_WISH_STRING_FORMAT, randomStudent, wishes);
    }

    private String getPostCommandResult(String command) {
        String[] commandParams = command.split(WHITE_SPACE_REGEX, POST_COMMAND_PARAMS);
        if (commandParams.length < POST_COMMAND_PARAMS) {
            return INVALID_COMMAND_MESSAGE;
        }

        String student = commandParams[STUDENT];
        String wish = commandParams[WISH];
        wishesByStudent.putIfAbsent(student, new HashSet<>());
        if (wishesByStudent.get(student).contains(wish)) {
            return String.format(WISH_EXISTS_STRING_FORMAT, student);
        }
        wishesByStudent.get(student).add(wish);

        return String.format(POST_SUCCESS_STRING_FORMAT, wish, student);
    }

    private void close() {
        try {
            serverSocketChannel.close();
            selector.close();
        } catch (IOException e) {
            System.out.println(SERVER_CLOSING_ERROR);
            e.printStackTrace();
        }
    }
}
