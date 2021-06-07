# Behavior Viewer: Framework para Facilitar o Desenvolvimento de Aplicações de Fenotipagem Digital na Saúde Mental
> Um framework focado em identificar padrões de comportamento de indivíduos monitorados através da coleta de dados de sensores físicos e virtuais.


[![GitHub issues](https://img.shields.io/github/issues/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/issues)
[![GitHub forks](https://img.shields.io/github/forks/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/network)
[![GitHub stars](https://img.shields.io/github/stars/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/stargazers)
[![GitHub license](https://img.shields.io/github/license/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1)
[![Twitter](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Fgithub.com%2Fjeancomp%2Ffenotipagem_digital_saude_vs_0_1)

O objetivo geral deste trabalho é fornecer um framework focado em Fenotipagem Digital de Saúde Mental (do inglês, Digital Phenotyping of Mental Health - DPMH). A solução vai facilitar o desenvolvimento de aplicações móveis que possam coletar dados de contexto passivamente, processando-os e gerando informações de alto nível. Portanto, este framework visa criar uma base de software para suportar a implementação de soluções que visem realizar o reconhecimento de padrões de comportamentos e hábitos dos usuários, os quais podem dar subsídios aos profissionais de saúde mental em suas análises, diagnósticos e tratamentos.

<h1 align="center">
  <img alt="Arquitetura-framework" title="#Arquitetura" src="framework.png" />
</h1>

Componentes:
* DigitalPhenotypingManager: responsável por gerenciar o framework (e.g., start, stop).
* BusSytem: barramento responsável por garantir a troca de mensagens entres os componentes do framework.
* InferenceProcessorManager: gerencia os rawcontextdataprocessor (e.g., start rawcontextdataprocessor, stop rawcontextdataprocessor).
* RawContextDataProcessor: detecta eventos comportamentais dos usuários monitorados (e.g., sociabilidade, mobilidade, sono, atividade física).
* ContextDataProvider: responsável por receber os dados dos sensores físicos e virtuais, além de gerenciá-los (e.g., start sensor, stop sensor)
* PhenotypeCompose: compõe fentótipos digitais dos usuários, recendo diretamente do rawcontextdataprocessor os eventos detectados.
* DataController: gerencia a privacidade e controle dos dados, libera acesso aos plugins para se comunicarem com o framework.

Sumário
=================
<!--ts-->
   * [Instalação](#Instalação)
   * [Exemplo de uso](#Exemplo-de-uso)
   * [Status do projeto](#Status-do-projeto)
   * [Histórico de lançamento](#Histórico-de-Lançamento)
   * [Como usar](#como-usar)
      * [Pre Requisitos](#Pré-requisitos)
      * [Local files](#local-files)
      * [Remote files](#remote-files)
      * [Multiple files](#multiple-files)
      * [Combo](#combo)
   * [Licença](#Licença)
   * [Contribuição](#Contribuição)
   * [Autor](#Autor)
<!--te-->

![](header.png)


Instalação
=================

Linux & Windows:

```sh
1º opção (projeto github): faça o download do projeto zip, descompacta-o, depois abra com no Android Studio "Open an Existing Project", pronto.
```
```sh
2º opção (arquivos aar): em construção... 
```
```sh
3º opção (apk): em construção... 
```

Exemplo-de-uso
=================

Em andamento (prints de tela).


Status-do-projeto
=================

<h4 align="center"> 
	🚧  Framework - Em desenvolvimento...  🚧
</h4>

### Features

- [x] DiditalPhenotypingManager
- [x] InferenceProcessorManager
- [x] ContextDataProvider
- [x] BusSystem
- [ ] PhenotypeComposer
- [ ] DataController
- [ ] RawContextDataProcessor


Histórico-de-Lançamento
=================

* 0.2.1
    * -
* 0.2.0
    * -
* 0.1.1
    * -
* 0.1.0
    * Adaptação de segurança (uso certificados digitais), em progresso.
* 0.0.1
    * Adaptação dos sensores virtuais ao M-HUB

Como-usar
=================

Pré-requisitos
-----
* Versão android: 6
* Versão API android: minSdkVersion > 26


Licença
=================

Your Name – [@Twitter](https://twitter.com/jeancomp) – jean.marques@lsdi.ufma.br

Distributed under the XYZ license. See ``LICENSE`` for more information.

[https://github.com/](https://github.com/jeancomp)

Contribuição
=================

1. Fork it (<https://github.com/yourname/yourproject/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request

<!-- Markdown link & img dfn's -->
[npm-image]: https://img.shields.io/npm/v/datadog-metrics.svg?style=flat-square
[npm-url]: https://npmjs.org/package/datadog-metrics
[npm-downloads]: https://img.shields.io/npm/dm/datadog-metrics.svg?style=flat-square
[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg?style=flat-square
[travis-url]: https://travis-ci.org/dbader/node-datadog-metrics
[wiki]: https://github.com/yourname/yourproject/wiki


Autor
=================

<a href="https://github.com/jeancomp">
       <a href="https://imgbb.com/"><img src="https://i.ibb.co/MsLwGfj/jp.jpg" alt="jp" border="0" width="80px;" /></a>
 <br />
 <sub><b>Jean Pablo</b></sub></a>


Feito por Jean Pablo 👋🏽 Entre em contato!

[![Twitter](https://img.shields.io/twitter/url?label=%40jeancomp&style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)
[![Linkedin Badge](https://img.shields.io/badge/-Jean-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/jean-pablo-marques-mendes/)](https://www.linkedin.com/in/jean-pablo-marques-mendes/) 
[![Gmail Badge](https://img.shields.io/badge/-jeancomp@gmail.com-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:jeancomp@gmail.com)](mailto:jeancomp@gmail.com)

