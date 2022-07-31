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

* <i>DPManager</i>: provides an API for managing the framework, with methods to start/stop services, activate/deactivate data processing modules (i.e., <i>DataProcessor</i> components), the security service, and define the composition mode of digital phenotypes, which is described below. In addition, this component uses the Builder Design Pattern to allow a step-by-step definition of necessary settings for the framework to work, such as informing the user ID, address and port of the MQTT external broker;

* <i>DataProcessor</i>: infers high-level information from data collected using physical and virtual sensors. When creating a <i>DataProcessor</i>, the developer should inform its name and a list of sensors to receive context data. Also, the developer should implement the business rule to infer high-level information, which can use specification- (e.g., complex event processing, fuzzy logic) or learning-based techniques (e.g., machine/deep learning models). Inferred high-level information are represented in the framework as digital phenotyping events and sent to the <i>PhenotypeComposer</i> component, where digital phenotypes are composed and distributed;

* <i>ProcessorManager</i>: this is a service responsible for managing <i>DataProcessor</i> components. It receives the list of <i>DataProcessor</i> components from the <i>DPManager</i> to be started. Also, the <i>ProcessorManager</i> manages the lifecycle of all sensors in the framework, so controlling their activation and deactivation, and managing Android runtime permissions. It avoids keeping sensors active that are not in use, or deactivating sensors in use by <i>DataProcessor</i> components. For this purpose, each <i>DataProcessor</i> should inform the required physical or virtual sensors;

<ul> <li><i>PhenotypeComposer</i>: service responsible for creating digital phenotype objects to be distributed via CDDL. To enable data buffer, this component works in three different modes:

<ol>
    <li> <i>send_when_it_arrives</i>: this is the simplest composition mode. When digital phenotyping events and raw context data arrive at the <i>PhenotypeComposer</i>, it immediately distributes them via the MQTT broker external. Therefore, in this mode, data is not buffered. Such mode is important for applications that mental health professionals require (near) real-time information;</li>
    <li> <i>group_all</i>: this mode distributes digital phenotypes only when digital phenotyping events from all active <i>DataProcessor</i> components arrives. While such condition is not satisfied, raw context data and digital phenotyping events and information is stored in a database (using model - Digital Phenotype Model);</li>
    <li> <i>frequency</i>: it has a schedule that controls the distribution of high-level information to the broker external, based on the frequency (e.g., seconds, minutes, hours) previously defined when starting the framework. When firing the schedule, all high-level information stored in the database (using model - Digital Phenotype Model) is retrieved and distributed to the broker external.</li>
</ol>
	</li>
	</ul>

* <i>RawDataCollector</i>: service responsible for collecting raw context data to also compose digital phenotypes. The <i>RawDataCollector</i> needs the names of the sensors to start collecting the data and choosing the way of composing the digital phenotypes, which are similar to the <i>PhenotypeComposer</i>. The compositing mode <i>send\_when\_it\_arrives</i> send raw data to broker (i.e., as the data is being received by the <i>RawDataCollector</i>, is distributed) and the composition mode <i>frequency</i> works through a schedule, when triggered, retrieves all raw context data from the database and distributes to the broker. The <i>RawDataCollector</i> also provides EPL/CEP (i.e., distributes, models and detects complex events on the flow of data generated by sensors, providing a language similar to SQL with SELECT, FROM, WHERE, GROUP BY, HAVING and ORDER BY clauses, being able to implement association, filtering and aggregation concepts).
	  
The following components are part of the Plugin:
* PluginManager: class responsible for managing the plugin and its data processing modules that it belongs to. When starting, it sends the list of data processing modules to the Core, when the Core receives this list, it returns only the modules that it is interested in starting, receives data from the Core's sensors, processes them, and returns to the Core.
* DataProcessor: features the same functionality in Core.

### Adapting NAM-Hub for Digital Phenotyping

