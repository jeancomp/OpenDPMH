package br.ufma.lsdi.digitalphenotyping.dp.handlingexceptions;

public class InvalidPasswordException extends Exception{
    public InvalidPasswordException(String message){
        super(message);
    }
}
