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
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

import java.util.Arrays;

/**
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public class ShowOrderIT extends WireMocker  {
    final String request="<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:showOrderRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>870970</ns1:agencyId></ns1:showOrderRequest></S:Body></S:Envelope>";

    /**
     * Test of getOrder method, of class ShowOrder.
     */
    @Test
    public void testShowOrder() throws Exception {
        stubForReturnOK( request, "showOrder_870970.xml");

        int[] showOrder = service.showOrder().getOrderAsIntList(870970);

        System.out.println("showOrder = " + Arrays.toString(showOrder));
        assertArrayEquals(new int[]{870970, 150000, 810015, 810010}, showOrder);

        verify(1, postRequestedFor(urlEqualTo("/")));
    }

    /**
     * Test of getOrder method, of class ShowOrder.
     */
    @Test
    public void testShowOrderRetries() throws Exception {
        stubForSequence( request,
                aResponse().withStatus(500).withBody("showOrder_870970.xml"),
                aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK),
                aResponse().withFixedDelay(requestTimeout+200).withStatus(200).withBody("fisk"),
                aResponse().withFixedDelay(200).withStatus(200).withHeader("Content-Type", "text/xml; charset=utf-8").withBodyFile("showOrder_870970.xml")
        );
        
        int[] showOrder = service.showOrder().getOrderAsIntList(870970);

        System.out.println("showOrder = " + Arrays.toString(showOrder));
        assertArrayEquals(new int[]{870970, 150000, 810015, 810010}, showOrder);

        verify(4, postRequestedFor(urlEqualTo("/")));
    }
    
}
