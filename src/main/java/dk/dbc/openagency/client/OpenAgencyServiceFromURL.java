/*
 * Copyright (C) 2015 DBC A/S (http://dbc.dk/)
 *
 * This is part of dbc-openagency-ws-java
 *
 * dbc-openagency-ws-java-1.19 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dbc-openagency-ws-java-1.19 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dbc.openagency.client;

import com.sun.xml.ws.client.BindingProviderProperties;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
public class OpenAgencyServiceFromURL {
    private final Logger log = LoggerFactory.getLogger(OpenAgencyServiceFromURL.class);

    final RetryPolicy RETRYPOLICY = new RetryPolicy()
            .retryOn(RuntimeException.class, Exception.class)
            .withDelay(500, TimeUnit.MILLISECONDS)
            .withMaxRetries(7);

    final OpenAgencyService service;
    final Authentication authentication;
    final OpenAgencyPortType port;
    final FindLibrary findLibrary;
    final LibraryRuleHandler libraryRuleHandler;
    final LibraryTypeList libraryTypeList;
    final ShowOrder showOrder;

    public static class Builder {

        private Authentication authentication = null;
        int connectTimeout = 2500;
        int requestTimeout = 10000;
        int cacheAge = 8;

        private Builder() {
        }

        public Builder authentication(String user, String group, String password) {
            authentication = new Authentication();
            authentication.userIdAut = user;
            authentication.groupIdAut = group;
            authentication.passwordAut = password;
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder requestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder setCacheAge(int newAge) {
            this.cacheAge = newAge;
            return this;
        }

        public OpenAgencyServiceFromURL build(String url) {
            OpenAgencyServiceFromURL service = new OpenAgencyServiceFromURL(url, authentication, connectTimeout, requestTimeout, cacheAge);
            return service;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private OpenAgencyServiceFromURL(String url, Authentication authentication, int connectTimeout, int requestTimeout, int cacheAge) {
        log.info("Create OpenAgency from url: '{}', connectTimeout:{}, requestTimeout:{}", url, connectTimeout, requestTimeout);
        this.authentication = authentication;
        URL wsdl = OpenAgencyServiceFromURL.class.getResource("/openagency.wsdl");
        service = new OpenAgencyService(wsdl);
        port = service.getOpenAgencyPortType();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        bindingProvider.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, connectTimeout);
        bindingProvider.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, requestTimeout);
        findLibrary = new FindLibrary(this);
        libraryRuleHandler = new LibraryRuleHandler(this, cacheAge);
        libraryTypeList = new LibraryTypeList(this);
        showOrder = new ShowOrder(this);
    }

    public FindLibrary findLibrary() {
        return findLibrary;
    }

    public ShowOrder showOrder() {
        return showOrder;
    }

    public LibraryTypeList libraryTypeList() {
        return libraryTypeList;
    }

    public LibraryRuleHandler libraryRules() {
        return libraryRuleHandler;
    }

}
