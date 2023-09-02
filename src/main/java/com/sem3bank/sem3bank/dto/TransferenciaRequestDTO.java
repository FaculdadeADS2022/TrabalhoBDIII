package com.sem3bank.sem3bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferenciaRequestDTO {
    private Long idUsuarioOrigem;
    private Long idUsuarioDestino;
    private Double valorTransferencia;
}

