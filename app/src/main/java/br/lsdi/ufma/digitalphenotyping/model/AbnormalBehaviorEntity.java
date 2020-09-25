package br.lsdi.ufma.digitalphenotyping.model;


import com.lsdi.social.mhealth.model.NotifyAbnormalBehavior;
import com.lsdi.social.mhealth.model.TimeInterval;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ABNORMAL_BEHAVIOR")
@Getter @Setter
@NoArgsConstructor
public class AbnormalBehaviorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="abnormal_behavior_id")
    private Long id;

    private LocalDateTime date;

    private String eventType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "context_attribute_id")
    private ContextAttributeEntity contextAttributeEntity;

    private Double similarity;
    private Double defuzzifiedValue;
    private Double isAbnormal;
    private Double isNormal;
    private Double isWarning;
    private String message;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "context_attribute_id")
    private List<TimeIntervalEntity> currentPattern;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "context_attribute_id")
    private List<TimeIntervalEntity> currentBehavior;


    public AbnormalBehaviorEntity(NotifyAbnormalBehavior notifyAbnormalBehavior){
        this.date = notifyAbnormalBehavior.getDate();
        this.eventType = notifyAbnormalBehavior.getEventType();
        this.contextAttributeEntity = new ContextAttributeEntity(notifyAbnormalBehavior.getContextAttribute());
        this.similarity = notifyAbnormalBehavior.getSimilarity();
        this.defuzzifiedValue = notifyAbnormalBehavior.getDefuzzifiedValue();
        this.isAbnormal = notifyAbnormalBehavior.getIsAbnormal();
        this.isNormal = notifyAbnormalBehavior.getIsNormal();
        this.isWarning = notifyAbnormalBehavior.getIsWarning();
        this.message = notifyAbnormalBehavior.getMessage();

        currentPattern = new ArrayList<>();
        for (TimeInterval interval : notifyAbnormalBehavior.getCurrentPattern()){
            currentPattern.add(new TimeIntervalEntity(interval));
        }

        currentBehavior = new ArrayList<>();
        for (TimeInterval interval : notifyAbnormalBehavior.getCurrentBehavior()){
            currentBehavior.add(new TimeIntervalEntity(interval));
        }
    }

}
