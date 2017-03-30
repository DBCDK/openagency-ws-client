/*
 * Copyright (C) 2015 DBC A/S (http://dbc.dk/)
 *
 * This is part of dbc-openagency-ws-java
 *
 * dbc-openagency-ws-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dbc-openagency-ws-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dbc.openagency.client;

import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Object to access OpenAgency's ShowOrder call
 *
 * Caches results for 8 hours
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public class ShowOrder {

    private static final int MAX_AGE_HOURS = 8;

    private final Cache<Integer, List<String>> cache;
    private final OpenAgencyServiceFromURL service;

    ShowOrder(OpenAgencyServiceFromURL service) {
        this.cache = new Cache<>(MAX_AGE_HOURS * 3600 * 1000);
        this.service = service;
    }

    /**
     * Fetch the order in which the agency wishes to see records based upon
     * owner
     *
     * @param agencyId agency to query for
     * @return List of Strings
     * @throws OpenAgencyException
     */
    public List<String> getOrder(int agencyId) throws OpenAgencyException {
        try {
            return cache.get(agencyId, new Cache.CacheProvider<Integer, List<String>>() {
                @Override
                public List<String> provide(Integer key) {
                    try {
                        return getOrderFromWebService(key);
                    } catch (OpenAgencyException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof OpenAgencyException) {
                throw (OpenAgencyException) cause;
            }
            throw e;
        }
    }

    private List<String> getOrderFromWebService(int agencyId) throws OpenAgencyException {
        synchronized (service) {
            ShowOrderRequest request = new ShowOrderRequest();
            request.setAgencyId(Helper.agencyIdToString(agencyId));
            if (service.authentication != null) {
                request.setAuthentication(service.authentication);
            }
            ShowOrderResponse response;
            try {
                synchronized (service) {
                    response = service.port.showOrder(request);
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
            return response.getAgencyId();
        }
    }

    /**
     * See {@link #getOrder(int)}
     *
     * @param agencyId agency to query for
     * @return List of Strings
     * @throws NumberFormatException
     * @throws OpenAgencyException
     */
    public int[] getOrderAsIntList(int agencyId) throws NumberFormatException, OpenAgencyException {
        List<String> agencyStrings = getOrder(agencyId);
        int[] agencies = new int[agencyStrings.size()];
        for (int i = 0 ; i < agencies.length ; i++) {
            agencies[i] = Integer.parseInt(agencyStrings.get(i), 10);
        }
        return agencies;
    }

}
