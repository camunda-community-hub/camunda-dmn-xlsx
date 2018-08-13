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

import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;

/**
 * @author Thorben Lindhauer
 *
 */
public class InputOutputColumns {

  protected List<HeaderValuesContainer> inputHeaderCells;
  protected List<HeaderValuesContainer> outputHeaderCells;

  public InputOutputColumns() {
    this.inputHeaderCells = new ArrayList<HeaderValuesContainer>();
    this.outputHeaderCells = new ArrayList<HeaderValuesContainer>();
  }

  public void addOutputHeaderCell(HeaderValuesContainer hvc) {
    this.outputHeaderCells.add(hvc);
  }

  public void addInputHeaderCell(HeaderValuesContainer hvc) {
    this.inputHeaderCells.add(hvc);
  }

  public List<HeaderValuesContainer> getOutputHeaderCells() {
    return outputHeaderCells;
  }

  public List<HeaderValuesContainer> getInputHeaderCells() {
    return inputHeaderCells;
  }
}
