/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayconfig.environment;

public class DeploymentBundleException extends RuntimeException {
    public DeploymentBundleException(String message) {
        super(message);
    }

    public DeploymentBundleException(String message, Throwable cause) {
        super(message, cause);
    }
}
