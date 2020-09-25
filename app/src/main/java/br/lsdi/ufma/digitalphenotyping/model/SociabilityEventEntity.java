package br.lsdi.ufma.digitalphenotyping.model;

import com.lsdi.social.mhealth.model.ContextAttribute;
import com.lsdi.social.mhealth.model.SociabilityEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "SOCIABILITY_EVENT")
@PrimaryKeyJoinColumn(name="digital_phenotype_id")
@Getter @Setter
@NoArgsConstructor
public class SociabilityEventEntity extends DigitalPhenotypeEntity implements Serializable {

    private String sociabilityType;

    private LocalDateTime endTime;

    private Double duration;

    public SociabilityEventEntity(SociabilityEvent sociabilityEvent){
        this.setUid(sociabilityEvent.getUid());
        this.setDatetime(sociabilityEvent.getDatetime());
        this.setEventType(sociabilityEvent.getEventType());
        this.sociabilityType = sociabilityEvent.getSociabilityType();
        this.endTime = sociabilityEvent.getEndTime();
        this.duration = sociabilityEvent.getDuration();
        for (ContextAttribute ctx : sociabilityEvent.getContextAttributes()){
        getContextAttributeEntities().add(new ContextAttributeEntity(ctx));
        }
    }

}
