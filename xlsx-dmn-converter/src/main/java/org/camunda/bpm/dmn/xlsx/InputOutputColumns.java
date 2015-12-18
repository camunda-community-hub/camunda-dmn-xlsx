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

import org.camunda.bpm.dmn.xlsx.elements.IndexedCell;

/**
 * @author Thorben Lindhauer
 *
 */
public class InputOutputColumns {

  protected List<IndexedCell> inputHeaderCells;
  protected List<IndexedCell> outputHeaderCells;

  public InputOutputColumns() {
    this.inputHeaderCells = new ArrayList<IndexedCell>();
    this.outputHeaderCells = new ArrayList<IndexedCell>();
  }

  public void addOutputHeaderCell(IndexedCell cell) {
    this.outputHeaderCells.add(cell);
  }

  public void addInputHeaderCell(IndexedCell cell) {
    this.inputHeaderCells.add(cell);
  }

  public List<IndexedCell> getOutputHeaderCells() {
    return outputHeaderCells;
  }

  public List<IndexedCell> getInputHeaderCells() {
    return inputHeaderCells;
  }
}
