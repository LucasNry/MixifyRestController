package model;

import org.junit.Assert;
import org.junit.Test;

public class HttpRequestTest {

    private static final String EXPECTED_BODY = "Test\nTest2";
    private static final int EXPECTED_HEADER_SIZE = 17;

    private static final Headers SAMPLE_HEADER = new Headers("sampleHeaderKey", "sampleHeaderValue");
    private static final QueryParameters SAMPLE_QUERY_PARAMETER = new QueryParameters("sampleQueryParamKey", "sampleQueryParamValue");
    private static final QueryParameters SAMPLE_QUERY_PARAMETERS = new QueryParameters(
            "sampleQueryParamKey1", "sampleQueryParamValue1",
            "sampleQueryParamKey2", "sampleQueryParamValue2"
    );

    @Test
    public void testRequestWithBody() {
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

        Assert.assertEquals(HttpMethod.GET, request.getHttpMethod());
        Assert.assertEquals(HttpVersion.ONE_DOT_ONE, request.getHttpVersion());
        Assert.assertEquals("/", request.getPath());
        Assert.assertNull(request.getQueryParameters());
        Assert.assertEquals(EXPECTED_HEADER_SIZE, request.getHeaders().getSize());
        Assert.assertEquals(EXPECTED_BODY, request.getBody());
    }

    @Test
    public void testRequestWithoutBody() {
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
                "Content-Length: 4");

        Assert.assertEquals(HttpMethod.GET, request.getHttpMethod());
        Assert.assertEquals(HttpVersion.ONE_DOT_ONE, request.getHttpVersion());
        Assert.assertEquals("/", request.getPath());
        Assert.assertNull(request.getQueryParameters());
        Assert.assertEquals(EXPECTED_HEADER_SIZE, request.getHeaders().getSize());
        Assert.assertEquals("", request.getBody());
    }

    @Test
    public void testRequestWithoutHeaders() {
        HttpRequest request = HttpRequest.fromString("GET / HTTP/1.1\r\n" +
                "\r\n" +
                "Test\r\n" +
                "Test2");

        Assert.assertEquals(HttpMethod.GET, request.getHttpMethod());
        Assert.assertEquals(HttpVersion.ONE_DOT_ONE, request.getHttpVersion());
        Assert.assertEquals("/", request.getPath());
        Assert.assertNull(request.getQueryParameters());
        Assert.assertEquals(0, request.getHeaders().getSize());
        Assert.assertEquals(EXPECTED_BODY, request.getBody());
    }

    @Test
    public void testRequestWithQueryParameters() {
        HttpRequest request = HttpRequest.fromString("GET /?sampleQueryParamKey=sampleQueryParamValue HTTP/1.1\r\n" +
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

        Assert.assertEquals(HttpMethod.GET, request.getHttpMethod());
        Assert.assertEquals(HttpVersion.ONE_DOT_ONE, request.getHttpVersion());
        Assert.assertEquals("/", request.getPath());
        Assert.assertEquals(SAMPLE_QUERY_PARAMETER.getParameter("sampleQueryParamKey"), request.getQueryParameters().getParameter("sampleQueryParamKey"));
        Assert.assertEquals(EXPECTED_HEADER_SIZE, request.getHeaders().getSize());
        Assert.assertEquals(EXPECTED_BODY, request.getBody());
    }

    @Test
    public void testResponseToStringAndBack() {
        String expectedPath = "/test";
        String expectedBody = "Test";

        HttpRequest originalHttpRequest = new HttpRequest(HttpMethod.GET, expectedPath, SAMPLE_QUERY_PARAMETER, HttpVersion.ONE_DOT_ONE, SAMPLE_HEADER, expectedBody);

        String actualString = originalHttpRequest.toString();

        HttpRequest unserializedHttpRequest = HttpRequest.fromString(actualString);

        Assert.assertEquals(HttpMethod.GET, unserializedHttpRequest.getHttpMethod());
        Assert.assertEquals(expectedPath, unserializedHttpRequest.getPath());
        Assert.assertEquals(
                SAMPLE_QUERY_PARAMETER.getParameter("sampleQueryParamKey"),
                originalHttpRequest.getQueryParameters().getParameter("sampleQueryParamKey")
        );
        Assert.assertEquals(HttpVersion.ONE_DOT_ONE, unserializedHttpRequest.getHttpVersion());
        Assert.assertEquals(1, unserializedHttpRequest.getHeaders().getSize());
        Assert.assertEquals(expectedBody, unserializedHttpRequest.getBody());
    }

    @Test
    public void testResponseToStringAndBackWithMultipleQueryParameters() {
        String expectedPath = "/test";
        String expectedBody = "Test";

        HttpRequest originalHttpRequest = new HttpRequest(HttpMethod.GET, expectedPath, SAMPLE_QUERY_PARAMETERS, HttpVersion.ONE_DOT_ONE, SAMPLE_HEADER, expectedBody);

        String actualString = originalHttpRequest.toString();

        HttpRequest unserializedHttpRequest = HttpRequest.fromString(actualString);

        Assert.assertEquals(HttpMethod.GET, unserializedHttpRequest.getHttpMethod());
        Assert.assertEquals(expectedPath, unserializedHttpRequest.getPath());
        Assert.assertEquals(
                SAMPLE_QUERY_PARAMETERS.getParameter("sampleQueryParamKey1"),
                originalHttpRequest.getQueryParameters().getParameter("sampleQueryParamKey1")
        );
        Assert.assertEquals(
                SAMPLE_QUERY_PARAMETERS.getParameter("sampleQueryParamKey2"),
                originalHttpRequest.getQueryParameters().getParameter("sampleQueryParamKey2")
        );
        Assert.assertEquals(HttpVersion.ONE_DOT_ONE, unserializedHttpRequest.getHttpVersion());
        Assert.assertEquals(1, unserializedHttpRequest.getHeaders().getSize());
        Assert.assertEquals(expectedBody, unserializedHttpRequest.getBody());
    }
}
