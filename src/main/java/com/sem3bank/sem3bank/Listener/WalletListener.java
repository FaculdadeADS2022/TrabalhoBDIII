package com.sem3bank.sem3bank.Listener;

import com.sem3bank.sem3bank.model.Movimentation;
import com.sem3bank.sem3bank.model.TipoMovimentacao;
import com.sem3bank.sem3bank.model.Wallet;
import com.sem3bank.sem3bank.repository.MovimentationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WalletListener {

    private final MovimentationRepository movimentationRepository;

    @Autowired
    public WalletListener(MovimentationRepository movimentationRepository) {
        this.movimentationRepository = movimentationRepository;
    }

    public void createMovimentation(Wallet wallet, TipoMovimentacao tipo, Double valor, String descricao) {
        Movimentation movimentation = new Movimentation();
        movimentation.setUsuario(wallet.getUsuario());
        movimentation.setTipoMovimentacao(tipo);
        movimentation.setValor(valor);
        movimentation.setData(new Date());
        movimentation.setDescricao(descricao);

        movimentationRepository.save(movimentation);
    }
}
