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


import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import org.junit.Before;
import org.junit.Rule;

public class WireMocker {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    protected OpenAgencyServiceFromURL service;
    int requestTimeout=1000;


    @Before
    public void setUp() {
        service = OpenAgencyServiceFromURL.builder().requestTimeout(requestTimeout).build("http://localhost:" + wireMockRule.port() + "/");
        WireMock.resetAllScenarios();
        WireMock.removeAllMappings();
    }


    protected void stubForReturnOK(String request, String fileName) {
        stubFor(post(urlEqualTo("/"))
                .withRequestBody(equalToXml(request))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "text/xml; charset=utf-8")
                        .withBodyFile(fileName)
                )
        );
    }


    protected void stubForSequence(String request, ResponseDefinitionBuilder... ResponseList) {
        for (int i = 0; i < ResponseList.length; i++) {
            stubFor(post(urlEqualTo("/"))
                    .withRequestBody(equalToXml(request))
                    .inScenario("request").whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i))
                    .willReturn(ResponseList[i])
                    .willSetStateTo(String.valueOf(i + 1))
            );

        }
    }
};