In addition to physical sensors, digital phenotyping tools are also using <a href="https://dl.acm.org/doi/10.1145/2893487">virtual</a> sensors to infer high-level information, such as those related to <a href="https://www.sciencedirect.com/science/article/abs/pii/S0167739X22000310">sociability</a> and <a href="https://www.sciencedirect.com/science/article/abs/pii/S1574119211001246">sleep</a>. However, the original version of the <a href="https://interscity.org/assets/Neighborhood-awareMobileHubAnE.pdf">NAM-Hub</a> does not work with virtual sensors. Therefore,
in this work, the drivers were implemented using broadcast receivers, they are: phone call, text message (SMS), screen touch, and screen on/off, were added to the NAM-Hub as an internal technology, where it can be viewed as a <i>SensorPhone</i>. The concept of driver in NAM-Hub means a set of code programmed so that the smartphone can interpret/collect the user's interaction with the environment (e.g., driver that receives latitude/longitude data from the GPS sensor when the user moves the smartphone). In this way, the framework <i>OpenDPMH</i> manages to collect user data through virtual sensors and generate high-level information from them.

### Digital Phenotype Model

The high-level information inferred by the <i>DataProcessor</i>, which are <a href="https://www.sciencedirect.com/science/article/abs/pii/S1574119211000253">situations of interest</a> and can represent a user's physical state, an event or a digital phenotype. Before detailing how to represent a digital phenotype, it is important to define which attributes and situations are in the framework.

Attributes (shown in Figure 2) are properties that characterize a digital phenotyping event. Timestamp and location coordinates are examples of user physical state recognition event attributes. These behavioral changes are situations of interest for mental health professionals and researchers to use to monitor well-being or contribute to the treatment of mental disorders.
Running, walking, driving are possible physical states of users that represent situations of interest to mental health professionals and researchers. Through the class diagram shown in Figure 2, it is possible to represent the digital phenotyping events, and the set of these objects will compose the user's digital phenotype (<i>DigitalPhenotype</i>).

<h5 align="center">
  <img alt="Arquitetura-framework" title="#Class diagram for representing digital phenotypes in the framework." src="/digitalPhenotype.jpeg" height="700" width="500"/>
	  </h5>
	
<h5 align="center">
	<CAPTION>Fig.2 Class diagram for representing digital phenotypes in the framework.<CAPTION>
</h5>

So the class diagram <i>DigitalPhenotypeEvent</i> can represent digital phenotyping events, such as: user identifier (<i>uid</i>), name of data processing module <i>dataProcessorName</i>, time of occurrence of the event (<i>startDateTime</i>, <i>endDateTime</i>), and the set of objects <i>DigitalPhenotypeEvent</i> make up a <i>DigitalPhenotype</i> representing the digital phenotype. The object Attribute can represent the properties of digital phenotyping events, such as: attribute name, value, type. The object Situation can represent the situation of interest, such as: name of the situation of interest (label) and description.

### Security in OpenDPMH

Information security is a requirement for almost any IT application, therefore, when developing digital phenotyping applications through the OpenDPMH framework it is necessary to ensure security mechanisms, especially if sensitive data is involved. Security properties like authentication, authorization, and confidentiality in the distribution of data are a must for protecting digital phenotyping applications. To guarantee those properties in OpenDPMH framework, we encapsulate the CDDL security service.

The framework security service uses digital certificates to authenticate and establish secure communication channels via TLS and an Access Control List (ACL) to prevent unauthorized users to access the OpenDPMH data, however, this service is optional for the developer to use it or not. The entire process to activate the framework's secure mode is done by <i>DPManager</i>, where it uses the builder pattern to receive information to configure the digital certificates. For that, we provide the <i>DPInterface</i> interface that contains the signatures of the methods necessary for the developer to configure and implement it in the best way, or use the code that is implemented in the framework. The process to configure the <i>OpenDPMH</i> security service is (I) generate the certificate signing request, (II) send the CSR to the trusted Certificate Authority (CA), (III) import the user signed certificate and trusted CA certificate, (IV) define access control rules. Before using the framework in secure mode, this process must be done so all required certificates can be used on authentication and establishment of secure communication channels. The <i>PhenotypeComposer</i> component is responsible for opening the secure connection with the external broker and the <i>DPInterface</i> exposes all the methods needed for configuring the digital certificates. 

