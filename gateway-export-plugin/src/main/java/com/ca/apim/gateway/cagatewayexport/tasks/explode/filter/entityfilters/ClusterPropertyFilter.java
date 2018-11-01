package com.ca.apim.gateway.cagatewayexport.tasks.explode.filter.entityfilters;

import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.Bundle;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.ClusterProperty;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.filter.EntityFilter;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.filter.FilterConfiguration;
import com.ca.apim.gateway.cagatewayexport.util.gateway.DependencyUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.ClusterProperty.CLUSTER_HOSTNAME_PROPERTY_NAME;

@Singleton
public class ClusterPropertyFilter implements EntityFilter<ClusterProperty> {

    private static final Set<Class<? extends EntityFilter>> FILTER_DEPENDENCIES = Stream.of(
            EncassFilter.class,
            ScheduledTaskFilter.class,
            IdentityProviderFilter.class,
            PolicyBackedServiceFilter.class,
            PolicyFilter.class,
            ServiceFilter.class).collect(Collectors.toSet());
    private static final String ENTITY_NAME = "clusterProperties";

    @Override
    public @NotNull Collection<Class<? extends EntityFilter>> getDependencyEntityFilters() {
        return FILTER_DEPENDENCIES;
    }

    @Override
    public List<ClusterProperty> filter(String folderPath, FilterConfiguration filterConfiguration, Bundle bundle, Bundle filteredBundle) {
        Stream<ClusterProperty> clusterPropertiesStream = DependencyUtils.filterDependencies(ClusterProperty.class, bundle, filteredBundle, e -> filterConfiguration.getRequiredEntityNames(ENTITY_NAME).contains(e.getName())).stream();
        clusterPropertiesStream = clusterPropertiesStream.filter(e -> !CLUSTER_HOSTNAME_PROPERTY_NAME.equals(e.getName()));

        List<ClusterProperty> clusterProperties = clusterPropertiesStream.collect(Collectors.toList());
        DependencyUtils.validateEntitiesInList(clusterProperties, filterConfiguration.getRequiredEntityNames(ENTITY_NAME), "Cluster Propert(ies)");
        return clusterProperties;
    }

    @Override
    public String getFilterableEntityName() {
        return ENTITY_NAME;
    }
}
