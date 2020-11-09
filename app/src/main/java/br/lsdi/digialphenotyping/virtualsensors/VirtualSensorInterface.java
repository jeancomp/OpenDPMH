package br.lsdi.digialphenotyping.virtualsensors;

// Classe responsável por registrar o sensor virtual
public interface VirtualSensorInterface {

    // Método responsável por registrar o sensor virtual no sistema
    Boolean registerListener(SensorEventListener sensorEventListener);

    // Método responsável por tirar dos registros o sensor virtual cadastrado, não é mais identificado como sensor
    Boolean unregisterListener();

    // Método responsável por iniciar a coleta dos dados dos sensores virtuais
    Boolean startCollecting();

    // Método responsável por interromper a coleta dos dados,
    Boolean stopCollecting();
}
