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
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import com.github.tomakehurst.wiremock.http.Fault;
import dk.dbc.openagency.client.LibraryRuleHandler.Rule;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LibraryRuleHandlerIT extends WireMocker {


    @Test
    public void testIsAllowed() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                        "libraryRule_710100.xml");
        

        LibraryRuleHandler libraryRules = service.libraryRules();
        assertEquals(true, libraryRules.isAllowed("710100", Rule.AUTH_AGENCY_COMMON_RECORD));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.AUTH_COMMON_NOTES));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.AUTH_COMMON_SUBJECTS));
        assertEquals(false, libraryRules.isAllowed("710100", Rule.AUTH_DBC_RECORDS));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.AUTH_EXPORT_HOLDINGS));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.AUTH_PUBLIC_LIB_COMMON_RECORD));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.AUTH_RET_RECORD));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.AUTH_ROOT));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.CREATE_ENRICHMENTS));
        assertEquals(true, libraryRules.isAllowed("710100", Rule.USE_ENRICHMENTS));
        assertEquals(false, libraryRules.isAllowed("710100", Rule.AUTH_CREATE_COMMON_RECORD));

        assertEquals(true, libraryRules.isAllowed(710100, Rule.AUTH_AGENCY_COMMON_RECORD));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.AUTH_COMMON_NOTES));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.AUTH_COMMON_SUBJECTS));
        assertEquals(false, libraryRules.isAllowed(710100, Rule.AUTH_DBC_RECORDS));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.AUTH_EXPORT_HOLDINGS));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.AUTH_PUBLIC_LIB_COMMON_RECORD));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.AUTH_RET_RECORD));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.AUTH_ROOT));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.CREATE_ENRICHMENTS));
        assertEquals(true, libraryRules.isAllowed(710100, Rule.USE_ENRICHMENTS));
        assertEquals(false, libraryRules.isAllowed(710100, Rule.USE_HOLDINGS_ITEM));
        assertEquals(false, libraryRules.isAllowed(710100, Rule.AUTH_CREATE_COMMON_RECORD));
    }
    
    @Test(expected = OpenAgencyException.class)
    public void testIsAllowed_agencyNotFound_wrongAgencyFound() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>123456</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                        "libraryRule_710100.xml");

        LibraryRuleHandler libraryRules = service.libraryRules();
        libraryRules.isAllowed("123456", Rule.AUTH_AGENCY_COMMON_RECORD);
    }
    
    @Test(expected = OpenAgencyException.class)
    public void testIsAllowed_agencyNotFound_emptyResult() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>123456</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                "libraryRule_empty.xml");
        LibraryRuleHandler libraryRules = service.libraryRules();
        libraryRules.isAllowed("123456", Rule.AUTH_AGENCY_COMMON_RECORD);
    }

    @Test
    public void testgetCatalogingTemplateString() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                        "libraryRule_710100.xml");

        LibraryRuleHandler libraryRules = service.libraryRules();
        String actual = libraryRules.getCatalogingTemplate("710100");
        assertEquals(actual, "fbs");
    }

    @Test
    public void testgetCatalogingTemplateInt() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                        "libraryRule_710100.xml");

        LibraryRuleHandler libraryRules = service.libraryRules();
        String actual = libraryRules.getCatalogingTemplate(710100);
        assertEquals(actual, "fbs");
    }


    @Test
    public void testRetriesFor_executeRequest() throws Exception {
        stubForSequence("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                aResponse().withStatus(500).withBody("Failure"),
                aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK),
                aResponse().withStatus(200).withFixedDelay(requestTimeout+200).withHeader("Content-Type", "text/xml; charset=utf-8").withBodyFile("libraryRule_710100.xml"),
                aResponse().withStatus(200).withFixedDelay(200).withHeader("Content-Type", "text/xml; charset=utf-8").withBodyFile("libraryRule_710100.xml")
        );                                                    
        
        LibraryRuleHandler libraryRules = service.libraryRules();
        String actual = libraryRules.getCatalogingTemplate("710100");
        assertEquals(actual, "fbs");

        verify(4, postRequestedFor(urlEqualTo("/")));
    }


}
