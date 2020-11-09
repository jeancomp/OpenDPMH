package br.lsdi.digialphenotyping.virtualsensors;

import android.content.Context;

//Aqui vai ter os métodos genéricos
public interface VirtualSensorInterface {
    // Método genérico, verifica se checou nova mensagem de texto, passando o código que identifica o usuário
    void onNewMessageReceived(String activationCode);



//    // Método verifica se a aplicação foi executada, quantas vezes, tempo inicio e tempo fim da app
//    boolean isAppRunning(Context context, String packageName);
}
