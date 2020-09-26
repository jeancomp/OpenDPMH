package br.lsdi.ufma.digitalphenotyping.model;

//import com.lsdi.social.mhealth.model.ContextAttribute;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;

//import javax.persistence.*;

import com.lsdi.social.mhealth.model.ContextAttribute;

import java.io.Serializable;


//@Entity
//@Table(name = "CONTEXT_ATTRIBUTE")
//@Getter @Setter
//@NoArgsConstructor
public class ContextAttributeEntity implements Serializable {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name ="context_attribute_id")
    private long id;
    private String label;
    private String description;
    private String contextType;

    public ContextAttributeEntity(ContextAttribute contextAttribute){

        this.label = contextAttribute.getLabel();
        this.description = contextAttribute.getDescription();
        this.contextType = contextAttribute.getContextType();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }
}
