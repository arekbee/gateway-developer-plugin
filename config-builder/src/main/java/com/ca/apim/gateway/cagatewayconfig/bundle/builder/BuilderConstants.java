/*
 * Copyright (c) 2018. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayconfig.bundle.builder;

@SuppressWarnings("squid:S2068") // sonarcloud believes this is a hardcoded password
public final class BuilderConstants {

    public static final String STORED_PASSWORD_REF_FORMAT = "${secpass.%s.plaintext}";

    private BuilderConstants(){
    }
}
