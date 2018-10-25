/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayconfig.tasks.zip.builder;

import com.ca.apim.gateway.cagatewayconfig.tasks.zip.beans.Bundle;
import com.ca.apim.gateway.cagatewayconfig.util.IdGenerator;
import com.ca.apim.gateway.cagatewayconfig.util.xml.DocumentTools;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.ca.apim.gateway.cagatewayconfig.tasks.zip.builder.EntityBuilder.BundleType.DEPLOYMENT;
import static com.ca.apim.gateway.cagatewayconfig.tasks.zip.builder.EntityBuilder.BundleType.ENVIRONMENT;
import static com.ca.apim.gateway.cagatewayconfig.util.TestUtils.assertOnlyMappingEntity;
import static com.ca.apim.gateway.cagatewayconfig.util.TestUtils.testDeploymentBundleWithOnlyMapping;
import static com.ca.apim.gateway.cagatewayconfig.util.entity.EntityTypes.CLUSTER_PROPERTY_TYPE;
import static com.ca.apim.gateway.cagatewayconfig.util.gateway.BundleElementNames.*;
import static com.ca.apim.gateway.cagatewayconfig.util.properties.PropertyConstants.PREFIX_ENV;
import static com.ca.apim.gateway.cagatewayconfig.util.properties.PropertyConstants.PREFIX_GATEWAY;
import static com.ca.apim.gateway.cagatewayconfig.util.xml.DocumentUtils.getSingleChildElement;
import static com.ca.apim.gateway.cagatewayconfig.util.xml.DocumentUtils.getSingleChildElementTextContent;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang.StringUtils.reverse;
import static org.junit.jupiter.api.Assertions.*;

class ClusterPropertyEntityBuilderTest {

    private static final IdGenerator ID_GENERATOR = new IdGenerator();
    private static final String ENV_PROP_1 = PREFIX_GATEWAY + "envprop1";
    private static final String ENV_PROP_2 = PREFIX_GATEWAY + "envprop2";
    private static final String STATIC_PROP_1 = "staticprop1";
    private static final String STATIC_PROP_2 = "staticprop2";
    private static final Map<String, String> ENV_PROPS = ImmutableMap.of(ENV_PROP_1, reverse(ENV_PROP_1), ENV_PROP_2, reverse(ENV_PROP_2));
    private static final Map<String, String> STATIC_PROPS = ImmutableMap.of(STATIC_PROP_1, reverse(STATIC_PROP_1), STATIC_PROP_2, reverse(STATIC_PROP_2));

    @Test
    void buildFromEmptyBundle_noProperties() {
        ClusterPropertyEntityBuilder builder = new ClusterPropertyEntityBuilder(ID_GENERATOR);
        final List<Entity> entities = builder.build(new Bundle(), DEPLOYMENT, DocumentTools.INSTANCE.getDocumentBuilder().newDocument());

        assertTrue(entities.isEmpty());
    }

    @Test
    void buildDeploymentBundleWithOnlyEnvProps() {
        ClusterPropertyEntityBuilder builder = new ClusterPropertyEntityBuilder(ID_GENERATOR);
        Bundle bundle = new Bundle();
        bundle.putAllEnvironmentProperties(ENV_PROPS);

        testDeploymentBundleWithOnlyMapping(
                builder,
                bundle,
                DocumentTools.INSTANCE.getDocumentBuilder().newDocument(),
                CLUSTER_PROPERTY_TYPE, ENV_PROPS.keySet().stream().map(k -> k.replace(PREFIX_GATEWAY, "")).collect(toList())
        );
    }

    @Test
    void buildDeploymentBundleWithStaticProps() {
        ClusterPropertyEntityBuilder builder = new ClusterPropertyEntityBuilder(ID_GENERATOR);
        Bundle bundle = new Bundle();
        bundle.putAllStaticProperties(STATIC_PROPS);

        final List<Entity> entities = builder.build(bundle, DEPLOYMENT, DocumentTools.INSTANCE.getDocumentBuilder().newDocument());
        assertExpectedProperties(entities, STATIC_PROPS);
    }

    @Test
    void buildDeploymentBundleWithMixedProps() {
        ClusterPropertyEntityBuilder builder = new ClusterPropertyEntityBuilder(ID_GENERATOR);
        Bundle bundle = new Bundle();
        bundle.putAllStaticProperties(STATIC_PROPS);
        bundle.putAllEnvironmentProperties(ENV_PROPS);

        final List<Entity> entities = builder.build(bundle, DEPLOYMENT, DocumentTools.INSTANCE.getDocumentBuilder().newDocument());
        assertExpectedProperties(entities, ImmutableMap.<String, String>builder().putAll(STATIC_PROPS).putAll(ENV_PROPS).build());
    }

    @Test
    void buildEnvironmentBundleWithProps_ignoringStatic() {
        ClusterPropertyEntityBuilder builder = new ClusterPropertyEntityBuilder(ID_GENERATOR);
        Bundle bundle = new Bundle();
        bundle.putAllStaticProperties(STATIC_PROPS);
        bundle.putAllEnvironmentProperties(ENV_PROPS);

        final List<Entity> entities = builder.build(bundle, ENVIRONMENT, DocumentTools.INSTANCE.getDocumentBuilder().newDocument());
        assertExpectedProperties(entities, ENV_PROPS);
    }

    @Test
    void buildDeploymentBundleWithDuplicateProps() {
        ClusterPropertyEntityBuilder builder = new ClusterPropertyEntityBuilder(ID_GENERATOR);
        Bundle bundle = new Bundle();
        bundle.putAllStaticProperties(STATIC_PROPS);
        //Duplicate prop envprop1 in both static and env props
        bundle.getStaticProperties().put("envprop1", "some value");
        bundle.putAllEnvironmentProperties(ENV_PROPS);

        assertThrows(EntityBuilderException.class, () -> builder.build(bundle, DEPLOYMENT, DocumentTools.INSTANCE.getDocumentBuilder().newDocument()));
    }

    private static void assertExpectedProperties(List<Entity> entities, Map<String, String> readOnlyExpectedProps) {
        Map<String, String> expectedProps = readOnlyExpectedProps.entrySet().stream().collect(toMap(o -> o.getKey().replace(PREFIX_GATEWAY, ""), Entry::getValue));
        assertFalse(entities.isEmpty());
        entities.forEach(e -> {
            assertNotNull(e.getId());
            assertEquals(CLUSTER_PROPERTY_TYPE, e.getType());
            if (e.getXml() == null) {
                assertOnlyMappingEntity(CLUSTER_PROPERTY_TYPE, new ArrayList<>(expectedProps.keySet()), e);
            } else {
                assertNotNull(expectedProps.get(e.getName()));

                Element xml = e.getXml();
                assertEquals(CLUSTER_PROPERTY, xml.getNodeName());
                assertNotNull(getSingleChildElement(xml, NAME));
                assertEquals(e.getName(), getSingleChildElementTextContent(xml, NAME));
                assertNotNull(getSingleChildElement(xml, VALUE));
                assertEquals(expectedProps.get(e.getName()), getSingleChildElementTextContent(xml, VALUE));
            }
            expectedProps.remove(e.getName());
        });
        assertTrue(expectedProps.isEmpty());
    }

}