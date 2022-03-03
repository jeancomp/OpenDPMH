package br.ufma.lsdi.digitalphenotyping.dataprocessor.util;

public class TriggerAlarm2 {
    private boolean dataGenerationFrequency;
    private static TriggerAlarm2 instance = null;

    public TriggerAlarm2() {
    }

    public static TriggerAlarm2 getInstance() {
        if (instance == null) {
            instance = new TriggerAlarm2();
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