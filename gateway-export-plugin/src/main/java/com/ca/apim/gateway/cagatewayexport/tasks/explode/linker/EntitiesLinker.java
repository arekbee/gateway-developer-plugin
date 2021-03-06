/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayexport.tasks.explode.linker;

import com.ca.apim.gateway.cagatewayconfig.beans.Bundle;

import java.io.File;

public interface EntitiesLinker {

    void link(Bundle filteredBundle, Bundle bundle);

    default void link(Bundle filteredBundle, Bundle bundle, File rootFolder) {
        link(filteredBundle, bundle);
    }
}
