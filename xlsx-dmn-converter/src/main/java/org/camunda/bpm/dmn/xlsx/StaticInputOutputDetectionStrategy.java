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

import java.util.Set;

import org.camunda.bpm.dmn.xlsx.elements.IndexedCell;
import org.camunda.bpm.dmn.xlsx.elements.IndexedRow;

/**
 * @author Thorben Lindhauer
 *
 */
public class StaticInputOutputDetectionStrategy implements InputOutputDetectionStrategy {

  protected Set<String> inputColumns;
  protected Set<String> outputColumns;

  public StaticInputOutputDetectionStrategy(Set<String> inputColumns, Set<String> outputColumns) {
    this.inputColumns = inputColumns;
    this.outputColumns = outputColumns;
  }

  public InputOutputColumns determineHeaderCells(IndexedRow headerRow, XlsxWorksheetContext context) {
    InputOutputColumns columns = new InputOutputColumns();

    for (IndexedCell cell : headerRow.getCells()) {
      if (inputColumns.contains(cell.getColumn())) {
        columns.addInputHeaderCell(cell);
      }
      else if (outputColumns.contains(cell.getColumn())) {
        columns.addOutputHeaderCell(cell);
      }
    }

    return columns;
  }
}
