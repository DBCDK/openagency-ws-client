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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public class CacheTest {

    public CacheTest() {
    }

    /**
     * Test Caching.
     */
    @Test
    public void testGet() {
        // TODO: Understand this code..
        // FIXME: test for jenkins file
        long maxAgeMs = 10;
        Cache<String, String> cache = new Cache<>(maxAgeMs);
        Provider provider = mock(Provider.class);
        when(provider.provide("a")).thenReturn("1");
        when(provider.provide("b")).thenReturn("2");

        assertEquals("1", cache.get("a", provider));
        long entryTimedOutMillis = System.currentTimeMillis() + maxAgeMs;
        assertEquals("1", cache.get("a", provider));
        assertTrue("Fetched again before timed out", System.currentTimeMillis() < entryTimedOutMillis);
        assertEquals("2", cache.get("b", provider));

        long sleepFor = entryTimedOutMillis - System.currentTimeMillis();
        if (sleepFor > 0) {
            try {
                Thread.sleep(sleepFor);
            } catch (InterruptedException e) {
                System.err.println("Caught: " + e.getMessage());
            }
        }

        assertTrue("First entry is timed out", System.currentTimeMillis() >= entryTimedOutMillis);

        verify(provider, times(2)).provide(anyString()); // called twice before entry timed out
        assertEquals("1", cache.get("a", provider));
        verify(provider, times(3)).provide(anyString()); // called 3 times when getting one that is timed out
    }


    // You can't mock an interface ;-)
    private static class Provider implements Cache.CacheProvider<String, String> {

        @Override
        public String provide(String key) {
            return null;
        }
    }

}
