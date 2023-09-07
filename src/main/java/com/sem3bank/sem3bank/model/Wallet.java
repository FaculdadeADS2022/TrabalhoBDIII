package com.sem3bank.sem3bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "carteira")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            mappedBy     = "carteira",
            targetEntity = User.class,
            fetch        = FetchType.LAZY,
            cascade      = CascadeType.ALL)
    @JsonIgnore
    private User usuario;

    @Column(name = "saldo")
    @JsonIgnore
    private Double saldo;

}
