package br.lsdi.digialphenotyping.virtualsensors;

// Classe responsável por registrar o sensor virtual
public interface VirtualSensorInterface {

    // Método responsável por registrar o sensor virtual no sistema
    public boolean registerListener();

    // Método responsável por tirar dos registros o sensor virtual cadastrado, não é mais identificado como sensor
    public boolean unregisterListener();

    // Método responsável por iniciar a coleta dos dados dos sensores virtuais
    public boolean startCollecting();

    // Método responsável por interromper a coleta dos dados,
    public boolean stopCollecting();
}
