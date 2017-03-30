/*
 * Copyright (C) 2015 DBC A/S (http://dbc.dk/)
 *
 * This is part of dbc-openagency-ws-client
 *
 * dbc-openagency-ws-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dbc-openagency-ws-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dbc.openagency.client;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public class WireMocker {

    public int port;
    @Rule
    public WireMockRule wireMockRule;
    public String url;

    public WireMocker() throws RuntimeException {
        port = Integer.parseInt(System.getProperty("wiremock.port"));
        wireMockRule = new WireMockRule(port);
        url = "http://localhost:" + port + "/";
    }

    @Before
    public void setUpWireMock() throws Exception {

        stubFor(post(urlMatching(".*"))
                .willReturn(aResponse()
                        .withStatus(501)
                        .withHeader("Content-Type", "text/html")
                        .withBody("404 body")));
        setupWiremock();
    }

    private InputStream open(String className, String file) {
        String path = "/" + className + "/" + file;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalArgumentException("Cannot open test-resource: " + path);
        }
        return stream;
    }

    public void setupWiremock() throws IOException {
        String className = getClass().getName();
        Matcher matcher = Pattern.compile("^(?:.*\\.)?(.*?)(?:IT)$").matcher(className);
        if (matcher.find()) {
            className = matcher.group(1);
        }
        InputStream stream = open(className, "wiremock.json");
        JsonReader reader = Json.createReader(stream);
        JsonArray array = reader.readArray();
        for (int i = 0 ; i < array.size() ; i++) {
            JsonObject obj = array.getJsonObject(i);
            String path = obj.getString("url", null);
            String body = obj.getString("body", null);
            String contentPath = obj.getString("content", null);
            InputStream contentResource = open(className, contentPath);
            int len = contentResource.available();
            byte[] content = new byte[len];
            new DataInputStream(contentResource).readFully(content);
            MappingBuilder req = post(urlMatching(path));
            if (body != null) {
                req.withRequestBody(matching(body));
            }
            stubFor(req
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml; charset=utf-8")
                            .withBody(content)));

        }
    }

}
