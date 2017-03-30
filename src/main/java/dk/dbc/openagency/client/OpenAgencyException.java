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

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
public class OpenAgencyException extends Exception {

    public OpenAgencyException(ErrorType error) {
        super("Webservice RESPONDS with content: " + errorString(error));
        this.request = null;
        this.response = null;
        this.error = error;
    }

    public OpenAgencyException(ErrorType error, LibraryRulesRequest request, LibraryRulesResponse response ) {
        super("Webservice RESPONDS with content: " + errorString(error));
        this.request = request;
        this.response = response;
        this.error = error;
    }

    public OpenAgencyException(ErrorType error, Throwable cause) {
        super("Webservice RESPONDS with content: " + errorString(error), cause);
        this.request = null;
        this.response = null;
        this.error = error;

    }

    public OpenAgencyException(ErrorType error, Throwable cause, LibraryRulesRequest request, LibraryRulesResponse response ) {
        super("Webservice RESPONDS with content: " + errorString(error), cause);
        this.request = request;
        this.response = response;
        this.error = error;
    }

    public LibraryRulesRequest getRequest() {
        return request;
    }

    public LibraryRulesResponse getResponse() {
        return response;
    }

    public ErrorType getError() {
        return error;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        Throwable cause = getCause();
        if (cause != null) {
            message += ": " + cause.getMessage();
        }
        return message;
    }

    private static String errorString(ErrorType error) {
        switch (error) {
            case AGENCY_NOT_FOUND:
                return "Agency not found";
            case AUTHENTICATION_ERROR:
                return "Authentication error";
            case ERROR_IN_REQUEST:
                return "Error in request";
            case NO_AGENCIES_FOUND:
                return "No agencies found";
            case NO_USERID_SELECTED:
                return "No userid selected";
            case PROFILE_NOT_FOUND:
                return "No profile found";
            case SERVICE_UNAVAILABLE:
                return "Service unavailable";
            default:
                throw new IllegalStateException("Don't know error: " + error.value());
        }
    }

    private final LibraryRulesRequest request;
    private final LibraryRulesResponse response;
    private final ErrorType error;

}
