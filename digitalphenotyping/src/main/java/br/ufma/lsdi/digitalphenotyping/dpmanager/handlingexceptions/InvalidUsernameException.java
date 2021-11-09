package br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions;

public class InvalidUsernameException extends Exception{
    public InvalidUsernameException(String message){
        super(message);
    }
}
