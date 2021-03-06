package com.ca.apim.gateway.cagatewayexport.tasks.explode.filter.entityfilters;

import com.ca.apim.gateway.cagatewayconfig.beans.Bundle;
import com.ca.apim.gateway.cagatewayconfig.beans.Dependency;
import com.ca.apim.gateway.cagatewayconfig.beans.IdentityProvider;
import com.ca.apim.gateway.cagatewayconfig.beans.Policy;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.filter.EntityFilterException;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.filter.FilterConfiguration;
import com.ca.apim.gateway.cagatewayexport.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdentityProviderFilterTest {
    @Test
    void filterNoEntities() {
        IdentityProviderFilter filter = new IdentityProviderFilter();

        Bundle filteredBundle = new Bundle();
        Bundle bundle = FilterTestUtils.getBundle();
        bundle.setDependencyMap(Collections.emptyMap());

        List<IdentityProvider> filteredEntities = filter.filter("/my/folder/path", new FilterConfiguration(), bundle, filteredBundle);

        assertEquals(0, filteredEntities.size());
    }

    @Test
    void filter() {
        IdentityProviderFilter filter = new IdentityProviderFilter();

        Bundle filteredBundle = new Bundle();
        filteredBundle.addEntity(TestUtils.createPolicy("my-policy", "1", "", "", null, ""));
        Bundle bundle = FilterTestUtils.getBundle();
        bundle.setDependencyMap(
                ImmutableMap.of(
                        new Dependency("1", Policy.class), Arrays.asList(new Dependency(IdentityProvider.INTERNAL_IDP_ID, IdentityProvider.class), new Dependency("3", IdentityProvider.class)),
                        new Dependency("2", Policy.class), Collections.singletonList(new Dependency("4", IdentityProvider.class))));
        bundle.addEntity(new IdentityProvider.Builder().name("idp1").id("1").build());
        bundle.addEntity(new IdentityProvider.Builder().name("idp2").id(IdentityProvider.INTERNAL_IDP_ID).build());
        bundle.addEntity(new IdentityProvider.Builder().name("idp3").id("3").build());
        bundle.addEntity(new IdentityProvider.Builder().name("idp4").id("4").build());


        FilterConfiguration filterConfiguration = new FilterConfiguration();
        List<IdentityProvider> filteredEntities = filter.filter("/my/folder/path", filterConfiguration, bundle, filteredBundle);

        assertEquals(1, filteredEntities.size());
        assertTrue(filteredEntities.stream().anyMatch(c -> "idp3".equals(c.getName())));

        filterConfiguration.getEntityFilters().put(filter.getFilterableEntityName(), new HashSet<>());
        filterConfiguration.getEntityFilters().get(filter.getFilterableEntityName()).add("idp4");
        filterConfiguration.getEntityFilters().get(filter.getFilterableEntityName()).add("idp1");
        filteredEntities = filter.filter("/my/folder/path", filterConfiguration, bundle, filteredBundle);

        assertEquals(3, filteredEntities.size());
        assertTrue(filteredEntities.stream().anyMatch(c -> "idp3".equals(c.getName())));
        assertTrue(filteredEntities.stream().anyMatch(c -> "idp1".equals(c.getName())));
        assertTrue(filteredEntities.stream().anyMatch(c -> "idp4".equals(c.getName())));

        filterConfiguration.getEntityFilters().get(filter.getFilterableEntityName()).add("non-existing-entity");
        EntityFilterException entityFilterException = assertThrows(EntityFilterException.class, () -> filter.filter("/my/folder/path", filterConfiguration, bundle, filteredBundle));
        assertTrue(entityFilterException.getMessage().contains("non-existing-entity"));
    }
}