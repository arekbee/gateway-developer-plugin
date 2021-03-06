/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayconfig.config.loader;

import com.ca.apim.gateway.cagatewayconfig.beans.Bundle;

import java.io.File;

public interface EntityLoader {

    Object loadSingle(String name, File entitiesFile);
    void load(Bundle bundle, File rootDir);
    void load(Bundle bundle, String name, String value);

    String getEntityType();
}
