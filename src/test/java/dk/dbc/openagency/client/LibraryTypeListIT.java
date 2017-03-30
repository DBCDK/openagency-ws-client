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

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public class LibraryTypeListIT extends WireMocker {
    private OpenAgencyServiceFromURL service;

    public LibraryTypeListIT() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        service= OpenAgencyServiceFromURL.builder().build(url);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getType method, of class LibraryTypeList.
     */
    @Test
    public void testGetType() throws Exception {
        LibraryTypeList request = service.libraryTypeList();
        assertEquals(LibraryType.UNKNOWN, request.getType("000200"));
        assertEquals(LibraryType.SKOLEBIBLIOTEK, request.getType("300615"));
        assertEquals(LibraryType.FOLKEBIBLIOTEK, request.getType("710100"));
        assertEquals(LibraryType.FORSKNINGSBIBLIOTEK, request.getType("874620"));
        assertEquals(LibraryType.UNKNOWN, request.getType("999999"));
    }

}
