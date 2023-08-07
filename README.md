# OpenDPMH: An Extensible Framework for Developing Mobile Sensing Applications of Digital Phenotyping
<p align="center">
OpenDPMH is a framework for developing mobile sensing applications able to collect useful user information for Digital Phenotyping of Mental Health (DPMH).
</p>

<p align="center">
  <img src="https://github.com/jeancomp/OpenDPMH/blob/master/open-dpmh-example.jpg" alt="OpenDPMH" width="300" />
</p>

## 📋 Requirements

* Android version: 6
* Android API Version: minSdkVersion > 26

## 📖  Documentation

### Composition mode:

 Composition mode is the app's behavior when a new data is collected by the middleware. The possibilities are:

- SEND_WHEN_IT_ARRIVES
- GROUP_ALL
- FREQUENCY
  - setFrequency(value)


### ⚙️ The Digital phenotyping manager
```
digitalPhenotypingManager = new DPManager.Builder(this)
                .setExternalServer("nameserver.com",1883)
                .setCompositionMode(CompositionMode.FREQUENCY)
                .setFrequency(15)
                .build();
        digitalPhenotypingManager.start();
```

### Stop the Digital phenotyping manager
```sh
digitalPhenotypingManager.stop();
```

## 🧰 Plugin

> The OpenDPMH architecture is enabled to add plugins, which extend the framework's capabilities by allowing the addition of new data processing modules.

### [Plugin Repository](https://github.com/jeancomp/Plugin)

## Cited Article IEEE Xplore

@INPROCEEDINGS {10178808,
author = {J. Mendes and F. Silva and A. Cardoso and I. Moura and L. Coutinho and D. Viana and M. Endler and A. S. Teles},
booktitle = {2023 IEEE 36th International Symposium on Computer-Based Medical Systems (CBMS)},
title = {OpenDPMH: A Framework for Developing Mobile Sensing Applications of Digital Phenotyping},
year = {2023},
volume = {},
issn = {},
pages = {198-203},
abstract = {Digital Phenotyping of Mental Health (DPMH) aims to passively collect data from ubiquitous devices to be used as evidence in the process of diagnosis, treatment, and monitoring. Literature presents different sensing mobile applications for digital phenotyping, however they are not extensible and can not be customized for use in other research. In this paper, we propose OpenDPMH, a framework for developing mobile sensing applications able to collect contextual data in order to produce useful user information that represent situations of interest for mental health professionals and researchers, such as human behaviors and habits. Our solution is extensible and reusable, as it allows the inclusion of modules for collecting and processing new raw context data with features for data distribution. By implementing a case study, we demonstrate that OpenDPMH is suitable for the development of DPMH mobile applications. Moreover, we carried out experiments to evaluate the energy consumption on smartphones, which demonstrate a low battery cost to run applications developed using the proposed framework.},
keywords = {energy consumption;costs;mental health;sensors;mobile applications;behavioral sciences;batteries},
doi = {10.1109/CBMS58004.2023.00216},
url = {https://doi.ieeecomputersociety.org/10.1109/CBMS58004.2023.00216},
publisher = {IEEE Computer Society},
address = {Los Alamitos, CA, USA},
month = {jun}
}


## 👏 Contributing
 

OpenDPMH is an open-source project. If there is a bug, or other improvement you would like to report or request, we encourage you to contribute.

Please, feel free to contact us for any questions: [![Gmail Badge](https://img.shields.io/badge/-ariel@lsdi.ufma.br-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:ariel@lsdi.ufma.br)](mailto:ariel@lsdi.ufma.br)

## 📄 License

OpenDPMH is LGPL-v3.0 licensed, as found in the [LICENSE][l] file.

[l]: https://github.com/jeancomp/OpenDPMH/blob/master/LICENSE.md
