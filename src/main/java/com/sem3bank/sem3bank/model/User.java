package com.sem3bank.sem3bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="primeiro_nome", nullable = false, length = 75)
    private String nome;

    @Column(name="email", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name="username", nullable = false)
    private String username;

    @Column(name="senha", nullable = false, length = 100)
    private String senha;

    @Column(name="cpf", nullable = false, length = 11)
    private String cpf;

    @OneToOne()
    @JoinColumn(name = "carteira")
    private Wallet carteira;

    @OneToMany(
            mappedBy     = "usuario",
            targetEntity = Movimentation.class,
            fetch        = FetchType.LAZY,
            cascade      = CascadeType.ALL)
    @JsonIgnore
    private Set<Movimentation> movimentacao;

}
