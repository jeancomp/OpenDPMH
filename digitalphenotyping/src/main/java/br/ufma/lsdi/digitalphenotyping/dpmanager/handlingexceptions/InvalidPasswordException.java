package br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions;

public class InvalidPasswordException extends Exception{
    public InvalidPasswordException(String message){
        super(message);
    }
}
