package com.sem3bank.sem3bank.dto;

import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UsuariosDTO {
    private Long idUsuario;
    private String nome;
    private Wallet wallet;

    public UsuariosDTO(User user) {
        this.idUsuario = user.getId();
        this.nome = user.getNome();
        this.wallet = user.getCarteira();
    }
}
