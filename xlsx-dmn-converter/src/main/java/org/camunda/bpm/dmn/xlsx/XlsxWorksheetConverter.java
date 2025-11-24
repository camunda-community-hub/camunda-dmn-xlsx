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

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.dmn.xlsx.api.SpreadsheetAdapter;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetRow;
import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;
import org.camunda.bpm.dmn.xlsx.elements.IndexedDmnColumns;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.HitPolicy;
import org.camunda.bpm.model.dmn.impl.DmnModelConstants;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.Description;
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
 */
public class XlsxWorksheetConverter {

  static
  {
    CellContentHandler.DEFAULT_HANDLERS.add(new DmnValueRangeConverter());
    CellContentHandler.DEFAULT_HANDLERS.add(new FeelSimpleUnaryTestConverter());
    CellContentHandler.DEFAULT_HANDLERS.add(new DmnValueStringConverter());
    CellContentHandler.DEFAULT_HANDLERS.add(new DmnValueNumberConverter());
  }

  protected final String historyTimeToLive;

  protected XlsxWorksheetContext worksheetContext;
  protected DmnConversionContext dmnConversionContext;
  protected SpreadsheetAdapter spreadsheetAdapter;
  
  protected int idCounter = 0;

  public XlsxWorksheetConverter(XlsxWorksheetContext worksheetContext, SpreadsheetAdapter spreadsheetAdapter, String historyTimeToLive) {
    this.worksheetContext = worksheetContext;
    this.dmnConversionContext = new DmnConversionContext(worksheetContext, spreadsheetAdapter.getCellContentHandlers(worksheetContext));
    this.spreadsheetAdapter = spreadsheetAdapter;
    this.historyTimeToLive = historyTimeToLive;
  }

  public DmnModelInstance convert() {

    DmnModelInstance dmnModel = initializeEmptyDmnModel();

    Decision decision = generateElement(dmnModel, Decision.class, worksheetContext.getName());
    decision.setName(spreadsheetAdapter.determineDecisionName(worksheetContext));
    decision.setCamundaHistoryTimeToLiveString(historyTimeToLive);
    dmnModel.getDefinitions().addChildElement(decision);

    DecisionTable decisionTable = generateElement(dmnModel, DecisionTable.class, "decisionTable");
    decision.addChildElement(decisionTable);

    setHitPolicy(decisionTable);
    convertInputsOutputs(dmnModel, decisionTable);
    convertRulesBatch(dmnModel, decisionTable, spreadsheetAdapter.determineRuleRows(worksheetContext));

    return dmnModel;
  }

  protected void setHitPolicy(DecisionTable decisionTable) {
    HitPolicy hitPolicy = spreadsheetAdapter.determineHitPolicy(worksheetContext);
    if (hitPolicy != null) {
      decisionTable.setHitPolicy(hitPolicy);
    }
  }

  protected void convertInputsOutputs(DmnModelInstance dmnModel, DecisionTable decisionTable) {

    InputOutputColumns inputOutputColumns = spreadsheetAdapter.determineInputOutputs(worksheetContext);

    // inputs
    for (HeaderValuesContainer hvc : inputOutputColumns.getInputHeaders()) {
      Input input = generateElement(dmnModel, Input.class, hvc.getId());
      decisionTable.addChildElement(input);

      // mandatory
      InputExpression inputExpression = generateElement(dmnModel, InputExpression.class);
      Text text = generateText(dmnModel, hvc.getText());
      inputExpression.setText(text);
      input.setInputExpression(inputExpression);

      // optionals
      if (hvc.getLabel() != null) {
        input.setLabel(hvc.getLabel());
      }
      if (hvc.getTypeRef() != null) {
        inputExpression.setTypeRef(hvc.getTypeRef());
      }
      if (hvc.getExpressionLanguage() != null) {
        inputExpression.setExpressionLanguage(hvc.getExpressionLanguage());
      }

      dmnConversionContext.getIndexedDmnColumns().addInput(hvc.getColumn(), input);
    }

    // outputs
    for (HeaderValuesContainer hvc : inputOutputColumns.getOutputHeaders()) {
      Output output = generateElement(dmnModel, Output.class, hvc.getId());
      decisionTable.addChildElement(output);

      // mandatory
      output.setName(hvc.getText());

      // optionals
      if (hvc.getLabel() != null) {
        output.setLabel(hvc.getLabel());
      }
      if (hvc.getTypeRef() != null) {
        output.setTypeRef(hvc.getTypeRef());
      }

      dmnConversionContext.getIndexedDmnColumns().addOutput(hvc.getColumn(), output);
    }

  }

