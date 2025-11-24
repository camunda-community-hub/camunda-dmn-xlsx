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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.camunda.bpm.dmn.xlsx.api.Spreadsheet;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetRow;
import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;
import org.camunda.bpm.model.dmn.HitPolicy;

public class AdvancedSpreadsheetAdapter extends BaseAdapter {

  public InputOutputColumns determineInputOutputs(Spreadsheet context) {
    Set<String> inputColumns = new LinkedHashSet<>();
    Set<String> outputColumns = new LinkedHashSet<>();

    SpreadsheetRow headerRow = context.getRows().get(0);

    List<SpreadsheetCell> cells = headerRow.getCells();

    // First pass: identify input/output columns
    for (SpreadsheetCell indexedCell : cells) {
      String cellContent = context.resolveCellContent(indexedCell);
      if("input".equalsIgnoreCase(cellContent)) {
        inputColumns.add(indexedCell.getColumn());
      }
      else if("output".equalsIgnoreCase(cellContent)) {
        outputColumns.add(indexedCell.getColumn());
      }
    }

    // Second pass: build header value containers
    InputOutputColumns columns = new InputOutputColumns();
    int idCounter = 0;
    for (String column : inputColumns) {
      idCounter++;
      HeaderValuesContainer hvc = new HeaderValuesContainer();
      hvc.setId("input" + idCounter);
      fillHvc(context, column, hvc);
      columns.addInputHeader(hvc);
    }
    idCounter = 0;
    for (String column : outputColumns) {
      idCounter++;
      HeaderValuesContainer hvc = new HeaderValuesContainer();
      hvc.setId("output" + idCounter);
      fillHvc(context, column, hvc);
      columns.addOutputHeader(hvc);
    }

    return columns;
  }

  public HitPolicy determineHitPolicy(Spreadsheet context) {
    if (context.getRows().size() < 4) {
      return null;
    }
    SpreadsheetRow row = context.getRows().get(4);
    if (row.getCell("A") != null) {
      final String hitPolicyString = context.resolveCellContent(row.getCell("A")).toUpperCase();
      return HitPolicy.valueOf(hitPolicyString);
    }
    else
    {
      return null;
    }
  }

  @Override
  public List<SpreadsheetRow> determineRuleRows(Spreadsheet context) {
    List<SpreadsheetRow> rows = context.getRows();
    return rows.subList(5, rows.size());
  }

  private void fillHvc(Spreadsheet context, String column, HeaderValuesContainer hvc) {
    SpreadsheetCell cell;

    // Get all cells for this column at once to minimize lookups
    SpreadsheetRow row1 = context.getRows().get(1);
    SpreadsheetRow row2 = context.getRows().get(2);
    SpreadsheetRow row3 = context.getRows().get(3);
    SpreadsheetRow row4 = context.getRows().get(4);

    cell = row1.getCell(column);
    hvc.setLabel(context.resolveCellContent(cell));

    cell = row2.getCell(column);
    hvc.setExpressionLanguage(context.resolveCellContent(cell));

    cell = row3.getCell(column);
    hvc.setText(context.resolveCellContent(cell));

    cell = row4.getCell(column);
    hvc.setTypeRef(context.resolveCellContent(cell));

    hvc.setColumn(column);
  }

  public int numberHeaderRows() {
    return 5;
  }
}
