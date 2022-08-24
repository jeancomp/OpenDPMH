package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;

public class DigitalPhenotype {
    public List<Situation> situationList = new ArrayList();

    public DigitalPhenotype(){}

    public List<Situation> getSituationList(){
        return situationList;
    }

    public void setSituationList(Situation situation){
        situationList.add(situation);
    }
}
