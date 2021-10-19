package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;

public class DigitalPhenotype {
    public List<DigitalPhenotypeEvent> dpeList = new ArrayList();

    public DigitalPhenotype(){}

    public List<DigitalPhenotypeEvent> getDigitalPhenotypeEventList(){
        return dpeList;
    }

    public void setDpeList(DigitalPhenotypeEvent digitalPhenotypeEvent){
        dpeList.add(digitalPhenotypeEvent);
    }
}
