package com.sem3bank.sem3bank.model;

public enum TipoMovimentacao {
    DEPOSITO("Depósito"),
    TRANSFERENCIA("Transferência"),
    SAQUE("Saque");

    private final String descricao;

    TipoMovimentacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}