package com.sem3bank.sem3bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "movimentacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Movimentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "usuario")
    @JsonIgnore
    private User usuario;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_movimentacao", nullable = false, length = 7)
    private TipoMovimentacao tipoMovimentacao;

    @Column(name="valor", nullable = false)
    private Double valor;

    @Column(name="data", nullable = false)
    private Date data;

    @Column(name="descricao", nullable = false)
    private String descricao;
}
