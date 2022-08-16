# OpenDPMH - Framework to Facilitate the Development of Digital Phenotyping Applications
<p align="center">
A framework aimed at identifying behavior patterns, habits, customs of monitored individuals through the collection of data from physical and virtual sensors.
</p>

## 📋 Requirements
---
* Android version: 6
* Android API Version: minSdkVersion > 26

## 📖  Documentation
---
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
---
> The plugin is the possibility to create another app that could connect with a previous OpenDPMH app installed on mobile, without the necessity to uninstall this.

### [Plugin Repository](https://github.com/jeancomp/Plugin)


## 👏 Contributing
--- 

OpenDPMH is an open-source project. If there's a a bug, or other improvement you'd like, we encourage you to contribute!

If you want to contact directly us, feel free by email: [![Gmail Badge](https://img.shields.io/badge/-ariel@lsdi.ufma.br-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:ariel@lsdi.ufma.br)](mailto:ariel@lsdi.ufma.br)