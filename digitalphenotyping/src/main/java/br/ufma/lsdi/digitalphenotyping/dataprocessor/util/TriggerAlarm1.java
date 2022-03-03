package br.ufma.lsdi.digitalphenotyping.dataprocessor.util;

public class TriggerAlarm1 {
    private boolean dataGenerationFrequency;
    private static TriggerAlarm1 instance = null;

    public TriggerAlarm1() {
    }

    public static TriggerAlarm1 getInstance() {
        if (instance == null) {
            instance = new TriggerAlarm1();
        }
        return instance;
    }

    public void set(boolean value) {
        dataGenerationFrequency = value;
    }

    public boolean get(){
        return dataGenerationFrequency;
    }
}