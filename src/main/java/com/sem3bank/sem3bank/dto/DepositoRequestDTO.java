package com.sem3bank.sem3bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositoRequestDTO {
    private Long idUsuario;
    private Double valorDeposito;
}
