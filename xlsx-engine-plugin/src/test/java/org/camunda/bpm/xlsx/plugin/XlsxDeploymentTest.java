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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Thorben Lindhauer
 *
 */
@ExtendWith(ProcessEngineExtension.class)
public class XlsxDeploymentTest {

  public ProcessEngine processEngine;
  protected String deployment;

  @AfterEach
  public void tearDown() {
    if (deployment != null) {
      processEngine.getRepositoryService().deleteDeployment(deployment, true);
    }
  }

  @Test
  public void testXlsxDeployment() {
    // when
    deployment = processEngine.getRepositoryService()
      .createDeployment()
      .addClasspathResource("test1.xlsx")
      .deploy()
      .getId();

    // then
    DecisionDefinition decisionDefinition = processEngine.getRepositoryService().createDecisionDefinitionQuery().singleResult();
    assertThat(decisionDefinition).isNotNull();
  }

  @Test
  public void testXlsxDeploymentWithMetaData() {
    // when
    deployment = processEngine.getRepositoryService()
      .createDeployment()
      .addClasspathResource("test1.xlsx")
      .addClasspathResource("test1.xlsx.yaml")
      .deploy()
      .getId();

    // then
    DecisionDefinition decisionDefinition = processEngine.getRepositoryService().createDecisionDefinitionQuery().singleResult();
    assertThat(decisionDefinition).isNotNull();

    DmnModelInstance dmnModel = processEngine.getRepositoryService().getDmnModelInstance(decisionDefinition.getId());
    Collection<DecisionTable> decisionTables = dmnModel.getModelElementsByType(DecisionTable.class);
    assertThat(decisionTables).hasSize(1);

    DecisionTable decisionTable = decisionTables.iterator().next();
    assertThat(decisionTable.getInputs()).hasSize(1);
    assertThat(decisionTable.getOutputs()).hasSize(1);

  }

  @Test
  @Deployment(resources = "test1.xlsx")
  public void testXlsxEvaluation() {
    // given
    DecisionDefinition decisionDefinition = processEngine.getRepositoryService().createDecisionDefinitionQuery().singleResult();

    // when
    DmnDecisionTableResult result = processEngine.getDecisionService().evaluateDecisionTableById(decisionDefinition.getId(),
        Variables.createVariables().putValue("input1", "foo").putValue("input2", 15));

    // then
    assertThat(result.getResultList()).hasSize(1);
    assertThat(result.getFirstResult().getEntryMap()).hasSize(1);
    assertThat((String)result.getFirstResult().getSingleEntry()).isEqualTo("foofoo");
  }
}
