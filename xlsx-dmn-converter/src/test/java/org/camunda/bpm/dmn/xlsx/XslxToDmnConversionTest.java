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
package org.camunda.bpm.dmn.xlsx;

import java.io.InputStream;
import java.util.Collection;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thorben Lindhauer
 *
 */
public class XslxToDmnConversionTest {

  // TODO: assert input entry text content

  @Test
  public void testSimpleConversion() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = getClassPathResource("test1.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = assertAndGetSingleDecisionTable(dmnModelInstance);
    Assert.assertNotNull(table);
    Assert.assertEquals(2, table.getInputs().size());
    Assert.assertEquals(1, table.getOutputs().size());
    Assert.assertEquals(4, table.getRules().size());
  }

  @Test
  public void testConversionOfMixedNumberAndStringColumns() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = getClassPathResource("test2.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = assertAndGetSingleDecisionTable(dmnModelInstance);
    Assert.assertNotNull(table);
    Assert.assertEquals(3, table.getInputs().size());
    Assert.assertEquals(1, table.getOutputs().size());
    Assert.assertEquals(4, table.getRules().size());
  }

  protected DecisionTable assertAndGetSingleDecisionTable(DmnModelInstance dmnModel) {
    Assert.assertNotNull(dmnModel.getDefinitions());
    Collection<Decision> decisions = dmnModel.getDefinitions().getChildElementsByType(Decision.class);
    Assert.assertEquals(1, decisions.size());

    Decision decision = decisions.iterator().next();
    Assert.assertNotNull(decision);

    Collection<DecisionTable> decisionTables = decision.getChildElementsByType(DecisionTable.class);
    Assert.assertEquals(1, decisionTables.size());

    return decisionTables.iterator().next();
  }

  // TODO: test conversion where file has numeric input

  // TODO: test conversion where an intermediary cell is empty

  protected InputStream getClassPathResource(String path) {
    return getClass().getClassLoader().getResourceAsStream(path);
  }

}
