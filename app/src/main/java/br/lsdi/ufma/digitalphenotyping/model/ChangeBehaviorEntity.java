package br.lsdi.ufma.digitalphenotyping.model;

import com.lsdi.social.mhealth.model.NotifyChangeBehavior;
import com.lsdi.social.mhealth.model.TimeInterval;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CHANGE_BEHAVIOR")
@Getter @Setter
@NoArgsConstructor
public class ChangeBehaviorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="change_behavior_id")
    private Long id;

    private LocalDateTime date;

    private String eventType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "context_attribute_id")
    private ContextAttributeEntity contextAttributeEntity;

    private Double similarity;
    private Double defuzzifiedValue;
    private Double isChange;
    private Double isNoChange;
    private Double isModerateChange;
    private String message;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "change_behavior_id")
    private List<TimeIntervalEntity> oldPattern;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "change_behavior_id")
    private List<TimeIntervalEntity> newPattern;

    public ChangeBehaviorEntity(NotifyChangeBehavior notifyChangeBehavior){
        this.date = notifyChangeBehavior.getDate();
        this.eventType = notifyChangeBehavior.getEventType();
        this.contextAttributeEntity = new ContextAttributeEntity(notifyChangeBehavior.getContextAttribute());
        this.similarity = notifyChangeBehavior.getSimilarity();
        this.defuzzifiedValue = notifyChangeBehavior.getDefuzzifiedValue();
        this.isChange = notifyChangeBehavior.getIsChange();
        this.isNoChange = notifyChangeBehavior.getIsNoChange();
        this.isModerateChange = notifyChangeBehavior.getIsModerateChange();
        this.message = notifyChangeBehavior.getMessage();

        oldPattern = new ArrayList<>();
        for (TimeInterval interval : notifyChangeBehavior.getOldPattern()){
            oldPattern.add(new TimeIntervalEntity(interval));
        }

        newPattern = new ArrayList<>();
        for (TimeInterval interval : notifyChangeBehavior.getNewPattern()){
            newPattern.add(new TimeIntervalEntity(interval));
        }

    }

}
