/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayconfig.config.loader;

import com.ca.apim.gateway.cagatewayconfig.beans.Bundle;
import com.ca.apim.gateway.cagatewayconfig.beans.KeyStoreType;
import com.ca.apim.gateway.cagatewayconfig.beans.PrivateKey;
import com.ca.apim.gateway.cagatewayconfig.util.json.JsonTools;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Collection;
import java.util.Map;

import static java.nio.file.Files.newInputStream;

@Singleton
public class PrivateKeyLoader extends EntityLoaderBase<PrivateKey> {

    private static final String FILE_NAME = "private-keys";

    @Inject
    PrivateKeyLoader(JsonTools jsonTools) {
        super(jsonTools);
    }

    @Override
    public String getEntityType() {
        return "PRIVATE_KEY";
    }

    @Override
    protected Class<PrivateKey> getBeanClass() {
        return PrivateKey.class;
    }

    @Override
    protected String getFileName() {
        return FILE_NAME;
    }

    @Override
    public void load(final Bundle bundle, final File rootDir) {
        super.load(bundle, rootDir);
        loadFromDirectory(bundle.getPrivateKeys().values(), new File(rootDir, "config/privateKeys"), false);
    }

    public static void loadFromDirectory(Collection<PrivateKey> privateKeys, File privateKeysDirectory, boolean failOnMissingKeys) {
        if (!privateKeys.isEmpty()) {
            // load p12 file
            if (privateKeysDirectory.exists()) {
                privateKeys.forEach(k -> {
                    File pk = new File(privateKeysDirectory, k.getAlias() + ".p12");
                    if (pk.exists()) {
                        k.setPrivateKeyFile(() -> newInputStream(pk.toPath()));
                    } else if (failOnMissingKeys) {
                        throw new ConfigLoadException("Private Key file for key '" + k.getAlias() + "' not found in the private keys directory specified: " + privateKeysDirectory.getPath());
                    }
                });
            } else if (failOnMissingKeys) {
                throw new ConfigLoadException("Directory specified for private keys does not exist: " + privateKeysDirectory.getPath());
            }
        }
    }

    @Override
    protected void putToBundle(Bundle bundle, @NotNull Map<String, PrivateKey> entitiesMap) {
        // Set the private key keystore type
        entitiesMap.forEach((key, pk) -> {
            pk.setAlias(key);
            pk.setKeyStoreType(KeyStoreType.fromName(pk.getKeystore()));

            bundle.getPrivateKeys().put(key, pk);
        });
    }
}