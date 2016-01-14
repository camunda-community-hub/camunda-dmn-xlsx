Excel table to DMN decision table converter
===========================================

This project has two components:

1. A standalone converter to transform Excel worksheets (xlsx files) into DMN decision tables (dmn files), implemented and embeddable in Java.
2. A [Camunda BPM](https://www.camunda.org) process engine plugin to enable xlsx deployment as part of process applications. Xlsx files are then converted on the fly to DMN tables.

Features
--------

* Conversion of Excel worksheets to DMN decision tables with inputs, outputs and rules
* Pluggable strategy to determine columns that represent inputs and outputs
* Comes as a Java library that can be embedded into custom applications

Usage
-----

### Standalone Converter

#### Java

##### Artifacts

Currently, there are no published Maven artifacts available. In order to use the projects, checkout the sources and perform `mvn clean install`. Then, declare the following Maven dependency in a project that uses the converter:

```xml
<dependency>
  <groupId>org.camunda.bpm.dmn.xlsx</groupId>
  <artifactId>dmn-xlsx-converter</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

##### Read Excel file, transform to DMN, write to DMN file:

```java
InputStream xlsxInputStream = ...; // open xlsx file here

// convert
XlsxConverter converter = new XlsxConverter();
DmnModelInstance dmnModelInstance = converter.convert(inputStream);

// write
OutputStream dmnOutputStream = ...; // open outputstream to file here
Dmn.writeModelToStream(dmnOutputStream, dmnModelInstance);
```

##### Configure conversion

The class `org.camunda.bpm.dmn.xlsx.XlsxConverter` has bean properties that allow configuration of the conversion process. These are:

* `ioDetectionStrategy`: An instance of `org.camunda.bpm.dmn.xlsx.InputOutputDetectionStrategy`. The default strategy assumes that all but the last column of the worksheet are inputs and the last column is an output. An instance of `org.camunda.bpm.dmn.xlsx.StaticInputOutputDetectionStrategy` can be set to define a static set of input and output columns. Custom strategies can be implemented by implementing the interface `InputOutputDetectionStrategy`.

### Camunda BPM process engine plugin

#### Artifacts

Currently, there are no published Maven artifacts available. In order to use the process engine plugin, checkout the sources and execute `mvn clean install`.

#### Configuration

Make sure to make the resulting `org.camunda.bpm.dmn.xlsx:dmn-xlsx-process-engine-plugin` artifact available on the process engine's classpath. Then configure the class `org.camunda.bpm.xlsx.plugin.XlsxDmnProcessEnginePlugin` as a process engine plugin.

#### Usage

With the plugin in place, xlsx files can be included in a deployment and are automatically recognized and converted into DMN XML at deployment time. To configure conversion for a file `<name>.xlsx`, a file `<name>.xlsx.yaml` can be included in the deployment. Example yaml contents:

```yaml
---
inputs: ['A', 'B']
outputs: ['C', 'E']
```

This declares that the columns A and B are inputs of the decision and C and E are outputs.


License
-------

Apache License 2.0.

How to contribute
-----------------

Contributions are welcome at any time. Use github issues to discuss missing features or report bugs, and pull requests to contribute code.