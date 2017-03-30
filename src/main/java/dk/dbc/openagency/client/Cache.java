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

import java.util.HashMap;

/**
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 * @param <K>
 * @param <V>
 */
class Cache<K, V> {

    interface CacheProvider<K, V> {

        /**
         * Provide an value for key
         *
         * @param key
         * @return new value
         */
        V provide(K key);
    }

    private final long maxAgeMillis;
    private final HashMap<K, CacheEntry<V>> cache;

    /**
     * Cache constructor, setting max age of cache entries
     * 
     * @param maxAgeMillis
     */
    public Cache(long maxAgeMillis) {
        this.maxAgeMillis = maxAgeMillis;
        this.cache = new HashMap<>();
    }

    /**
     * Fetch from cache
     *
     * If no entry, or entry is expired, then refresh cache using provider
     *
     * @param key
     * @param provider
     * @return cached entry
     */
    public V get(K key, CacheProvider<K, V> provider) {
        CacheEntry<V> cacheEntry;
        synchronized (this) {
            cacheEntry = cache.get(key);
            if (cacheEntry == null) {
                cacheEntry = new CacheEntry<>();
                cache.put(key, cacheEntry);
            }
        }
        synchronized (cacheEntry) {
            if (!cacheEntry.isValid()) {
                cacheEntry.setEntry(provider.provide(key));
            }
        }
        return cacheEntry.getEntry();
    }

    private class CacheEntry<V> {

        private long createdMillis;
        private V entry;

        public CacheEntry() {
            createdMillis = Long.MIN_VALUE;
            entry = null;
        }

        public boolean isValid() {
            return createdMillis + maxAgeMillis > System.currentTimeMillis();
        }

        public V getEntry() {
            return entry;
        }

        public void setEntry(V entry) {
            createdMillis = System.currentTimeMillis();
            this.entry = entry;
        }
    }

}
