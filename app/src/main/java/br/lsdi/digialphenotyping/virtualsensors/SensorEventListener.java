package br.lsdi.digialphenotyping.virtualsensors;

public interface SensorEventListener {

    // Método responsável por enviar os dados para um destinatário
    Object sendData();

    // Método é acionado quando dados de um sensor capturado, um evento aciona esse método
    void receiveData(Object sensor);
}
