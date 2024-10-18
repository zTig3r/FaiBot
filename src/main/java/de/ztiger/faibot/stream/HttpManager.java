package de.ztiger.faibot.stream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;

public class HttpManager {

    private static final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    public static CloseableHttpResponse getData(String uri, String token) throws IOException {
        return getData(uri, token, null);
    }

    public static CloseableHttpResponse getData(String uri, String token, Map<String, String> headers) throws IOException {
        HttpGet request = new HttpGet(uri);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        request.addHeader("Content-Type", "application/json");

        if (headers != null) headers.forEach(request::addHeader);

        return httpClient.execute(request);
    }

    public static CloseableHttpResponse putData(String uri, String token, String requestBody) throws IOException {
        HttpPut request = new HttpPut(uri);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        return httpClient.execute(request);
    }
}
