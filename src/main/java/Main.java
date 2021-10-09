import model.Headers;
import model.HttpRequest;
import model.HttpResponse;
import sun.jvm.hotspot.memory.HeapBlock.Header;

public class Main {
    public static void main(String[] args) {
        HttpRequest request = HttpRequest.fromString("GET / HTTP/1.1\r\n" +
                "sec-ch-ua: \"Google Chrome\";v=\"93\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"93\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"macOS\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\r\n" +
                "X-Client-Data: CIu2yQEIo7bJAQjEtskBCKmdygEI6PzKAQjxl8sBCIyeywEI7/LLAQi0+MsBCJ75ywEI8vnLAQiw+ssBCKL+ywEIvv7LAQie/8sBCOL/ywE=\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Content-Type: text/plain\r\n" +
                "Postman-Token: 6968598a-56a4-453b-88d2-d63a2394aa46\r\n" +
                "Host: 127.0.0.1:9876\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "\r\n" +
                "Test\r\n" +
                "Test2");


        HttpResponse httpResponse = HttpResponse.fromString("HTTP/1.1 200 OK\r\n" +
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
                "Server: Apache/2.2.14 (Win32)\r\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                "Content-Length: 88\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: Closed\r\n" +
                "\r\n" +
                "Test\r\n" +
                "Test2");

        System.out.println(request);
    }
}
