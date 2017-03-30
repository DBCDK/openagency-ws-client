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

/**
 *
 * @author Morten Bøgeskov <mb@dbc.dk>
 */
public enum LibraryType {

    UNKNOWN("Ukendt"),
    ALLE("Alle"),
    FOLKEBIBLIOTEK("Folkebibliotek"),
    FORSKNINGSBIBLIOTEK("Forskningsbibliotek"),
    SKOLEBIBLIOTEK("Skolebibliotek");

    private final String value;

    LibraryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LibraryType fromValue(String v) {
        for (LibraryType c : LibraryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return UNKNOWN;
    }
}