To prevent a malicious application from intercepting the communication between the core and the plugin, it is also necessary to open a secure communication channel between them, therefore, the plugin
will have to start the security service. The plugin also uses the builder pattern to receive information to configure the digital certificate. The plugin has built-in methods (e.g., <i>sendCertificateCSR</i>, <i>receiveCertificate-CRT</i>) as well as the core that also verifies if the digital certificate has already been created. The process to configure the security service in the plugin is (I) generate the certificate signing request, (II) send the CSR to the trusted Certificate Authority (CA), (III) import the user signed certificate and trusted CA certificate. This way, the security service is configured in the plugin and ready to establish a secure communication channel with the core.

### Case Studies

Two applications were implemented using the <i>OpenDPMH</i> framework: a case study using only the core, and another one with the addition of a plugin. In both case studies, the objective is to demonstrate the mechanisms provided by the framework in the development process of mobile sensing applications for digital phenotyping of mental health.

### Case Study 1: Core

The symptoms of mental disorders can be externalized by changes in social behaviors, which characterizes a situation of interest for the monitoring of mental health. To implement the first case study, we considered sociability as a situation of interest in an illustrative scenario described below.

<i>A psychologist, named Andressa, used an application developed with the framework to understand behaviors and habits of a patient called Maria. In the consultation of the therapy (a first face-to-face session), Andressa collected information about Maria using self-reports. Maria reported living in São Luís, MA (a Brazilian city), married, mother of one child, and working in a private company as a secretary for about one year. Maria further informed that the reason she was seeking therapy was due to personal stresses, relationship conflicts, as well as professional issues. Based on the information obtained, Andressa decided to use a digital phenotyping application to better understand sociability behavior of Maria, as she considered it to be an important aspect to be analyzed, since social relationships' characteristics can protect or contribute to the development of <a href="https://www.mdpi.com/1424-8220/21/1/86">mental disorders</a>. Andressa explained to Maria that she would use a mobile application to support therapy, and no private information collected (e.g., phone calls and text messages) would be exposed. Maria then agreed and authorized the monitoring, so allowing the application to be installed on her smartphone. From the second weekly session, Andressa was able to analyze results achieved by providing the use of the application to Maria.</i>

By using the <i>OpenDPMH</i>, two <i>DataProcessor</i> components were implemented to compose digital phenotypes: <i>Online\_Sociability</i> and <i>Physical\_Sociability</i>. The first one aimed at identifying user online socialization by collecting data from two virtual sensors, phone call and text messaging (SMS), which are already provided by the <i>OpenDPMH</i> framework. In this case study, any event of these two virtual sensors indicates that a user is socializing, intends to socialize or, at least, may indicate a cue related to sociability. Examples of events monitored by the virtual sensors are: receiving/initiating phone calls, and receiving/sending SMS messages, screen on/off and screen touch. The second <i>DataProcessor</i> component implemented was <i>Physical\_Sociability</i>, which aimed to identify whether the user is socializing in person. Audio samples via microphone are captured and inferred the presence of human voice using the <a href="https://github.com/gkonovalov/android-vad">VAD</a> library. This component assumes that if a human voice is found around the mobile device, there is a cue related to sociability. The composition mode of digital phenotypes defined was <i>frequency</i>, with a data distribution interval to the external broker of 15 minutes for visualization by mental health professionals.

Figure 3 displays application screenshots developed for the first case study. After activating the <i>DataProcessor</i> components available in the application, the user can visualize them with status "on" (i.e., running), as shown in Figure 3(a). When clicking on the <i>Physical\_Sociability</i> component card, the application depicts a dashboard of event logs (Figure 3(b)), with information such as date and time of the last event, a graph showing the number of conversations in person (x-axis shows the day, and y-axis is the number of conversation records), and a button to finish the processing. Figure 3(c) displays a dashboard of the <i>Online\_Sociability</i> component, with different information regarding phone calls and SMS messages, such as: date and time of the last record (call and message), the number and type of call (incoming, outgoing or missed) and message (incoming and outgoing).

<h5 align="center">
  <img alt="Arquitetura-framework" title="#Application screens of the first case study." src="/b.jpg" height="470" width="700"/>
		</h5>
	
<h5 align="center">
	<CAPTION>Fig.3 Application screens of the first case study.<CAPTION>
</h5>

