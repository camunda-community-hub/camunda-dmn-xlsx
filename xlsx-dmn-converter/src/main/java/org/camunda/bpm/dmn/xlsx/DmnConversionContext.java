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

import org.xlsx4j.sml.Cell;

/**
 * @author Thorben Lindhauer
 *
 */
public class DmnConversionContext {

  protected List<CellContentHandler> cellContentHandlers = new ArrayList<CellContentHandler>();
  protected XlsxWorksheetContext worksheetContext;

  public DmnConversionContext(XlsxWorksheetContext worksheetContext) {
    this.worksheetContext = worksheetContext;
  }

  public String resolveCellValue(Cell cell) {
    for (CellContentHandler contentHandler : cellContentHandlers) {
      if (contentHandler.canConvert(cell, worksheetContext)) {
        return contentHandler.convert(cell, worksheetContext);
      }
    }
    throw new RuntimeException("cannot parse cell content, unsupported format");

  }

  public void addCellContentHandler(CellContentHandler cellContentHandler) {
    this.cellContentHandlers.add(cellContentHandler);
  }

}
