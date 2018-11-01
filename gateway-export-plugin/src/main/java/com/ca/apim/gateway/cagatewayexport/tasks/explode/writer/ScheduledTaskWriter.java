/*
 * Copyright (c) 2018. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.ca.apim.gateway.cagatewayexport.tasks.explode.writer;

import com.ca.apim.gateway.cagatewayconfig.tasks.zip.beans.ScheduledTask;
import com.ca.apim.gateway.cagatewayconfig.util.file.DocumentFileUtils;
import com.ca.apim.gateway.cagatewayconfig.util.json.JsonTools;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.Bundle;
import com.ca.apim.gateway.cagatewayexport.tasks.explode.bundle.entity.ScheduledTaskEntity;
import com.google.common.annotations.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ca.apim.gateway.cagatewayexport.tasks.explode.writer.WriterHelper.writeFile;

@Singleton
public class ScheduledTaskWriter implements EntityWriter {
    private static final String SCHEDULED_TASKS_FILE = "scheduled-tasks";
    private final DocumentFileUtils documentFileUtils;
    private final JsonTools jsonTools;

    @Inject
    ScheduledTaskWriter(DocumentFileUtils documentFileUtils, JsonTools jsonTools) {
        this.documentFileUtils = documentFileUtils;
        this.jsonTools = jsonTools;
    }

    @Override
    public void write(Bundle bundle, File rootFolder) {
        Map<String, ScheduledTask> scheduledTaskBeans = bundle.getEntities(ScheduledTaskEntity.class)
                .values()
                .stream()
                .collect(Collectors.toMap(ScheduledTaskEntity::getName, this::getScheduledTaskBean));

        writeFile(rootFolder, documentFileUtils, jsonTools, scheduledTaskBeans, SCHEDULED_TASKS_FILE, ScheduledTask.class);
    }

    @VisibleForTesting
    ScheduledTask getScheduledTaskBean(ScheduledTaskEntity scheduledTaskEntity) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.setPolicy(scheduledTaskEntity.getPolicyPath());
        scheduledTask.setOneNode(scheduledTaskEntity.isOneNode());
        scheduledTask.setJobType(scheduledTaskEntity.getJobType());
        scheduledTask.setJobStatus(scheduledTaskEntity.getJobStatus());
        scheduledTask.setExecutionDate(scheduledTaskEntity.getExecutionDate());
        scheduledTask.setCronExpression(scheduledTaskEntity.getCronExpression());
        scheduledTask.setShouldExecuteOnCreate(scheduledTaskEntity.isShouldExecuteOnCreate());
        scheduledTask.setProperties(scheduledTaskEntity.getProperties());
        return scheduledTask;
    }
}
