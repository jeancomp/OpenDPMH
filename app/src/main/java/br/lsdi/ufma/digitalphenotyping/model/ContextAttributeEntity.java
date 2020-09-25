package br.lsdi.ufma.digitalphenotyping.model;

import com.lsdi.social.mhealth.model.ContextAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "CONTEXT_ATTRIBUTE")
@Getter @Setter
@NoArgsConstructor
public class ContextAttributeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="context_attribute_id")
    private long id;
    private String label;
    private String description;
    private String contextType;

    public ContextAttributeEntity(ContextAttribute contextAttribute){

        this.label = contextAttribute.getLabel();
        this.description = contextAttribute.getDescription();
        this.contextType = contextAttribute.getContextType();
    }


}
