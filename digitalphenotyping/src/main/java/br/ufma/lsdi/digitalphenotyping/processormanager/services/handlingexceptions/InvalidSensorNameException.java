package br.ufma.lsdi.digitalphenotyping.processormanager.services.handlingexceptions;

public class InvalidSensorNameException extends Exception{
    public InvalidSensorNameException(String message){
        super(message);
    }
}
