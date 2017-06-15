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

import net.jodah.failsafe.Failsafe;

import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Object to access OpenAgency's LibraryTypeList call
 *
 * Caches results for 8 hours
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public class LibraryTypeList {

    private static final int MAX_AGE_HOURS = 8;

    private final Cache<String, HashMap<String, LibraryType>> cache;

    private final OpenAgencyServiceFromURL service;

    LibraryTypeList(OpenAgencyServiceFromURL service) {
        this.cache = new Cache<>(MAX_AGE_HOURS * 3600 * 1000);
        this.service = service;
    }

    /**
     * fetch A {@link LibraryType} for an agency
     *
     * @param agencyId agency to query for
     * @return {@link LibraryType} describing agency
     * @throws OpenAgencyException
     */
    public LibraryType getType(String agencyId) throws OpenAgencyException {
        HashMap<String, LibraryType> cachedList = cache.get("", new Cache.CacheProvider<String, HashMap<String, LibraryType>>() {

            @Override
            public HashMap<String, LibraryType> provide(String key) {
                try {
                    return getListFromWebService();
                } catch (OpenAgencyException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        LibraryType type = cachedList.get(agencyId);
        if (type == null) {
            type = LibraryType.UNKNOWN;
        }
        return type;
    }

    private HashMap<String, LibraryType> getListFromWebService() throws OpenAgencyException {
        LibraryTypeListRequest request = new LibraryTypeListRequest();
        LibraryTypeListResponse response;
        try {
            synchronized (service) {
                response = Failsafe.with(service.RETRYPOLICY).get(()->service.port.libraryTypeList(request));
            }
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SocketTimeoutException) {
                throw new OpenAgencyException(ErrorType.SERVICE_UNAVAILABLE, cause);
            }
            throw e;
        }
        ErrorType error = response.getError();
        if (error != null) {
            throw new OpenAgencyException(error);
        }
        HashMap<String, LibraryType> tempMap = new HashMap<>();
        for (LibraryTypeInfo info : response.getLibraryTypeInfo()) {
            String agencyId = info.getAgencyId();
            String agencyType = info.getAgencyType();
            tempMap.put(agencyId, LibraryType.fromValue(agencyType));
        }
        return tempMap;
    }

}
