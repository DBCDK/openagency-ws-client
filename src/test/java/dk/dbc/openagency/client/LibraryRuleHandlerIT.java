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

import com.github.tomakehurst.wiremock.http.Fault;
import dk.dbc.openagency.client.LibraryRuleHandler.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LibraryRuleHandlerIT extends WireMocker {


    @Test
    public void testIsAllowed() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                "libraryRule_710100.xml");

        LibraryRuleHandler libraryRules = service.libraryRules();
        assertTrue(libraryRules.isAllowed("710100", Rule.AUTH_AGENCY_COMMON_RECORD));
        assertTrue(libraryRules.isAllowed("710100", Rule.AUTH_COMMON_NOTES));
        assertTrue(libraryRules.isAllowed("710100", Rule.AUTH_COMMON_SUBJECTS));
        assertFalse(libraryRules.isAllowed("710100", Rule.AUTH_DBC_RECORDS));
        assertTrue(libraryRules.isAllowed("710100", Rule.AUTH_EXPORT_HOLDINGS));
        assertTrue(libraryRules.isAllowed("710100", Rule.AUTH_PUBLIC_LIB_COMMON_RECORD));
        assertTrue(libraryRules.isAllowed("710100", Rule.AUTH_RET_RECORD));
        assertTrue(libraryRules.isAllowed("710100", Rule.AUTH_ROOT));
        assertTrue(libraryRules.isAllowed("710100", Rule.CREATE_ENRICHMENTS));
        assertTrue(libraryRules.isAllowed("710100", Rule.USE_ENRICHMENTS));
        assertFalse(libraryRules.isAllowed("710100", Rule.USE_HOLDINGS_ITEM));
        assertFalse(libraryRules.isAllowed("710100", Rule.AUTH_CREATE_COMMON_RECORD));
        assertFalse(libraryRules.isAllowed("710100", Rule.AUTH_ADD_DK5_TO_PHD_ALLOWED));
        assertFalse(libraryRules.isAllowed("710100", Rule.AUTH_METACOMPASS));
        assertTrue(libraryRules.isAllowed("710100", Rule.PART_OF_DANBIB));
        assertFalse(libraryRules.isAllowed("710100", Rule.PART_OF_BIBLIOTEK_DK));

        assertTrue(libraryRules.isAllowed(710100, Rule.AUTH_AGENCY_COMMON_RECORD));
        assertTrue(libraryRules.isAllowed(710100, Rule.AUTH_COMMON_NOTES));
        assertTrue(libraryRules.isAllowed(710100, Rule.AUTH_COMMON_SUBJECTS));
        assertFalse(libraryRules.isAllowed(710100, Rule.AUTH_DBC_RECORDS));
        assertTrue(libraryRules.isAllowed(710100, Rule.AUTH_EXPORT_HOLDINGS));
        assertTrue(libraryRules.isAllowed(710100, Rule.AUTH_PUBLIC_LIB_COMMON_RECORD));
        assertTrue(libraryRules.isAllowed(710100, Rule.AUTH_RET_RECORD));
        assertTrue(libraryRules.isAllowed(710100, Rule.AUTH_ROOT));
        assertTrue(libraryRules.isAllowed(710100, Rule.CREATE_ENRICHMENTS));
        assertTrue(libraryRules.isAllowed(710100, Rule.USE_ENRICHMENTS));
        assertFalse(libraryRules.isAllowed(710100, Rule.USE_HOLDINGS_ITEM));
        assertFalse(libraryRules.isAllowed(710100, Rule.AUTH_CREATE_COMMON_RECORD));
        assertFalse(libraryRules.isAllowed(710100, Rule.AUTH_ADD_DK5_TO_PHD_ALLOWED));
        assertFalse(libraryRules.isAllowed(710100, Rule.AUTH_METACOMPASS));
        assertTrue(libraryRules.isAllowed(710100, Rule.PART_OF_DANBIB));
        assertFalse(libraryRules.isAllowed(710100, Rule.PART_OF_BIBLIOTEK_DK));
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
    public void testgetLibrariesByCatalogingTemplateSet() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:libraryRule><ns1:name>cataloging_template_set</ns1:name><ns1:string>fbs</ns1:string></ns1:libraryRule></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                "libraryRule_cataloging_template_set.xml");

        final LibraryRuleHandler libraryRules = service.libraryRules();
        final Set<String> actual = libraryRules.getLibrariesByCatalogingTemplateSet("fbs");
        assertEquals(new HashSet<>(Arrays.asList("710100", "710200")), actual);
    }

    @Test
    public void testRetriesFor_executeRequest() throws Exception {
        stubForSequence("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                aResponse().withStatus(500).withBody("Failure"),
                aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK),
                aResponse().withStatus(200).withFixedDelay(requestTimeout + 200).withHeader("Content-Type", "text/xml; charset=utf-8").withBodyFile("libraryRule_710100.xml"),
                aResponse().withStatus(200).withFixedDelay(200).withHeader("Content-Type", "text/xml; charset=utf-8").withBodyFile("libraryRule_710100.xml")
        );

        LibraryRuleHandler libraryRules = service.libraryRules();
        String actual = libraryRules.getCatalogingTemplate("710100");
        assertEquals(actual, "fbs");

        verify(4, postRequestedFor(urlEqualTo("/")));
    }

    @Test
    public void testGetAllowedLibraryRules() throws Exception {
        stubForReturnOK("<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns1:libraryRulesRequest xmlns:ns1=\"http://oss.dbc.dk/ns/openagency\"><ns1:agencyId>710100</ns1:agencyId></ns1:libraryRulesRequest></S:Body></S:Envelope>",
                "libraryRule_710100.xml");

        final LibraryRuleHandler libraryRuleHandler = service.libraryRuleHandler;
        final Set<String> expected = new HashSet<>(Arrays.asList(
                "auth_public_lib_common_record",
                "auth_common_notes",
                "auth_agency_common_record",
                "create_enrichments",
                "part_of_danbib",
                "auth_common_subjects",
                "auth_root",
                "auth_export_holdings",
                "use_enrichments",
                "auth_ret_record",
                "auth_add_dk5_to_phd"
        ));
        final Set<String> actual = libraryRuleHandler.getAllowedLibraryRules("710100");
        assertEquals(expected, actual);
    }

}
