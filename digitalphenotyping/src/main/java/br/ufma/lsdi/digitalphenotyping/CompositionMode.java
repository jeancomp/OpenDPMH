package br.ufma.lsdi.digitalphenotyping;

public enum CompositionMode {
    /**
     * Send the Phenotype to the server as soon as it arrives.
     */
    SEND_WHEN_IT_ARRIVES,

    /**
     * Groups all phenotypes, then sends them to the server.
     */
    GROUP_ALL,

    /**
     * Sets a frequency where phenotypes will be sent to the server.
     */
    FREQUENCY
}
