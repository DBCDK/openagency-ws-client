/*
 * Copyright (C) 2015 DBC A/S (http://dbc.dk/)
 *
 * This is part of dbc-openagency-ws-client-1.19
 *
 * dbc-openagency-ws-client-1.19 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dbc-openagency-ws-client-1.19 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dbc.openagency.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.Assert;
import org.junit.Test;


/**
 */
public class FindLibraryIT extends WireMocker  {
    final String request="<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:findLibraryRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:findLibraryRequest></S:Body></S:Envelope>";

    /**
     * Test of getOrder method, of class ShowOrder.
     */
    @Test
    public void testFindLibrary() throws Exception {
        stubForReturnOK( request, "findLibrary_710100.xml");

        String result = service.findLibrary().findLibraryByAgency(710100).agencyName;

        Assert.assertEquals("Københavns Biblioteker", result);


        verify(1, postRequestedFor(urlEqualTo("/")));
    }

    @Test
    public void testFindLibraryDoesNotExists() throws Exception {
        stubForReturnOK( request, "findLibrary_999999_does_not_exists.xml");

        PickupAgency result = service.findLibrary().findLibraryByAgency(710100);

        verify(1, postRequestedFor(urlEqualTo("/")));
        Assert.assertNull("Expects no result for nonexisting agency", result);
    }

    @Test (expected = OpenAgencyException.class)
    public void testFindLibraryMultipleValues() throws Exception {
        stubForReturnOK( request, "findLibrary_multiple_values.xml");

        service.findLibrary().findLibraryByAgency(710100);
    }

    /**
     * Test of getOrder method, of class ShowOrder.
     */
    @Test
    public void testFindLibraryRetries() throws Exception {
        stubForSequence( request,
                aResponse().withStatus(500).withBody("findLibrary_710100.xml"),
                aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK),
                aResponse().withFixedDelay(requestTimeout+200).withStatus(200).withBody("fisk"),
                aResponse().withFixedDelay(200).withStatus(200).withHeader("Content-Type", "text/xml; charset=utf-8").withBodyFile("findLibrary_710100.xml")
        );
        
        String result = service.findLibrary().findLibraryByAgency(710100).agencyName;

        verify(4, postRequestedFor(urlEqualTo("/")));
        
        Assert.assertEquals("Københavns Biblioteker", result);
    }
    
}
