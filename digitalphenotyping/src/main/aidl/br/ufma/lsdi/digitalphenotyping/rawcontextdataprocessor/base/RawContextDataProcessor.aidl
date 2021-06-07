// RawContextDataProcessor.aidl
package br.ufma.lsdi.digitalphenotyping.rawcontextdataprocessor.base;

// Tipos de dados aceitos:
// int, lon, boolean, float, double, String, List<>, Parcelable

interface RawContextDataProcessor {
    /**
    * Recebe os dados
    *
    */
    void receive(String nameSensor, String startDate, String endDate, int availableAttributes, String dataContext);


    /**
    * Processa os dados
    *
    */
    void processing();


    /**
    * Envia os dados
    *
    */
    String send(String dataContext);

}