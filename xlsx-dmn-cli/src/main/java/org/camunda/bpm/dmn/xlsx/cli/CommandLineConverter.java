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
package org.camunda.bpm.dmn.xlsx.cli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.camunda.bpm.dmn.xlsx.AdvancedSpreadsheetAdapter;
import org.camunda.bpm.dmn.xlsx.StaticInputOutputDetectionStrategy;
import org.camunda.bpm.dmn.xlsx.XlsxConverter;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetAdapter;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;

/**
 * @author Thorben Lindhauer
 *
 */
public class CommandLineConverter {

  public static void main(String[] args) {

    if (args.length == 0) {
      final String LINE_SEPARATOR = System.getProperty("line.separator");
      StringBuilder sb = new StringBuilder();
      sb.append("Usage: java -jar ...jar [--inputs A,B,C,..] [--outputs D,E,F,...] [--advanced] path/to/file.xlsx path/to/outfile.dmn");
      sb.append(LINE_SEPARATOR);
      sb.append("The use of --advanced negates the --input and --output parameters.");
      System.out.println(sb.toString());
      return;
    }

    SpreadsheetAdapter ioStrategy;
    XlsxConverter converter = new XlsxConverter();

    String inputFile = args[args.length - 2];
    String outputFile = args[args.length - 1];


    Set<String> inputs = null;
    Set<String> outputs = null;
    for (int i = 0; i < args.length - 2; i++) {
      if ("--inputs".equals(args[i])) {
        inputs = new HashSet<>();
        inputs.addAll(Arrays.asList(args[i + 1].split(",")));
      }

      if ("--outputs".equals(args[i])) {
        outputs = new HashSet<>();
        outputs.addAll(Arrays.asList(args[i + 1].split(",")));
      }

      if ("--advanced".equals(args[i])) {
        inputs = null;
        outputs = null;
        ioStrategy = new AdvancedSpreadsheetAdapter();
        converter.setIoDetectionStrategy(ioStrategy);
      }
    }



    if (inputs != null && outputs != null) {
      ioStrategy = new StaticInputOutputDetectionStrategy(inputs, outputs);
      converter.setIoDetectionStrategy(ioStrategy);
    }

    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
      try {

        fileInputStream = new FileInputStream(inputFile);
        DmnModelInstance dmnModelInstance = converter.convert(fileInputStream);
        fileOutputStream = new FileOutputStream(outputFile);

        Dmn.writeModelToStream(fileOutputStream, dmnModelInstance);
      }
      finally {
        if (fileInputStream != null) {
          fileInputStream.close();
        }

        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      }
    } catch (Exception e) {
      System.out.println("Could not convert file: " + e.getMessage());
      e.printStackTrace(System.out);
    }

  }
}
