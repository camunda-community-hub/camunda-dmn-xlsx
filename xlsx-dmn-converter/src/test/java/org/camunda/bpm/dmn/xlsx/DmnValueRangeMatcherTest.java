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

import java.util.Arrays;
import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Thorben Lindhauer
 *
 */
@RunWith(Parameterized.class)
public class DmnValueRangeMatcherTest {

  @Parameters
  public static Iterable data() {
      return Arrays.asList(new Object[][] {
            { "[1..9]", true },
            { "]1..9]", true },
            { "[1..9[", true },
            { "]1..9[", true },
            { "[100..900]", true },
            { "[10.1..909]", true },
            { "[10..90.1]", true },
            { "[10.1..90.1]", true },
            { "text", false },
            { "[a..b]", false },
            { "[100..a]", false },
            { "[100..900", false },
            { "100..900", false },
            { "[100900]", false },
            { "[100.900]", false },
         });
     }

  @Parameter(0)
  public String input;

  @Parameter(1)
  public boolean shouldMatch;

  @Test
  public void shouldMatchInclusiveInterval()
  {
    Matcher matcher = DmnValueRangeConverter.RANGE_REGEX.matcher(input);

    Assert.assertEquals(shouldMatch, matcher.matches());
  }
}