  /**
   * Optimized batch conversion that creates all rules first, then adds them to the decision table
   * This avoids O(n²) performance when adding many child elements
   */
  protected void convertRulesBatch(DmnModelInstance dmnModel, DecisionTable decisionTable, List<SpreadsheetRow> rulesRows) {
    // Pre-fetch column mappings once
    IndexedDmnColumns dmnColumns = dmnConversionContext.getIndexedDmnColumns();
    List<Input> orderedInputs = dmnColumns.getOrderedInputs();
    List<Output> orderedOutputs = dmnColumns.getOrderedOutputs();
    int numInputs = orderedInputs.size();
    int numOutputs = orderedOutputs.size();
    
    // Build all rules first (without adding to decision table)
    List<Rule> rules = new ArrayList<>(rulesRows.size());
    
    for (SpreadsheetRow ruleRow : rulesRows) {
      Rule rule = buildRule(dmnModel, ruleRow, orderedInputs, orderedOutputs, numInputs, numOutputs);
      rules.add(rule);
    }
    
    // Now add all rules to decision table in batch
    for (Rule rule : rules) {
      decisionTable.addChildElement(rule);
    }
  }

  /**
   * Build a single rule without adding it to the decision table
   */
  protected Rule buildRule(DmnModelInstance dmnModel, SpreadsheetRow ruleRow, 
                          List<Input> orderedInputs, List<Output> orderedOutputs,
                          int numInputs, int numOutputs) {
    
    Rule rule = generateElement(dmnModel, Rule.class, "excelRow" + ruleRow.getRaw().getR());
    IndexedDmnColumns dmnColumns = dmnConversionContext.getIndexedDmnColumns();
    String defaultContent = getDefaultCellContent();
    long rowNumber = ruleRow.getRaw().getR();

    // Process inputs
    for (int i = 0; i < numInputs; i++) {
      Input input = orderedInputs.get(i);
      String xlsxColumn = dmnColumns.getSpreadsheetColumn(input);
      SpreadsheetCell cell = ruleRow.getCell(xlsxColumn);
      String coordinate = xlsxColumn + rowNumber;

      InputEntry inputEntry = generateElement(dmnModel, InputEntry.class, coordinate);
      String textValue = cell != null ? dmnConversionContext.resolveCellValue(cell) : defaultContent;
      Text text = generateText(dmnModel, textValue);
      inputEntry.setText(text);
      rule.addChildElement(inputEntry);
    }

    // Process outputs
    for (int i = 0; i < numOutputs; i++) {
      Output output = orderedOutputs.get(i);
      String xlsxColumn = dmnColumns.getSpreadsheetColumn(output);
      SpreadsheetCell cell = ruleRow.getCell(xlsxColumn);
      String coordinate = xlsxColumn + rowNumber;

      OutputEntry outputEntry = generateElement(dmnModel, OutputEntry.class, coordinate);
      String textValue = cell != null ? dmnConversionContext.resolveCellValue(cell) : defaultContent;
      Text text = generateText(dmnModel, textValue);
      outputEntry.setText(text);
      rule.addChildElement(outputEntry);
    }

    // Add description
    List<SpreadsheetCell> cells = ruleRow.getCells();
    SpreadsheetCell annotationCell = cells.get(cells.size() - 1);
    Description description = generateDescription(dmnModel, worksheetContext.resolveCellContent(annotationCell));
    rule.setDescription(description);

    return rule;
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
   * With a generated id - using counter instead of random for better performance
   */
  public <E extends DmnElement> E generateElement(DmnModelInstance modelInstance, Class<E> elementClass) {
    String generatedId = elementClass.getSimpleName() + (idCounter++);
    return generateElement(modelInstance, elementClass, generatedId);
  }

  protected Text generateText(DmnModelInstance dmnModel, String content) {
    Text text = dmnModel.newInstance(Text.class);
    text.setTextContent(content);
    return text;
  }

  protected Description generateDescription(DmnModelInstance dmnModel, String content) {
    Description description = dmnModel.newInstance(Description.class);
    description.setTextContent(content);
    return description;
  }
}
