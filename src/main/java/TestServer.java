import controller.ServerController;

import java.io.IOException;

public class TestServer extends ServerController {
    public TestServer() throws IOException {
        super(7654);
    }

    public static void main(String[] args) throws Exception {
        TestServer testServer = new TestServer();
        testServer.start();
    }
}
