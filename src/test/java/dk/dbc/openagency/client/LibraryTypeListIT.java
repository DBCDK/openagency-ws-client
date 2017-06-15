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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class LibraryTypeListIT  extends  WireMocker {

    public static final String REQUEST = "<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryTypeListRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"/></S:Body></S:Envelope>";
    
    /**
     * Test of getType method, of class LibraryTypeList.
     */
    @Test
    public void testGetType() throws Exception {
        stubForReturnOK(REQUEST,
                "libraryTypeList.xml");

        LibraryTypeList request = service.libraryTypeList();
        assertEquals(LibraryType.UNKNOWN, request.getType("000200"));
        assertEquals(LibraryType.SKOLEBIBLIOTEK, request.getType("300615"));
        assertEquals(LibraryType.FOLKEBIBLIOTEK, request.getType("710100"));
        assertEquals(LibraryType.FORSKNINGSBIBLIOTEK, request.getType("874620"));
        assertEquals(LibraryType.UNKNOWN, request.getType("999999"));

        verify(1, postRequestedFor(urlEqualTo("/")));
    }

    @Test
    public void testGetTypeRetry() throws Exception {
        stubForSequence(REQUEST,
                aResponse().withStatus(500).withBody("libraryTypeList.xml"),
                aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK),
                aResponse().withStatus(200).withFixedDelay(requestTimeout+200).withHeader("Content-Type", "text/xml; charset=utf-8").withBody("libraryTypeList.xml"),
                aResponse().withStatus(200).withFixedDelay(300).withHeader("Content-Type", "text/xml; charset=utf-8").withBodyFile("libraryTypeList.xml")
        );

        LibraryTypeList request = service.libraryTypeList();
        assertEquals(LibraryType.UNKNOWN, request.getType("000200"));
        assertEquals(LibraryType.SKOLEBIBLIOTEK, request.getType("300615"));
        assertEquals(LibraryType.FOLKEBIBLIOTEK, request.getType("710100"));
        assertEquals(LibraryType.FORSKNINGSBIBLIOTEK, request.getType("874620"));
        assertEquals(LibraryType.UNKNOWN, request.getType("999999"));

        
        verify(4, postRequestedFor(urlEqualTo("/")));
    }

}
