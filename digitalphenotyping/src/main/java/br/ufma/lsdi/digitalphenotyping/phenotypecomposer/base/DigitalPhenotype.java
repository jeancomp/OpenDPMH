package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

import java.util.Date;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base.ContextAttribute;

public abstract class DigitalPhenotype {
    private long id;
    private String uid;
    private Date datetime;
    private String type;
    private List<ContextAttribute> contextAttribute;
}