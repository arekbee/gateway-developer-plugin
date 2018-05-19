/*
 * Copyright (c) 2018 CA. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayexport.tasks.explode.linker;

import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.Bundle;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.Folder;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.ServiceEntity;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.writer.WriteException;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.writer.WriterHelper;
import com.ca.apim.gateway.cagatewayexport.util.policy.PolicyXMLSimplifier;
import com.ca.apim.gateway.cagatewayexport.util.xml.DocumentParseException;
import com.ca.apim.gateway.cagatewayexport.util.xml.DocumentTools;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServiceLinker implements EntityLinker<ServiceEntity> {
    private final DocumentTools documentTools;
    private final PolicyXMLSimplifier policyXMLSimplifier;

    ServiceLinker(DocumentTools documentTools) {
        this.documentTools = documentTools;
        this.policyXMLSimplifier = PolicyXMLSimplifier.INSTANCE;
    }

    @Override
    public Class<ServiceEntity> getEntityClass() {
        return ServiceEntity.class;
    }

    @Override
    public void link(ServiceEntity service, Bundle bundle) {
        try {
            service.setPolicyXML(policyXMLSimplifier.simplifyPolicyXML(WriterHelper.stringToXML(documentTools, service.getPolicy()), bundle));
        } catch (DocumentParseException e) {
            throw new WriteException("Exception linking and simplifying policy: " + service.getName() + " Message: " + e.getMessage(), e);
        }
        service.setPath(getServicePath(bundle, service));
    }

    private String getServicePath(Bundle bundle, ServiceEntity serviceEntity) {
        Folder folder = bundle.getFolderTree().getFolderById(serviceEntity.getFolderId());
        Path folderPath = bundle.getFolderTree().getPath(folder);
        return Paths.get(folderPath.toString(), serviceEntity.getName() + ".xml").toString();
    }
}
