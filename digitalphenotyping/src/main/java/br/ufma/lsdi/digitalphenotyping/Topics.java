package br.ufma.lsdi.digitalphenotyping;

public enum Topics {
    ADD_PLUGIN_TOPIC("addplugin"),
    REMOVE_PLUGIN_TOPIC("removeplugin"),
    START_PROCESSOR_TOPIC("startprocessor"),
    STOP_PROCESSOR_TOPIC("stoprocessor"),
    ACTIVE_DATA_PROCESSORS_LIST_TOPIC("activedataprocessorlist"),
    ACTIVE_SENSOR_TOPIC("activesensor"),
    DEACTIVATE_SENSOR_TOPIC("deactivatesensor"),
    INFERENCE_TOPIC("inference");

    private final String text;

    Topics(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}