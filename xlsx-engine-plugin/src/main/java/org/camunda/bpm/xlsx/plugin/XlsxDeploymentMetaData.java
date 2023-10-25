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
package org.camunda.bpm.xlsx.plugin;

import static org.camunda.bpm.dmn.xlsx.XlsxConverter.DEFAULT_HISTORY_TIME_TO_LIVE;

import java.util.Set;

import org.camunda.bpm.dmn.xlsx.StaticInputOutputDetectionStrategy;
import org.camunda.bpm.dmn.xlsx.XlsxConverter;

/**
 * @author Thorben Lindhauer
 *
 */
public class XlsxDeploymentMetaData {

  protected String historyTimeToLive = DEFAULT_HISTORY_TIME_TO_LIVE;
  protected Set<String> inputs;
  protected Set<String> outputs;

  public String getHistoryTimeToLive() {
    return historyTimeToLive;
  }

  public void setHistoryTimeToLive(String historyTimeToLive) {
    this.historyTimeToLive = historyTimeToLive;
  }

  public Set<String> getInputColumns() {
    return inputs;
  }

  public void setInputs(Set<String> inputColumns) {
    this.inputs = inputColumns;
  }

  public Set<String> getOutputs() {
    return outputs;
  }

  public void setOutputs(Set<String> outputColumns) {
    this.outputs = outputColumns;
  }

  public void applyTo(XlsxConverter converter) {
    if (outputs != null && !outputs.isEmpty()) {
      StaticInputOutputDetectionStrategy ioDetectionStrategy =
          new StaticInputOutputDetectionStrategy(inputs, outputs);
      converter.setIoDetectionStrategy(ioDetectionStrategy);
      converter.setHistoryTimeToLive(historyTimeToLive);
    }
  }

}
