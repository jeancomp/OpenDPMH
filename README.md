# OpenDPMH - Framework to Facilitate the Development of Digital Phenotyping Applications
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

### The framework:

The framework <i>OpenDPMH</i> (abbreviation for <b>Open</b> <b>D</b>igital <b>P</b>henotyping of <b>M</b>ental  <b>H</b>ealth) was developed to facilitate the development of mobile sensing applications of digital phenotyping. Next, we present its main components and modeling.

### Architecture

The framework <i>OpenDPMH</i> is implemented in the Java language for the Android operating system. The project is open source, available on <a href="https://github.com/jeancomp/OpenDPMH">GitHub</a> under the LGPL-3.0 license. It is divided into two main parts: <i>Core</i> and <i>Plugin</i>, as illustrated in the framework architecture in Figure 1. <i>Core</i> part is consumed by a main application and provides features for the management of sensors, raw data processing modules, one or more plugins, and data composition and distribution. The <i>OpenDPMH</i> architecture is enabled to add plugins (<i>Plugin</i> part in Figure 1), which extend the framework capabilities by allowing the addition of new data processing modules.


<h5 align="center">
  <img alt="Arquitetura-framework" title="#Arquitetura" src="/framework.png" />
</h5>
 
 <h5 align="center">
  <CAPTION>Fig.1 Architecture<CAPTION>
</h5>

The <i>Core</i> runs two CDDL instances. The first starts from the <i>DPManager</i> component and connects to a MQTT micro broker that serves for communication between all framework components. This also enables the communication between components of the <i>Core</i> and <i>Plugin</i>. The second CDDL instance is in the <i>PhenotypeComposer</i> and is used to establish a communication channel with an external broker to distribute a data structure that we call <i>Digital Phenotype</i>. This is a Java class with attributes to represent raw context data and high-level information (i.e., digital phenotyping events, described in Section Digital Phenotype Model). High-level information may correspond to human behaviors and habits (e.g., sociability, physical activity, mobility, sleep), and other information of interest for mental health professionals (e.g., environmental context, mood). Other systems (i.e., dashboards for data visualization by professionals) connected to the MQTT external broker can subscribe to receive patient information.

The following components are part of the Core:

*fact that can compromise the use of a digital phenotyping application developed with the <i>OpenDPMH</i>. Therefore, the developer can use this feature to ensure data distribution protection.

![](header.png)


Project-status
=================

<h4 align="center"> 
	��  Framework - finished...  ��
</h4>


Release-history
=================

* 1.0

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
```sh
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


Made by Jean Pablo ���� Contact!

[![Twitter](https://img.shields.io/twitter/url?label=%40jeancomp&style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)
[![Linkedin Badge](https://img.shields.io/badge/-Jean-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/jean-pablo-marques-mendes/)](https://www.linkedin.com/in/jean-pablo-marques-mendes/) 
[![Gmail Badge](https://img.shields.io/badge/-jeancomp@gmail.com-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:jeancomp@gmail.com)](mailto:jeancomp@gmail.com)