Screenshots in Figures 3 (b) and (c) present a preview of what is produced from the Maria's smartphone and can be visualized by Andressa in a consumer application. As shown, the application is able to monitor the day-to-day sociability of patients in treatments, so recording the moments when they socialize. From identified situations of interest (sociability in the case study), psychologists can make more appropriate and evidence-based interventions. Therefore, this illustrative case study shows that a developer can use the <i>OpenDPMH</i> framework to implement data processing modules (i.e., <i>DataProcessor</i> components) capable of generating situations of interest relevant to the mental health professional. Moreover, as the framework provides reusable code available to be instantiated or put into execution (e.g., virtual and physical sensors, data distribution mechanisms), the digital phenotyping application of this case study was easily implemented, so the proposed solution can facilitate the development process.

### Case Study 2: Plugin

The second case study is a continuation of the first illustrative scenario presented:

<i>As therapy sessions progressed, Andressa realized that she also needed to monitor Maria's physical activity, a situation of interest that is strongly related to several <a href="https://www.sciencedirect.com/science/article/abs/pii/S1755296619301632">mental disorders</a>.}</i>

To add a new <i>DataProcessor</i> component to recognize high-level information related to physical activity, the Android project of the first application using the <i>OpenDPMH</i> should be reimplemented and recompiled to build a new application. This is not interesting as it would require a software update on the patient's smartphone, which would stop collecting data, hence losing data. Therefore, to enable the monitoring of a new situation of interest required by Andressa, the <i>DataProcessor</i> component <i>Physical\_Activity</i> was implemented in a plugin using the <a href="https://developers.google.com/location-context/activity-recognition">Activity Recognition API</a> to infer user's physical states (e.g., running, walking, in vehicle, on bicycle).

Figure 4 displays screens of the application running over the <i>Core</i> part of the framework, which has already two data processing modules working (<i>Physical\_Sociability</i> and <i>Online\_Sociability</i>), as seen in Figure 4 (a). After running the plugin, the <i>DataProcessor</i> component <i>Physical\_Activity</i> becomes available to be activated, as seen in Figure 4 (b). Finally, as depicted in Figure~\ref{fig:plugin} (c), a dashboard summarizes different information regarding physical activity states, such as: the total number of each state, a graph with time series of each activity, and the last record of activity transition.

<h5 align="center">
  <img alt="Arquitetura-framework" title="#Application screens of the second case study." src="/c.jpg" height="470" width="700"/>
	</h5>
	
<h5 align="center">
	<CAPTION>Fig.4 Application screens of the second case study.<CAPTION>
</h5>

The second case study demonstrates our framework working with its two parts integrated (<i>Core</i> and <i>Plugin</i>), which is a feature that allows mental health professionals to enhance the patient monitoring process using a digital phenotyping. Also, this extensibility feature enables the developer to implement different modules in plugins, which can be shared to be reused in other research.

### Energy Consumption Evaluation
\label{evaluation}

We performed an experimental evaluation to measure the energy consumption when using the framework. This experimental evaluation is important to verify that mobile applications of digital phenotyping should not consume excessive energy. Otherwise, by accelerating the depletion of the battery, users might uninstall the application to prevent battery drain, so applications will be used for a short time or avoided.

In addition, accelerated battery drain will possibly create user dissatisfaction, causing the application to be uninstalled.

When considering energy consumption as a key point for the adoption of digital phenotyping applications, the objective of the experiments was to evaluate the impact that the framework <i>OpenDPMH</i> brought to the energy consumption of a smartphone.

In the experiments, we used the applications developed in the previous case studies with different configurations to monitor energy consumption. Applications were run for a period of 10 hours. After initialization, applications began to collect sensor data and compose digital phenotypes. The battery level was measured before and after the runtime. For this purpose, we used the Android library <i>BatteryManager</i>. Furthermore, except for applications that integrate the Android operating system (e.g., calendar, SMS exchange app, phone call app), there were no other applications running on the mobile device. The experiments were carried out in a smartphone Samsung Galaxy A01, 32 GB of disk storage, 2 GB of RAM, 4G network, Wi-Fi, 3000 mAh battery, Octa-Core 2.0 GHz processor, and Android operating system version 10.0.

