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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.camunda.bpm.dmn.xlsx.elements.IndexedDmnColumns;

/**
 * @author Thorben Lindhauer
 *
 */
public class DmnConversionContext {

  protected final List<CellContentHandler> cellContentHandlers;
  protected final XlsxWorksheetContext worksheetContext;

  protected IndexedDmnColumns indexedDmnColumns = new IndexedDmnColumns();
  protected final Map<String, String> resolvedValueCache = new HashMap<>();

  public DmnConversionContext(XlsxWorksheetContext worksheetContext, List<CellContentHandler> cellContentHandlers) {
    this.worksheetContext = worksheetContext;
    this.cellContentHandlers = cellContentHandlers;
  }

  public String resolveCellValue(SpreadsheetCell cell) {
    // Create cache key from cell coordinates
    String cacheKey = cell.getColumn() + cell.getRow();
    
    // Check cache first
    String cached = resolvedValueCache.get(cacheKey);
    if (cached != null) {
      return cached;
    }
    
    // Find appropriate handler and convert
    for (CellContentHandler contentHandler : cellContentHandlers) {
      if (contentHandler.canConvert(cell, worksheetContext)) {
        String result = contentHandler.convert(cell, worksheetContext);
        resolvedValueCache.put(cacheKey, result);
        return result;
      }
    }
    
    throw new RuntimeException("cannot parse cell content, unsupported format");
  }

  public IndexedDmnColumns getIndexedDmnColumns() {
    return indexedDmnColumns;
  }

}
