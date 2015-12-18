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

import java.util.List;

import org.camunda.bpm.dmn.xlsx.elements.IndexedCell;
import org.camunda.bpm.dmn.xlsx.elements.IndexedDmnColumns;
import org.camunda.bpm.dmn.xlsx.elements.IndexedRow;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.impl.DmnModelConstants;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.DmnElement;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.NamedElement;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;

/**
 * @author Thorben Lindhauer
 *
 */
public class XlsxWorksheetConverter {

  protected XlsxWorksheetContext worksheetContext;
  protected DmnConversionContext dmnConversionContext;

  public XlsxWorksheetConverter(XlsxWorksheetContext worksheetContext) {
    this.worksheetContext = worksheetContext;
    this.dmnConversionContext = new DmnConversionContext(worksheetContext);

    // order is important
    this.dmnConversionContext.addCellContentHandler(new FeelSimpleUnaryTestConverter());
    this.dmnConversionContext.addCellContentHandler(new DmnValueStringConverter());
    this.dmnConversionContext.addCellContentHandler(new DmnValueNumberConverter());
  }

  public DmnModelInstance convert() {

    DmnModelInstance dmnModel = initializeEmptyDmnModel();

    Decision decision = generateNamedElement(dmnModel, Decision.class, "decision");
    dmnModel.getDefinitions().addChildElement(decision);

    DecisionTable decisionTable = generateElement(dmnModel, DecisionTable.class, "decisionTable");
    decision.addChildElement(decisionTable);

    List<IndexedRow> rows = worksheetContext.getRows();

    convertInputsOutputs(dmnModel, decisionTable, rows.get(0));
    convertRules(dmnModel, decisionTable, rows.subList(1, rows.size()));

    return dmnModel;
  }

  protected void convertInputsOutputs(DmnModelInstance dmnModel, DecisionTable decisionTable, IndexedRow header) {
    // TODO: initial simple implementation: last entry is output, all others are inputs
    if (!header.hasCells()) {
      throw new RuntimeException("A dmn table requires at least one output; the header row contains no entries");
    }

    List<IndexedCell> cells = header.getCells();

    // inputs
    for (int i = 0; i < cells.size() - 1; i++) {
      IndexedCell inputCell = cells.get(i);
      Input input = generateElement(dmnModel, Input.class, worksheetContext.resolveCellValue(inputCell.getCell()));
      decisionTable.addChildElement(input);

      InputExpression inputExpression = generateElement(dmnModel, InputExpression.class);
      Text text = generateText(dmnModel, worksheetContext.resolveCellValue(inputCell.getCell()));
      inputExpression.setText(text);
      input.setInputExpression(inputExpression);

      dmnConversionContext.getIndexedDmnColumns().addInput(inputCell, input);
    }

    // output
    IndexedCell outputCell = cells.get(cells.size() - 1);
    Output output = generateElement(dmnModel, Output.class, worksheetContext.resolveCellValue(outputCell.getCell()));
    output.setName(worksheetContext.resolveCellValue(outputCell.getCell()));
    decisionTable.addChildElement(output);

    dmnConversionContext.getIndexedDmnColumns().addOutput(outputCell, output);
  }

  protected void convertRules(DmnModelInstance dmnModel, DecisionTable decisionTable, List<IndexedRow> rulesRows) {
    for (IndexedRow rule : rulesRows) {
      convertRule(dmnModel, decisionTable, rule);
    }
  }

  protected void convertRule(DmnModelInstance dmnModel, DecisionTable decisionTable, IndexedRow ruleRow) {
    Rule rule = generateElement(dmnModel, Rule.class, "excelRow" + ruleRow.getRow().getR());
    decisionTable.addChildElement(rule);

    IndexedDmnColumns dmnColumns = dmnConversionContext.getIndexedDmnColumns();

    for (Input input : dmnColumns.getOrderedInputs()) {
      String xlsxColumn = dmnColumns.getXlsxColumn(input);
      IndexedCell cell = ruleRow.getCell(xlsxColumn);
      String coordinate = xlsxColumn + ruleRow.getRow().getR();

      InputEntry inputEntry = generateElement(dmnModel, InputEntry.class, coordinate);
      String textValue = cell != null ? dmnConversionContext.resolveCellValue(cell.getCell()) : getDefaultCellContent();
      Text text = generateText(dmnModel, textValue);
      inputEntry.setText(text);
      rule.addChildElement(inputEntry);
    }

    for (Output output : dmnColumns.getOrderedOutputs()) {
      String xlsxColumn = dmnColumns.getXlsxColumn(output);
      IndexedCell cell = ruleRow.getCell(xlsxColumn);
      String coordinate = xlsxColumn + ruleRow.getRow().getR();

      OutputEntry outputEntry = generateElement(dmnModel, OutputEntry.class, coordinate);
      String textValue = cell != null ? dmnConversionContext.resolveCellValue(cell.getCell()) : getDefaultCellContent();
      Text text = generateText(dmnModel, textValue);
      outputEntry.setText(text);
      rule.addChildElement(outputEntry);
    }
  }

  protected String getDefaultCellContent() {
    return "-";
  }

  protected DmnModelInstance initializeEmptyDmnModel() {
    DmnModelInstance dmnModel = Dmn.createEmptyModel();
    Definitions definitions = generateNamedElement(dmnModel, Definitions.class, "definitions");
    definitions.setNamespace(DmnModelConstants.CAMUNDA_NS);
    dmnModel.setDefinitions(definitions);

    return dmnModel;
  }

  public <E extends NamedElement> E generateNamedElement(DmnModelInstance modelInstance, Class<E> elementClass, String name) {
    E element = generateElement(modelInstance, elementClass, name);
    element.setName(name);
    return element;
  }

  public <E extends DmnElement> E generateElement(DmnModelInstance modelInstance, Class<E> elementClass, String id) {
    E element = modelInstance.newInstance(elementClass);
    element.setId(id);
    return element;
  }

  /**
   * With a generated id
   */
  public <E extends DmnElement> E generateElement(DmnModelInstance modelInstance, Class<E> elementClass) {
    // TODO: use a proper generator for random IDs
    String generatedId = elementClass.getSimpleName() + Integer.toString((int) (Integer.MAX_VALUE * Math.random()));
    return generateElement(modelInstance, elementClass, generatedId);
  }

  protected Text generateText(DmnModelInstance dmnModel, String content) {
    Text text = dmnModel.newInstance(Text.class);
    text.setTextContent(content);
    return text;
  }
}
