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

import java.util.List;
import java.util.Map;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Thorben Lindhauer
 *
 */
public class XlsxDeploymentTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  protected String deployment;

  @After
  public void tearDown() {
    if (deployment != null) {
      rule.getRepositoryService().deleteDeployment(deployment, true);
    }
  }

  @Test
  public void testXlsxDeployment() {
    // when
    deployment = rule.getRepositoryService()
      .createDeployment()
      .addClasspathResource("test1.xlsx")
      .deploy()
      .getId();

    // then
    DecisionDefinition decisionDefinition = rule.getRepositoryService().createDecisionDefinitionQuery().singleResult();
    Assert.assertNotNull(decisionDefinition);

  }

  @Test
  @Deployment(resources = "test1.xlsx")
  public void testXlsxEvaluation() {
    // given
    DecisionDefinition decisionDefinition = rule.getRepositoryService().createDecisionDefinitionQuery().singleResult();

    // when
    DmnDecisionTableResult result = rule.getDecisionService().evaluateDecisionTableById(decisionDefinition.getId(),
        Variables.createVariables().putValue("input1", "foo").putValue("input2", 15));

    // then
    Assert.assertEquals(1, result.getResultList().size());
    Assert.assertEquals(1, result.getSingleResult().getEntryMap().size());
    Assert.assertEquals("foofoo", result.getSingleResult().getSingleEntry());
  }
}
