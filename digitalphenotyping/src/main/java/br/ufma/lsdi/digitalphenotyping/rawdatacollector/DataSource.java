package br.ufma.lsdi.digitalphenotyping.rawdatacollector;

public class DataSource {
    private String name;
    private Integer samplingRate=0;
    private String epl;

    public DataSource(String name, String epl){
        this.name = name;
        this.epl = epl;
    }

    public DataSource(String name, Integer samplingRate, String epl){
        this.name = name;
        this.samplingRate = samplingRate;
        this.epl = epl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSamplingRate(){
        return samplingRate;
    }

    public void setSamplingRate(Integer samplingRate){
        this.samplingRate = samplingRate;
    }

    public String getEpl() {
        return epl;
    }

    public void setEpl(String epl) {
        this.epl = epl;
    }
}
