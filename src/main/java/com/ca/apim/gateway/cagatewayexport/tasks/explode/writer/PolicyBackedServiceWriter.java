/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayexport.tasks.explode.writer;

import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.Bundle;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.Folder;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.PolicyBackedServiceEntity;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.PolicyEntity;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.writer.beans.PolicyBackedService;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.writer.beans.PolicyBackedServiceOperation;
import com.ca.apim.gateway.cagatewayexport.util.file.DocumentFileUtils;
import com.ca.apim.gateway.cagatewayexport.util.json.JsonTools;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class PolicyBackedServiceWriter implements EntityWriter {
    private final DocumentFileUtils documentFileUtils;
    private final JsonTools jsonTools;

    public PolicyBackedServiceWriter(DocumentFileUtils documentFileUtils, JsonTools jsonTools) {
        this.documentFileUtils = documentFileUtils;
        this.jsonTools = jsonTools;
    }

    @Override
    public void write(Bundle bundle, File rootFolder) {
        File configFolder = new File(rootFolder, "config");
        documentFileUtils.createFolder(configFolder.toPath());

        Map<String, PolicyBackedServiceEntity> policyBackedServiceEntityMap = bundle.getEntities(PolicyBackedServiceEntity.class);

        Map<String, PolicyBackedService> policyBackedServiceBeans = policyBackedServiceEntityMap.values().stream().collect(Collectors.toMap(PolicyBackedServiceEntity::getName, policyBackedServiceEntity -> getPolicyBackedServiceBean(bundle, policyBackedServiceEntity)));

        File servicesFile = new File(configFolder, "policy-backed-services.yml");

        ObjectWriter yamlWriter = jsonTools.getObjectWriter(JsonTools.YAML);
        try (OutputStream fileStream = Files.newOutputStream(servicesFile.toPath())) {
            yamlWriter.writeValue(fileStream, policyBackedServiceBeans);
        } catch (IOException e) {
            throw new WriteException("Exception writing policy backed services config file", e);
        }
    }

    private String getPolicyPath(Bundle bundle, String policyId) {
        PolicyEntity policy = bundle.getEntities(PolicyEntity.class).get(policyId);
        Folder folder = bundle.getFolderTree().getFolderById(policy.getFolderId());
        Path folderPath = bundle.getFolderTree().getPath(folder);
        return Paths.get(folderPath.toString(), policy.getName() + ".xml").toString();
    }

    private PolicyBackedService getPolicyBackedServiceBean(Bundle bundle, PolicyBackedServiceEntity policyBackedServiceEntity) {
        PolicyBackedService policyBackedServiceBean = new PolicyBackedService();
        policyBackedServiceBean.setInterfaceName(policyBackedServiceEntity.getInterfaceName());
        policyBackedServiceBean.setOperations(policyBackedServiceEntity.getOperations().entrySet().stream().map(e -> new PolicyBackedServiceOperation(e.getKey(), getPolicyPath(bundle, e.getValue()))).collect(Collectors.toList()));
        return policyBackedServiceBean;
    }
}
