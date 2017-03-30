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

import org.junit.*;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public class ShowOrderIT extends WireMocker {

    private OpenAgencyServiceFromURL service;

    public ShowOrderIT() throws Exception {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        service = OpenAgencyServiceFromURL.builder().build(url);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getOrder method, of class ShowOrder.
     */
    @Test
    public void testShowOrder() throws Exception {
        int[] showOrder = service.showOrder().getOrderAsIntList(870970);

        System.out.println("showOrder = " + Arrays.toString(showOrder));
        assertArrayEquals(new int[]{870970, 150000, 810015, 810010}, showOrder);
    }

}
