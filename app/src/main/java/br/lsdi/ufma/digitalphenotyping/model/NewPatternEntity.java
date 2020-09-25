package br.lsdi.ufma.digitalphenotyping.model;


import com.lsdi.social.mhealth.model.NotifyNewPattern;
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
@Table(name = "NEW_PATTERN")
@Getter @Setter
@NoArgsConstructor
public class NewPatternEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="new_pattern_id")
    private Long id;

    private LocalDateTime date;

    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.MERGE)
    @JoinColumn(name = "context_attribute_id")
    private ContextAttributeEntity contextAttributeEntity;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "new_pattern_id")
    private List<TimeIntervalEntity> socialPattern;
    //private int[] socialPatternArray;

    public NewPatternEntity(NotifyNewPattern notifyNewPattern){
        this.date = notifyNewPattern.getDate();
        this.eventType = notifyNewPattern.getEventType();
        this.contextAttributeEntity = new ContextAttributeEntity(notifyNewPattern.getContextAttribute());
        socialPattern = new ArrayList<>();
        for (TimeInterval interval : notifyNewPattern.getSocialPattern()){
            socialPattern.add(new TimeIntervalEntity(interval));
        }
    }


}
