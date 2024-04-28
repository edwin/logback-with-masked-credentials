package com.edw;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HeaderElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

/**
 * <pre>
 *     com.edw.MainTest
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 28 Apr 2024 15:45
 */
@SpringBootTest
public class MainTest {

    private WireMockServer wireMockServer = new WireMockServer();
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    private Logger logger = LoggerFactory.getLogger(MainTest.class);

    @Test
    @DisplayName("01. Testing Hello World Page")
    public void indexTest() throws IOException {
        wireMockServer.start();

        configureFor("localhost", 8080);
        stubFor(
                get(
                    urlEqualTo("/"))
                        .willReturn(
                                aResponse()
                                        .withBody("Hello World")
                        ));

        HttpGet request = new HttpGet("http://localhost:8080/");
        request.addHeader("Accept-Encoding", "text/plain");
        request.addHeader("Accept-Charset", "utf-8");

        // authentication header
        request.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiS.jwt-sample.cpQcLckrTofjLZtCFFcMfThBNWD");

        CloseableHttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = convertResponseToString(httpResponse);

        verify(getRequestedFor(urlEqualTo("/")));
        Assertions.assertEquals("Hello World", stringResponse);

        wireMockServer.stop();
    }

    private String convertResponseToString(CloseableHttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String stringResponse = scanner.useDelimiter("\\Z").next();
        scanner.close();

        logger.debug("response is {}", stringResponse);

        return stringResponse;
    }

}
