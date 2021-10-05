package br.ufma.lsdi.digitalphenotyping;

public enum Topics {
    ADD_PLUGIN_TOPIC("addplugin"),
    REMOVE_PLUGIN_TOPIC("removeplugin"),
    START_PROCESSOR_TOPIC("startprocessor"),
    STOP_PROCESSOR_TOPIC("stoprocessor"),
    ACTIVE_PROCESSOR_TOPIC("activeProcessors"),
    DEACTIVATE_PROCESSOR_TOPIC("deactivateprocessor"),
    ACTIVE_SENSOR_TOPIC("activesensor"),
    DEACTIVATE_SENSOR_TOPIC("deactivatesensor"),
    LIST_SENSORS_TOPIC("listsensors"),
    COMPOSITION_MODE_TOPIC("compositionmode"),
    MAINSERVICE_COMPOSITIONMODE_TOPIC("mainservicecompositionmode"),
    INFERENCE_TOPIC("inference"),
    OPENDPMH_TOPIC("opendpmh");

    private final String text;

    Topics(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}