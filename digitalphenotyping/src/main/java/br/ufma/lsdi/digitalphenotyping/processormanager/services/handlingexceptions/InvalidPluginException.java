package br.ufma.lsdi.digitalphenotyping.processormanager.services.handlingexceptions;

public class InvalidPluginException extends Exception{
    public InvalidPluginException(String message){
        super(message);
    }
}
