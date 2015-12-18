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
package org.camunda.bpm.dmn.xlsx.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.Output;

/**
 * @author Thorben Lindhauer
 *
 */
public class IndexedDmnColumns {

  protected Map<Input, IndexedCell> headerCellsByInput = new HashMap<Input, IndexedCell>();
  protected Map<Output, IndexedCell> headerCellsByOutput = new HashMap<Output, IndexedCell>();

  // as they appear in the resulting DMN table
  protected List<Input> orderedInputs = new ArrayList<Input>();
  protected List<Output> orderedOutputs = new ArrayList<Output>();

  public List<Input> getOrderedInputs() {
    return orderedInputs;
  }

  public List<Output> getOrderedOutputs() {
    return orderedOutputs;
  }

  public String getXlsxColumn(Input input) {
    IndexedCell headerCell = headerCellsByInput.get(input);
    return headerCell.getColumn();
  }

  public String getXlsxColumn(Output output) {
    IndexedCell headerCell = headerCellsByOutput.get(output);
    return headerCell.getColumn();
  }

  public void addInput(IndexedCell cell, Input input) {
    this.orderedInputs.add(input);
    this.headerCellsByInput.put(input, cell);
  }

  public void addOutput(IndexedCell cell, Output output) {
    this.orderedOutputs.add(output);
    this.headerCellsByOutput.put(output, cell);
  }

}
