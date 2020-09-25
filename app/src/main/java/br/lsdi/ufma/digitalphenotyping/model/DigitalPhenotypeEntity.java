package br.lsdi.ufma.digitalphenotyping.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DIGITAL_PHENOTYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
@NoArgsConstructor
public abstract class DigitalPhenotypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="digital_phenotype_id")
    Long id;

    private String uid;
    private String eventType;

    private LocalDateTime datetime;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "phenotype_context",
            joinColumns = { @JoinColumn(name = "digital_phenotype_id") },
            inverseJoinColumns = { @JoinColumn(name = "context_attribute_id") }
    )
    private List<ContextAttributeEntity> contextAttributeEntities = new ArrayList<>();

    @Transient
    private String contextAttribute;


}
