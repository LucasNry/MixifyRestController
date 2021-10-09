package model;

import org.junit.Assert;
import org.junit.Test;

public class HttpResponseTest {

    private static final String EXPECTED_BODY = "Test\nTest2";
    private static final int EXPECTED_HEADER_SIZE = 6;

    private static final Headers SAMPLE_HEADER = new Headers("sampleHeaderKey", "sampleHeaderValue");

    @Test
    public void testResponseWithBody() {
        HttpResponse response = HttpResponse.fromString("HTTP/1.1 200 OK\r\n" +
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
                "Server: Apache/2.2.14 (Win32)\r\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                "Content-Length: 88\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: Closed\r\n" +
                "\r\n" +
                "Test\r\n" +
                "Test2");

        Assert.assertEquals(RequestStatus.OK.getStatusCode(), response.getRequestStatus().getStatusCode());
        Assert.assertEquals(RequestStatus.OK.getStatusMessage(), response.getRequestStatus().getStatusMessage());
        Assert.assertEquals(EXPECTED_HEADER_SIZE, response.getHeaders().getSize());
        Assert.assertEquals(EXPECTED_BODY, response.getBody());
    }

    @Test
    public void testResponseWithoutBody() {
        HttpResponse response = HttpResponse.fromString("HTTP/1.1 200 OK\r\n" +
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
                "Server: Apache/2.2.14 (Win32)\r\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                "Content-Length: 88\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: Closed");

        Assert.assertEquals(RequestStatus.OK.getStatusCode(), response.getRequestStatus().getStatusCode());
        Assert.assertEquals(RequestStatus.OK.getStatusMessage(), response.getRequestStatus().getStatusMessage());
        Assert.assertEquals(EXPECTED_HEADER_SIZE, response.getHeaders().getSize());
        Assert.assertEquals("", response.getBody());
    }

    @Test
    public void testResponseWithoutHeaders() {
        HttpResponse response = HttpResponse.fromString("HTTP/1.1 200 OK\r\n" +
                "\r\n" +
                "Test\r\n" +
                "Test2");

        Assert.assertEquals(RequestStatus.OK.getStatusCode(), response.getRequestStatus().getStatusCode());
        Assert.assertEquals(RequestStatus.OK.getStatusMessage(), response.getRequestStatus().getStatusMessage());
        Assert.assertEquals(0, response.getHeaders().getSize());
        Assert.assertEquals(EXPECTED_BODY, response.getBody());
    }

    @Test
    public void testResponseToStringAndBack() {
        String expectedBody = "Test";

        HttpResponse originalHttpResponse = HttpResponse
                .builder()
                .headers(SAMPLE_HEADER)
                .body(expectedBody)
                .build();

        String actualString = originalHttpResponse.toString();

        HttpResponse unserializedHttpResponse = HttpResponse.fromString(actualString);

        Assert.assertEquals(RequestStatus.OK.getStatusCode(), unserializedHttpResponse.getRequestStatus().getStatusCode());
        Assert.assertEquals(RequestStatus.OK.getStatusMessage(), unserializedHttpResponse.getRequestStatus().getStatusMessage());
        Assert.assertEquals(1, unserializedHttpResponse.getHeaders().getSize());
        Assert.assertEquals(expectedBody, unserializedHttpResponse.getBody());
    }
}
