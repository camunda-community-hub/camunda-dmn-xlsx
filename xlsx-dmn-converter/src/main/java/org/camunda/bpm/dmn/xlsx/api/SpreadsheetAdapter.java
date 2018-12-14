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
package org.camunda.bpm.dmn.xlsx.api;

import org.camunda.bpm.dmn.xlsx.InputOutputColumns;
import org.camunda.bpm.dmn.xlsx.XlsxWorksheetContext;
import org.camunda.bpm.model.dmn.HitPolicy;

/**
 * Implement this interface to tailor the conversion process to a specific format
 * of your excel sheets.
 */
public interface SpreadsheetAdapter {

  InputOutputColumns determineInputOutputs(XlsxWorksheetContext context);

  /**
   * @return the number of leading rows before the decision rules begin
   */
  int numberHeaderRows();

  /**
   * @return null to use the DMN default
   */
  HitPolicy determineHitPolicy(XlsxWorksheetContext context);
}