### Only Core Working
\label{primeiraavaliacao}

We performed the first experimental evaluation to analyze the impact of energy consumption in relation to the three modes of composition of the digital phenotype (<i>send\_when\_it\_arrives</i>, <i>group\_all</i> and <i>frequency</i>). 

The objective of the first evaluation was to analyze the impact of energy consumption caused by the framework working with two processing modules (<i>Physical\_Activity</i> and <i>Physical\_Sociability</i>) of data in the three modes of composition of digital phenotypes (<i>frequency</i>, <i>group\_all</i> and <i>send\_when\_it\_arrives</i>). The way <i>frequency</i> used two frequency settings for sending data to the broker:high (every 15 minutes) and low (every 45 minutes). The module <i>Physical\_Activity</i> was configured so, if no activity was inferred within a period of one minute, it would trigger a message with no data to the <i>PhenotypeComposer</i>. The purpose of this condition was not to let the <i>PhenotypeComposer</i> without receiving data for a long time. The module <i>Physical\_Sociability</i>  was set to process for one minute and pause for 3 minutes. These module settings remained fixed across all assessments. In this way, we created five scenarios ({setups}) for the first evaluation, as seen below.

\begin{enumerate}
    \item {\small <b>{Setup 1</b>}: Smartphone with factory settings;}
    \item {\small <b>{Setup 2</b>}: Only core application running; Processing modules: <i>Physical\_Activity</i> and <i>Physical\_Sociability</i>; Composition mode: <i>frequency</i> high (15min);}
	\item {\small <b>{Setup 3</b>}: Only core application running; Processing modules: <i>Physical\_Activity</i> and <i>Physical\_Sociability</i>; Composition mode: <i>frequency</i> low (45 min);}
    \item {\small <b>{Setup 4</b>}: Only core application running; Processing modules: <i>Physical\_Activity</i> and <i>Physical\_Sociability</i>; Compose mode: <i>group\_all</i>;}
    \item {\small <b>{Setup 5</b>}: Only core application running; Processing modules: <i>Physical\_Activity</i> and <i>Physical\_Sociability</i>; Compose mode: <i>send\_when\_it\_arrives</i>.}
\end{enumerate}

The results obtained show that the variation in energy consumption was small between the modes of composition of digital phenotypes for a period of 10 hours, as can be seen in Figure~\ref{fig:aval1}a. In us setups 2 and 3, there was a consumption of 8\% of the battery (3000 mAh) for the interval that the phenotypes are distributed every 15 minutes and 7\% for 45 minutes. In compositing mode <i>group\_all</i>, there was 10\% power consumption, and 13\% in <i>send\_when\_it\_arrives</i>. Comparing the three modes of phenotype composition with each other, we observed that there was a higher consumption for the mode <i>send\_when\_it\_arrives</i>, as there is an increase in the frequency of data distribution to the  broker external. For digital phenotype composite mode <i>send\_when\_it\_arrives</i>, all information that arrives at the <i>PhenotypeComposer</i> it's distributed to the broker immediately. Therefore, among the three composition modes, it is the one that consumes the most energy. In phenotype composition mode <i>group\_all</i>, the frequency is based on the data processor's data generation frequency: the rule for distributing the data in the <i>PhenotypeComposer</i> it's satisfied when all the data from the active modules arrives. Now for the mode <i>frequency</i>, the higher the frequency of data distribution for the broker, the greater the energy consumption.

<h5 align="center">
  <img alt="Arquitetura-framework" title="#Results of experimental evaluations." src="/aval1.png"/>
		</h5>
	
	
<h5 align="center">
	<CAPTION>Fig.5 Results of experimental evaluations.<CAPTION>
</h5>

### Adding a Plugin

The second experimental evaluation aimed to analyze the impact of consumption of energy using the plugin to extend the framework. 

The objective of the second evaluation was to
analyze the impact of energy consumption caused by the addition of plugin. The applications of each scenario were also executed for a period of 10 hours. We define the distribution range of the phenotypes in the mode frequency for discharge (every 15 minutes). We only use one data processing module (<i>Physical\_Activity</i>) at the <i>plugin</i>, with no module in the core. In this way, we have four setups in the second evaluation, as seen below. We emphasize that we carried out experiments with the <i>group\_all</i>, and the results were similar to the way <i>send\_when\_it\_arrives</i>. As we only use one processing module, the compositing mode <i>group\_all</i> works in the same way as <i>send\_when\_it\_arrives</i>.

