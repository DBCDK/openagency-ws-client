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

import dk.dbc.openagency.client.LibraryRuleHandler.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class LibraryRuleHandlerIT extends WireMocker {

    @Test
    public void testIsAllowed() throws Exception {
        OpenAgencyServiceFromURL service= OpenAgencyServiceFromURL.builder().build(url);
        
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
        OpenAgencyServiceFromURL service= OpenAgencyServiceFromURL.builder().build(url);
        LibraryRuleHandler libraryRules = service.libraryRules();
        libraryRules.isAllowed("123456", Rule.AUTH_AGENCY_COMMON_RECORD);
    }
    
    @Test(expected = OpenAgencyException.class)
    public void testIsAllowed_agencyNotFound_emptyResult() throws Exception {
        OpenAgencyServiceFromURL service= OpenAgencyServiceFromURL.builder().build(url + "emptyResult");
        LibraryRuleHandler libraryRules = service.libraryRules();
        libraryRules.isAllowed("123456", Rule.AUTH_AGENCY_COMMON_RECORD);
    }

    @Test
    public void testgetCatalogingTemplateString() throws Exception {
        OpenAgencyServiceFromURL service = OpenAgencyServiceFromURL.builder().build(url);
        LibraryRuleHandler libraryRules = service.libraryRules();
        String actual = libraryRules.getCatalogingTemplate("710100");
        assertEquals(actual, "fbs");
    }

    @Test
    public void testgetCatalogingTemplateInt() throws Exception {
        OpenAgencyServiceFromURL service = OpenAgencyServiceFromURL.builder().build(url);
        LibraryRuleHandler libraryRules = service.libraryRules();
        String actual = libraryRules.getCatalogingTemplate(710100);
        assertEquals(actual, "fbs");
    }

}
