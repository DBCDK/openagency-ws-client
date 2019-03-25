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

import net.jodah.failsafe.Failsafe;

import java.net.SocketTimeoutException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object to access OpenAgency's FindLibrary call
 *
 * Caches results for 8 hours
 */
public class FindLibrary {

    private final Logger log = LoggerFactory.getLogger(FindLibrary.class);
    
    private static final int MAX_AGE_HOURS = 8;

    private final Cache<Integer, PickupAgency> cache;
    private final OpenAgencyServiceFromURL service;

    FindLibrary(OpenAgencyServiceFromURL service) {
        this.cache = new Cache<>(MAX_AGE_HOURS * 3600 * 1000);
        this.service = service;
    }

    /**
     * Fetch Library information for an agency
     *
     * @param agencyId agency to query for
     * @return PickupAgency properties
     * @throws OpenAgencyException
     */
    public PickupAgency findLibraryByAgency(int agencyId) throws OpenAgencyException {
        try {
            return cache.get(agencyId, new Cache.CacheProvider<Integer, PickupAgency>() {
                @Override
                public PickupAgency provide(Integer agencyId) {
                    try {
                        return getFindLibraryByAgencyFromWebService(agencyId);
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

    private PickupAgency getFindLibraryByAgencyFromWebService(Integer agencyId) throws OpenAgencyException {
        
            FindLibraryRequest request = new FindLibraryRequest();
            
            request.setAgencyId(Helper.agencyIdToString(agencyId));
            if (service.authentication != null) {
                request.setAuthentication(service.authentication);
            }
            FindLibraryResponse response;
            try {
                synchronized (service) {
                    response = Failsafe.with(service.RETRYPOLICY).get(()->service.port.findLibrary(request));
                    
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
            List<PickupAgency> pickupAgency = response.getPickupAgency();
            if (pickupAgency.isEmpty()) {
                return null;
            } else {
            if (pickupAgency.size() == 1) {
                return pickupAgency.get(0);
            } else {
                log.error("Multiple values for agency '{}': {}", agencyId, response);
                throw new OpenAgencyException(ErrorType.ERROR_IN_REQUEST);
            }
        }
    }
}
