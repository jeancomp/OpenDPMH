package br.lsdi.ufma.digitalphenotyping.model;

import com.lsdi.social.mhealth.model.TimeInterval;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "TIME_INTERVAL")
@Getter @Setter
@NoArgsConstructor
public class TimeIntervalEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="time_interval_id")
    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    @ElementCollection
    @CollectionTable(name="slot", joinColumns=@JoinColumn(name="time_interval_id"))
    @Column(name="slot")
    private List<Integer> slots;
    private Integer timeSeq;

    public TimeIntervalEntity(TimeInterval timeInterval){
        this.startTime = timeInterval.getStartTime();
        this.endTime = timeInterval.getEndTime();
        this.slots = timeInterval.getSlots();
        this.timeSeq = timeInterval.getTimeSeq();
    }
}