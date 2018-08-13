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
import java.util.Iterator;

import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.HitPolicy;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thorben Lindhauer
 *
 */
public class XslxToDmnConversionTest {

  public static final String DMN_11_NAMESPACE = "http://www.omg.org/spec/DMN/20151101/dmn.xsd";

  private static final String JAVASCRIPT_SNIPPET =
          "if (exp1 % 2 == 0)\n" +
                  "    {erg = 2;}\n" +
                  "else\n" +
                  "    {erg = 1;}\n" +
                  "erg;";

  // TODO: assert input entry text content

  @Test
  public void testSimpleConversion() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test1.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    Assert.assertNotNull(table);
    Assert.assertEquals(2, table.getInputs().size());
    Assert.assertEquals(1, table.getOutputs().size());
    Assert.assertEquals(4, table.getRules().size());
  }

  @Test
  public void testConversionOfMixedNumberAndStringColumns() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test2.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    Assert.assertNotNull(table);
    Assert.assertEquals(3, table.getInputs().size());
    Assert.assertEquals(1, table.getOutputs().size());
    Assert.assertEquals(4, table.getRules().size());
  }

  @Test
  public void testConversionOfEmptyCells() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test3.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    Assert.assertNotNull(table);
    Assert.assertEquals(3, table.getInputs().size());
    Assert.assertEquals(1, table.getOutputs().size());
    Assert.assertEquals(4, table.getRules().size());
  }

  @Test
  public void testDmnNamespace() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test1.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);

    Assert.assertEquals(DMN_11_NAMESPACE, dmnModelInstance.getDefinitions().getDomElement().getNamespaceURI());
  }

  @Test
  public void testConversionOfNullTitleOfParts() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test4.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);

    Assert.assertNotNull(table);
    Assert.assertEquals(2, table.getInputs().size());
    Assert.assertEquals(1, table.getOutputs().size());
    Assert.assertEquals(1, table.getRules().size());
  }

  @Test
  public void testConversionWithRanges() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test5.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    Assert.assertNotNull(table);
    Assert.assertEquals(1, table.getInputs().size());
    Assert.assertEquals(1, table.getOutputs().size());
    Assert.assertEquals(4, table.getRules().size());

    Rule firstRule = table.getRules().iterator().next();

    InputEntry inputEntry = firstRule.getInputEntries().iterator().next();
    String firstInput = inputEntry.getTextContent();
    Assert.assertEquals("[1..2]", firstInput);
  }

  @Test
  public void testConversionWithComplexHeaders() {
    XlsxConverter converter = new XlsxConverter();
    converter.setIoDetectionStrategy(new AdvancedInputOutputDetectionStrategy());
    InputStream inputStream = TestHelper.getClassPathResource("test6.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    Assert.assertNotNull(dmnModelInstance);

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    Assert.assertNotNull(table);
    Assert.assertEquals(2, table.getInputs().size());
    Assert.assertEquals(2, table.getOutputs().size());
    Assert.assertEquals(2, table.getRules().size());
    Assert.assertEquals(HitPolicy.FIRST, table.getHitPolicy());

    Iterator<Input> inputIterator = table.getInputs().iterator();
    Input input = inputIterator.next();
    Assert.assertEquals("input1", input.getId());
    Assert.assertEquals("InputLabel1", input.getLabel());
    Assert.assertEquals("string", input.getInputExpression().getTypeRef());
    Assert.assertEquals("Exp1", input.getTextContent());

    input = inputIterator.next();
    Assert.assertEquals("input2", input.getId());
    Assert.assertEquals("InputLabel2", input.getLabel());
    Assert.assertEquals("integer", input.getInputExpression().getTypeRef());
    Assert.assertEquals("javascript", input.getInputExpression().getExpressionLanguage());
    Assert.assertEquals(JAVASCRIPT_SNIPPET, input.getInputExpression().getTextContent());

    Iterator<Rule> ruleIterator = table.getRules().iterator();
    Rule rule = ruleIterator.next();
    Assert.assertEquals("Comment1", rule.getDescription().getTextContent());

    InputEntry inputEntry = rule.getInputEntries().iterator().next();
    String firstInput = inputEntry.getTextContent();
    Assert.assertEquals("\"Foo\"", firstInput);

    rule = ruleIterator.next();
    Assert.assertEquals("Another Comment", rule.getDescription().getTextContent());
  }
}
