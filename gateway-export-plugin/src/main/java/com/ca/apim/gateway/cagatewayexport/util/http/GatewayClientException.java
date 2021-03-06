/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayexport.util.http;

public class GatewayClientException extends RuntimeException {
    public GatewayClientException(String message) {
        super(message);
    }

    public GatewayClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
