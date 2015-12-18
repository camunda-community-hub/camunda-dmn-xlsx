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

import org.docx4j.openpackaging.parts.SpreadsheetML.SharedStrings;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSst;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;

/**
 * @author Thorben Lindhauer
 *
 */
public class XlsxWorksheetContext {

  protected CTSst sharedStrings;
  protected Worksheet worksheet;

  public XlsxWorksheetContext(CTSst sharedStrings, Worksheet worksheet) {
    this.sharedStrings = sharedStrings;
    this.worksheet = worksheet;
  }

  public List<Row> getRows() {
    return worksheet.getSheetData().getRow();
  }

  public String resolveCellValue(Cell cell) {
     List<CTRst> siElements = sharedStrings.getSi();
     // TODO: do something if v is null or cannot be parsed
     int index = Integer.parseInt(cell.getV());
     return siElements.get(index).getT().getValue();
  }

  public String resolveCellValue(Cell cell, CellContentConverter contentConverter) {
    List<CTRst> siElements = sharedStrings.getSi();
    // TODO: do something if v is null or cannot be parsed
    int index = Integer.parseInt(cell.getV());
    String rawValue = siElements.get(index).getT().getValue();

    return contentConverter.convert(rawValue);
 }


}
