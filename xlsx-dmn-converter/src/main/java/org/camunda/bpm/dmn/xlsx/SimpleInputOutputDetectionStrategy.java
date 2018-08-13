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

import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;
import org.camunda.bpm.dmn.xlsx.elements.IndexedCell;
import org.camunda.bpm.dmn.xlsx.elements.IndexedRow;

/**
 * @author Thorben Lindhauer
 *
 */
public class SimpleInputOutputDetectionStrategy implements InputOutputDetectionStrategy {

  public InputOutputColumns determineHeaderCells(IndexedRow headerRow, XlsxWorksheetContext context) {
    if (!headerRow.hasCells()) {
      throw new RuntimeException("A dmn table requires at least one output; the header row contains no entries");
    }

    InputOutputColumns ioColumns = new InputOutputColumns();

    List<IndexedCell> cells = headerRow.getCells();
    HeaderValuesContainer hvc = new HeaderValuesContainer();
    IndexedCell outputCell = cells.get(cells.size() - 1);
    fillHvc(outputCell, context, hvc);
    hvc.setId("Output" + outputCell.getColumn());

    ioColumns.addOutputHeaderCell(hvc);

    for (IndexedCell inputCell : cells.subList(0, cells.size() - 1)) {
      hvc = new HeaderValuesContainer();
      fillHvc(inputCell, context, hvc);
      hvc.setId("Input" + inputCell.getColumn());
      ioColumns.addInputHeaderCell(hvc);
    }

    return ioColumns;
  }

  @Override
  public String determineHitPolicy(XlsxWorksheetContext context) {
    return null;
  }

  private void fillHvc(IndexedCell cell, XlsxWorksheetContext context, HeaderValuesContainer hvc) {
    hvc.setText(context.resolveCellValue(cell.getCell()));
    hvc.setColumn(cell.getColumn());
  }

  public int numberHeaderRows() {
        return 1;
  }

}