\begin{enumerate}
    \item {\small <b>{Setup 1</b>}: Core running <i>Physical\_Activity</i>, without the plugin; Composition mode: <i>frequency</i> high (15min);}
    
     \item {\small <b>{Setup 2</b>}: Core without processing module and plugin running <i>Physical\_Activity</i>; Composition mode: <i>frequency</i> high (15 min);}
    
     \item {\small <b>{Setup 3</b>}: Core running <i>Physical\_Activity</i>, without the plugin; Compose mode: <i>send\_ when\_it\_arrives</i>;}
    
     \item {\small <b>{Setup 4</b>}: Core without processing module and plugin running <i>Physical\_Activity</i>; Compose mode: <i>send\_when\_it\_arrives</i>.}
\end{enumerate}

The results of the second evaluation show that there was a difference in energy consumption with the addition of plugin. In the Figure~\ref{fig:aval1}b we can observe the setups 1 and 2 with mode <i>frequency</i>, in which the energy consumption was higher using the plugin than the core with 9\% and 5\%, respectively. We believe that energy consumption with the use of plugin was higher because there are two applications running on the smartphone with services running in the foreground. The same happens in setups 3 and 4 with 8\% power consumption using only the core and 10\% with the inclusion of plugin. For these two scenarios, the compositing mode was <i>send\_when\_it\_arrives</i>.

### Enabling Secure Communication

The third evaluation aimed to analyze the consumption when activating the security of the framework.

The objective of the third evaluation was to show the impact on energy consumption when activating the security feature provided in the framework. For that, we use the core used two data processing modules (<i>Physical\_Activity</i> and <i>Physical\_Sociability</i>) with the same settings. We select the mode of composition of digital phenotype's <i>frequency</i> high (distributing data every 15 minutes). This mode was chosen due to ensure that the same amount of data would be distributed across both setups, which are presented below.

\begin{enumerate}
    \item {\small <b>{Setup 1</b>}: Only core application running; Processing modules: <i>Physical\_Activity</i> and <i>Physical\_Sociability</i>; Composition mode: <i>frequency</i> high (15 min); <b>No security};}
    \item {\small <b>{Setup 2</b>}: Only core application running; Processing modules: <i>Physical\_Activity</i> and <i>Physical\_Sociability</i>; Composition mode: <i>frequency</i> high (15 min); <b>Safely</b>.}
\end{enumerate}

The results show that there is not a big difference when using the security feature provided by the framework, as shown in Figure~\ref{fig:aval1}c. Consumption was 8\% in the setup 1 (no security), and 10\% in the second experiment using security: a 2\% difference in energy consumption impact. We believe that this difference in consumption of 2\% was due to the message encryption process, since every message sent from the core to broker pass is encrypted. To encrypt, there is additional processing and the size of the transmitted message is increased.

These results show the cost related to battery consumption when activating the security feature, which was not high. Thus, we believe that the use of security in the framework is not a fact that can compromise the use of a digital phenotyping application developed with the <i>OpenDPMH</i>. Therefore, the developer can use this feature to ensure data distribution protection.

![](header.png)


Project-status
=================

<h4 align="center"> 
	��  Framework - finished...  ��
</h4>

### Features

- [x] DPManager
- [x] ProcessorManager
- [x] DataProcessor
- [x] PhenotypeComposer
- [x] RawDataCollector


Release-history
=================

* 1.0
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


Made by Jean Pablo ���� Contact!

[![Twitter](https://img.shields.io/twitter/url?label=%40jeancomp&style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)
[![Linkedin Badge](https://img.shields.io/badge/-Jean-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/jean-pablo-marques-mendes/)](https://www.linkedin.com/in/jean-pablo-marques-mendes/) 
[![Gmail Badge](https://img.shields.io/badge/-jeancomp@gmail.com-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:jeancomp@gmail.com)](mailto:jeancomp@gmail.com)

