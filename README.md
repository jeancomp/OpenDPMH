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

### [IEEE Xplore](https://ieeexplore.ieee.org/abstract/document/10178808)


## 👏 Contributing
 

OpenDPMH is an open-source project. If there is a bug, or other improvement you would like to report or request, we encourage you to contribute.

Please, feel free to contact us for any questions: [![Gmail Badge](https://img.shields.io/badge/-ariel@lsdi.ufma.br-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:ariel@lsdi.ufma.br)](mailto:ariel@lsdi.ufma.br)

## 📄 License

OpenDPMH is LGPL-v3.0 licensed, as found in the [LICENSE][l] file.

[l]: https://github.com/jeancomp/OpenDPMH/blob/master/LICENSE.md
