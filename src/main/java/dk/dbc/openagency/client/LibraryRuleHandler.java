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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Object to access OpenAgency's libraryRules call
 * <p>
 * Caches results for 8 hours
 */
public class LibraryRuleHandler {
    private final Logger log = LoggerFactory.getLogger(LibraryRuleHandler.class);

    private static final int MAX_AGE_HOURS = 8;
    private final Cache<String, Set<String>> libraryRulesCache;
    private final Cache<String, String> catalogingTemplateCache;
    private final Cache<String, Set<String>> catalogingTemplateSetCache;
    private final OpenAgencyServiceFromURL service;

    public enum Rule {
        CREATE_ENRICHMENTS("create_enrichments"),
        PART_OF_BIBLIOTEK_DK("part_of_bibliotek_dk"),
        USE_ENRICHMENTS("use_enrichments"),
        USE_LOCALDATA_STREAM("use_localdata_stream"),
        USE_HOLDINGS_ITEM("use_holdings_item"),
        AUTH_ROOT("auth_root"),
        AUTH_COMMON_SUBJECTS("auth_common_subjects"),
        AUTH_COMMON_NOTES("auth_common_notes"),
        AUTH_DBC_RECORDS("auth_dbc_records"),
        AUTH_PUBLIC_LIB_COMMON_RECORD("auth_public_lib_common_record"),
        AUTH_RET_RECORD("auth_ret_record"),
        AUTH_AGENCY_COMMON_RECORD("auth_agency_common_record"),
        AUTH_EXPORT_HOLDINGS("auth_export_holdings"),
        AUTH_CREATE_COMMON_RECORD("auth_create_common_record"),
        AUTH_ADD_DK5_TO_PHD_ALLOWED("auth_create_common_record"),
        AUTH_METACOMPASS("auth_metacompass");

        private final String value;

        Rule(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.getValue();
        }
    }

    public LibraryRuleHandler(OpenAgencyServiceFromURL service, int maxAge) {
        this.libraryRulesCache = new Cache<>(maxAge * 3600 * 1000);
        this.catalogingTemplateCache = new Cache<>(maxAge * 3600 * 1000);
        this.catalogingTemplateSetCache = new Cache<>(maxAge * 3600 * 1000);
        this.service = service;
    }

    public LibraryRuleHandler(OpenAgencyServiceFromURL service) {
        this.libraryRulesCache = new Cache<>(MAX_AGE_HOURS * 3600 * 1000);
        this.catalogingTemplateCache = new Cache<>(MAX_AGE_HOURS * 3600 * 1000);
        this.catalogingTemplateSetCache = new Cache<>(MAX_AGE_HOURS * 3600 * 1000);

        this.service = service;
    }

    public boolean isAllowed(int agencyId, Rule rule) throws OpenAgencyException {
        return isAllowed(Helper.agencyIdToString(agencyId), rule);
    }

    public boolean isAllowed(String agencyId, Rule rule) throws OpenAgencyException {
        try {
            Set<String> allowedLibraryRules = libraryRulesCache.get(agencyId, new Cache.CacheProvider<String, Set<String>>() {
                @Override
                public Set<String> provide(String key) {
                    try {
                        return getLibraryRulesFromWebService(key);
                    } catch (OpenAgencyException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            return allowedLibraryRules.contains(rule.getValue());
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof OpenAgencyException) {
                throw (OpenAgencyException) cause;
            }
            throw e;
        }
    }

    public String getCatalogingTemplate(String agencyId) throws OpenAgencyException {
        try {
            return catalogingTemplateCache.get(agencyId, new Cache.CacheProvider<String, String>() {
                @Override
                public String provide(String key) {
                    try {
                        List<LibraryRule> libraryRules = getLibraryRulesAllFromWebService(agencyId);
                        for (LibraryRule libraryRule : libraryRules) {
                            if ("cataloging_template_set".equals(libraryRule.getName()) && !libraryRule.getString().isEmpty()) {
                                return libraryRule.getString();
                            }
                        }

                        throw new OpenAgencyException(ErrorType.AGENCY_NOT_FOUND);
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

    public Set<String> getLibrariesByCatalogingTemplateSet(String catalogingTemplateSet) throws OpenAgencyException {
        try {
            return catalogingTemplateSetCache.get(catalogingTemplateSet, new Cache.CacheProvider<String, Set<String>>() {
                @Override
                public Set<String> provide(String key) {
                    try {
                        return getLibrariesByCatalogingTemplateSetFromWebService(catalogingTemplateSet);
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

    private Set<String> getLibraryRulesFromWebService(String agencyId) throws OpenAgencyException {
        Set<String> allowedRules = new HashSet<>();
        List<LibraryRule> libraryRules = getLibraryRulesAllFromWebService(agencyId);
        // Build result set
        for (LibraryRule libraryRule : libraryRules) {
            if (libraryRule.isBool() != null && libraryRule.isBool()) {
                allowedRules.add(libraryRule.getName());
            }
        }

        return allowedRules;
    }

    private LibraryRulesResponse executeRequest(LibraryRulesRequest request) throws OpenAgencyException {
        LibraryRulesResponse response;
        synchronized (service) {
            if (service.authentication != null) {
                request.setAuthentication(service.authentication);
            }

            try {
                response = Failsafe.with(service.RETRYPOLICY).get(() -> service.port.libraryRules(request));
            } catch (RuntimeException e) {
                log.error("Exception getting Library Rules from OpenAgency");
                Throwable cause = e.getCause();
                throw new OpenAgencyException(ErrorType.SERVICE_UNAVAILABLE, cause, request, null);
            }
        }
        return response;
    }

    private List<LibraryRule> getLibraryRulesAllFromWebService(String agencyId) throws OpenAgencyException {
        LibraryRulesRequest request = new LibraryRulesRequest();
        request.setAgencyId(agencyId);
        log.debug("Looking for agencyId {}", agencyId);

        LibraryRulesResponse response = executeRequest(request);

        ErrorType error = response.getError();
        if (error != null) {
            log.error("Looking up library rules for agency {} failed", agencyId);
            throw new OpenAgencyException(error);
        }

        if (response.libraryRules == null) {
            throw new OpenAgencyException(ErrorType.AGENCY_NOT_FOUND, request, response);
        }

        List<LibraryRule> libraryRules = null;
        for (LibraryRules library : response.libraryRules) {
            if (library.getAgencyId().equals(agencyId)) {
                libraryRules = library.getLibraryRule();
            }
        }
        if (libraryRules == null) {
            throw new OpenAgencyException(ErrorType.AGENCY_NOT_FOUND, request, response);
        }

        return libraryRules;

    }

    private Set<String> getLibrariesByCatalogingTemplateSetFromWebService(String catalogingTemplateSet) throws OpenAgencyException {
        LibraryRulesRequest request = new LibraryRulesRequest();
        LibraryRule rule = new LibraryRule();
        rule.setName("cataloging_template_set");
        rule.setString(catalogingTemplateSet);
        request.getLibraryRule().add(rule);
        log.debug("Looking for catalogingTemplateSet {}", catalogingTemplateSet);

        LibraryRulesResponse response = executeRequest(request);

        ErrorType error = response.getError();
        if (error != null) {
            log.error("Looking up library rules for {} libraries failed", catalogingTemplateSet);
            throw new OpenAgencyException(error);
        }

        Set<String> libraryRules = new HashSet<>();
        if (response.libraryRules != null) {
            for (LibraryRules library : response.libraryRules) {
                libraryRules.add(library.getAgencyId());
            }
        }

        return libraryRules;
    }


}
