/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.xlsx.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.camunda.bpm.dmn.xlsx.XlsxConverter;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.deploy.Deployer;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;

/**
 * Converts an xlsx file to a dmn file on the fly and deploys the dmn table
 *
 * @author Thorben Lindhauer
 */
public class XlsxDeployer implements Deployer {

  public static final String XLSX_RESOURCE_SUFFIX = "xlsx";

  public void deploy(DeploymentEntity deployment) {
    for (ResourceEntity resource : deployment.getResources().values()) {
      if (canHandle(resource)) {
        DmnModelInstance dmnModel = generateDmnModel(resource);
        addAsDeploymentResource(deployment, dmnModel, generateDmnResourceName(resource));
      }
    }
  }

  protected void addAsDeploymentResource(DeploymentEntity deployment, DmnModelInstance dmnModel, String name) {
    ResourceEntity resource = new ResourceEntity();

    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    Dmn.writeModelToStream(byteStream, dmnModel);
    byte[] bytes = byteStream.toByteArray();

    resource.setName(name);
    resource.setBytes(bytes);
    resource.setDeploymentId(deployment.getId());

    // Mark the resource as 'generated'
    resource.setGenerated(true);

    Context.getCommandContext().getResourceManager().insert(resource);

    deployment.addResource(resource);
  }

  protected String generateDmnResourceName(ResourceEntity xlsxResource) {
    return xlsxResource.getName() + ".dmn";
  }

  protected boolean canHandle(ResourceEntity resource) {
    return resource.getName().endsWith("." + XLSX_RESOURCE_SUFFIX);
  }

  protected DmnModelInstance generateDmnModel(ResourceEntity xlsxResource) {
    byte[] bytes = xlsxResource.getBytes();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

    XlsxConverter converter = new XlsxConverter();
    return converter.convert(inputStream);
  }

}
