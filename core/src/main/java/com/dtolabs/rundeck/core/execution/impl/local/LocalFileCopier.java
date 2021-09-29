/*
 * Copyright 2016 SimplifyOps, Inc. (http://simplifyops.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
* LocalFileCopier.java
* 
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: 3/21/11 5:43 PM
* 
*/
package com.dtolabs.rundeck.core.execution.impl.local;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.ExecutionContext;
import com.dtolabs.rundeck.core.execution.impl.common.BaseFileCopier;
import com.dtolabs.rundeck.core.execution.service.FileCopier;
import com.dtolabs.rundeck.core.execution.service.FileCopierException;
import com.dtolabs.rundeck.core.execution.service.MultiFileCopier;
import com.dtolabs.rundeck.core.execution.service.NodeExecutorResultImpl;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepFailureReason;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * LocalFileCopier is ...
 *
 * @author Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
 */
public class LocalFileCopier extends BaseFileCopier implements FileCopier {
    public static final String SERVICE_PROVIDER_TYPE = "local";
    public static final String DISABLE_LOCAL_EXECUTOR_ENV = "RUNDECK_DISABLED_LOCAL_EXECUTOR";
    private boolean disableLocalExecutor = false;

    public LocalFileCopier(Framework framework) {
        this.framework = framework;

        String disableLocalExecutor = System.getenv(DISABLE_LOCAL_EXECUTOR_ENV);

        if(disableLocalExecutor!=null && disableLocalExecutor.equals("true")){
            this.disableLocalExecutor=true;
        }

    }

    public void setDisableLocalExecutor(boolean disableLocalExecutor) {
        this.disableLocalExecutor = disableLocalExecutor;
    }

    private Framework framework;

    private String copyFile(
            final ExecutionContext context,
            File scriptfile,
            InputStream input,
            String script,
            INodeEntry node,
            final String destination
    ) throws FileCopierException
    {

        if(disableLocalExecutor){
            throw new FileCopierException("Local Executor is disabled", StepFailureReason.ConfigurationFailure);
        }

        return BaseFileCopier.writeLocalFile(
                scriptfile,
                input,
                script,
                null != destination ? new File(destination) : null
        ).getAbsolutePath();
    }

    public String copyFileStream(ExecutionContext context, InputStream input, INodeEntry node, String destination) throws FileCopierException {
        return copyFile(context, null, input, null, node, destination);
    }

    public String copyFile(ExecutionContext context, File file, INodeEntry node, String destination) throws FileCopierException {
        return copyFile(context, file, null, null, node, destination);
    }

    public String copyScriptContent(ExecutionContext context, String script, INodeEntry node, String destination) throws FileCopierException {
        return copyFile(context, null, null, script, node, destination);
    }
}
