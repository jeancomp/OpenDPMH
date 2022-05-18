# OpenDP - Framework to Facilitate the Development of Digital Phenotyping Applications
> A framework aimed at identifying behavior patterns, habits, customs of monitored individuals through the collection of data from physical and virtual sensors.


[![GitHub issues](https://img.shields.io/github/issues/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/issues)
[![GitHub forks](https://img.shields.io/github/forks/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/network)
[![GitHub stars](https://img.shields.io/github/stars/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/stargazers)
[![GitHub license](https://img.shields.io/github/license/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1)
[![Twitter](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Fgithub.com%2Fjeancomp%2Ffenotipagem_digital_saude_vs_0_1)

Summary
=================
<!--ts-->
   * [Goals](#Goals)
   * [Project status](#Project-status)
   * [Release history](#Release-history)
   * [How to use](#How-to-use)
      * [Prerequisites](#Prerequisites)
      * [Installation](#Installation)
      * [Example of use](#Example-of-use)
   * [Plugin](#Plugin)
   * [Certification Authority with Mosquito](#Certification-Authority-With-Mosquito)
   * [License](#License)
   * [Contribution](#Contribution)
   * [Author](#Author)
<!--te-->


Goals
=================
The general objective of this work is to provide a framework focused on Digital Mental Health Phenotyping (DPMH). The solution will facilitate the development of mobile applications that can passively collect context data, process it, and generate high-level information. Therefore, this framework aims to create a software base to support the implementation of solutions that aim to recognize patterns of behavior and habits of users, which can support mental health professionals in their analyses, diagnoses, and treatments.

<h1 align="center">
  <img alt="Arquitetura-framework" title="#Arquitetura" src="/framework.png" />
</h1>

Core Components:
* DPManager: responsible for managing the framework (e.g., start/stop, start/stop the processors, configuring the composition mode of PhenotypeComposer).
* ProcessorManager: manages activeDataprocessor (e.g., start/stop activeDataprocessor), start/stop sensors, and identifies new plugin that has been installed.
* DataProcessor: the class where the processors will be implemented (e.g., sociability, mobility, sleep, physical activity).
* RawDataCollector: The class responsible for collecting the raw data and distributing it to the broker, provides EPL resources for the developer to create their CEP rule.
* PhenotypeComposer: composes digital phenotypesEvent of users, receiving directly from the data processor of detected events (eg phone calls, sms, GPS, accelerometer).

Plugin Components:
* PluginManager: class responsible for managing the plugin and its data processing modules that it belongs to. When starting, it sends the list of data processing modules to the Core, when the Core receives this list, it returns only the modules that it is interested in starting, receives data from the Core's sensors, processes them, and returns to the Core.
* DataProcessor: features the same functionality in Core.


![](header.png)


Project-status
=================

<h4 align="center"> 
	🚧  Framework - finished...  🚧
</h4>

### Features

- [x] DPManager
- [x] ProcessorManager
- [x] DataProcessor
- [x] PhenotypeComposer
- [x] RawDataCollector


Release-history
=================

* 0.0.1
    * Security adaptation (digital certificates) in progress.
    * Adaptation of virtual sensors to the M-HUB.

How-to-use
==================

Prerequisites
-----
* Android version: 6
* Android API Version: minSdkVersion > 26

Installation
-----

Linux & Windows:

```sh
1º option (github project):
	* download the zip project, unzip it.
	* then open with in Android Studio "Open an Existing Project", ready.
```
```sh
2º option (aar files): under construction... 
```
```sh
3º option (apk): under construction... 
```

Example-of-use
-----
DPManager
```sh
public DPManager digitalPhenotypingManager;
```
Start-framework:
```sh
Option:
1 - SEND_WHEN_IT_ARRIVES
2 - GROUP_ALL
3 - FREQUENCY
3.1 - setFrequency(value).
```
```sh
digitalPhenotypingManager = new DPManager.Builder(this)
                .setExternalServer("nameserver.com",1883)
                .setCompositionMode(CompositionMode.FREQUENCY)
                .setFrequency(15)
                .build();
        digitalPhenotypingManager.start();
```
Stop-framework:
```sh
digitalPhenotypingManager.stop();
```
Start-activeDataprocessor:
```sh
List<String> listProcessors = {nameProcessor1,nameProcessor2,...};
digitalPhenotypingManager.getInstance().startDataProcessors(listProcessors);
```
Stop-DisableDataprocessor:
```sh
List<String> listProcessors = {nameProcessor1,nameProcessor2,...};
digitalPhenotypingManager.getInstance().stopDataProcessors(listProcessors);
```

Plugin
=================
Instructions for use.
-----
[https://github.com/jeancomp/Plugin](https://github.com/jeancomp/Plugin)


Certification-Authority-With-Mosquito
=================
Instructions for use.
-----
[https://github.com/jeancomp/Certification-Authority-With-Mosquito](https://github.com/jeancomp/Certification-Authority-With-Mosquito)

License
=================

Your Name – [@Twitter](https://twitter.com/jeancomp) – jean.marques@lsdi.ufma.br

Distributed under the XYZ license. See ``LICENSE`` for more information.

[https://github.com/](https://github.com/jeancomp)

Contribution
=================

Main developer:
1.  Jean Pablo (<https://github.com/jeancomp>)


Contributors:
1. Ariel Teles (https://github.com/arielsteles)
2. André

<!-- Markdown link & img dfn's -->
[npm-image]: https://img.shields.io/npm/v/datadog-metrics.svg?style=flat-square
[npm-url]: https://npmjs.org/package/datadog-metrics
[npm-downloads]: https://img.shields.io/npm/dm/datadog-metrics.svg?style=flat-square
[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg?style=flat-square
[travis-url]: https://travis-ci.org/dbader/node-datadog-metrics
[wiki]: https://github.com/yourname/yourproject/wiki


Author
=================

<a href="https://github.com/jeancomp">
       <a href="https://imgbb.com/"><img src="https://i.ibb.co/MsLwGfj/jp.jpg" alt="jp" border="0" width="80px;" /></a>
 <br />
 <sub><b>Jean Pablo</b></sub></a>


Made by Jean Pablo 👋🏽 Contact!

[![Twitter](https://img.shields.io/twitter/url?label=%40jeancomp&style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)
[![Linkedin Badge](https://img.shields.io/badge/-Jean-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/jean-pablo-marques-mendes/)](https://www.linkedin.com/in/jean-pablo-marques-mendes/) 
[![Gmail Badge](https://img.shields.io/badge/-jeancomp@gmail.com-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:jeancomp@gmail.com)](mailto:jeancomp@gmail.com)

