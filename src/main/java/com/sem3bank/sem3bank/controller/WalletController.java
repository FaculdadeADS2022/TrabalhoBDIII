package com.sem3bank.sem3bank.controller;

import com.sem3bank.sem3bank.Listener.WalletListener;
import com.sem3bank.sem3bank.dto.DepositoRequestDTO;
import com.sem3bank.sem3bank.dto.SaqueRequestDTO;
import com.sem3bank.sem3bank.dto.TransferenciaRequestDTO;
import com.sem3bank.sem3bank.model.TipoMovimentacao;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.model.Wallet;
import com.sem3bank.sem3bank.repository.UserRepository;
import com.sem3bank.sem3bank.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/carteira")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletListener walletListener;

    @PostMapping("/deposito")
    public ResponseEntity<String> realizarDeposito(@RequestBody DepositoRequestDTO request) {
        // Primeiro, obtenha o usuário pelo idUsuario
        Optional<User> userOptional = userRepository.findById(request.getIdUsuario());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Em seguida, obtenha a carteira do usuário
            Wallet wallet = user.getCarteira();

            if (wallet != null) {
                double novoSaldo = wallet.getSaldo() + request.getValorDeposito();
                wallet.setSaldo(novoSaldo);
                walletRepository.save(wallet);

                // Criar movimentação de depósito
                walletListener.createMovimentation(wallet, TipoMovimentacao.DEPOSITO, request.getValorDeposito(), "Depósito na carteira");

                return ResponseEntity.ok("Depósito realizado com sucesso.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("O usuário não possui uma carteira associada.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/saque")
    public ResponseEntity<String> realizarSaque(@RequestBody SaqueRequestDTO request) {
        // Obtenha a carteira do usuário pelo idUsuario
        Optional<Wallet> walletOptional = walletRepository.findById(request.getIdUsuario());

        if (walletOptional.isPresent()) {
            Wallet wallet = walletOptional.get();
            double saldoAtual = wallet.getSaldo();
            double valorSaque = request.getValorSaque();

            if (saldoAtual >= valorSaque) {
                double novoSaldo = saldoAtual - valorSaque;
                wallet.setSaldo(novoSaldo);
                walletRepository.save(wallet);

                // Criar movimentação de saque
                walletListener.createMovimentation(wallet, TipoMovimentacao.SAQUE, valorSaque, "Saque da carteira");

                return ResponseEntity.ok("Saque realizado com sucesso.");
            } else {
                return ResponseEntity.badRequest().body("Saldo insuficiente.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transferencia")
    public ResponseEntity<String> realizarTransferencia(@RequestBody TransferenciaRequestDTO request) {
        // Obtenha as carteiras de origem e destino pelo idUsuario
        Optional<Wallet> walletOrigemOptional = walletRepository.findById(request.getIdUsuarioOrigem());
        Optional<Wallet> walletDestinoOptional = walletRepository.findById(request.getIdUsuarioDestino());

        if (walletOrigemOptional.isPresent() && walletDestinoOptional.isPresent()) {
            Wallet walletOrigem = walletOrigemOptional.get();
            Wallet walletDestino = walletDestinoOptional.get();
            double valorTransferencia = request.getValorTransferencia();

            if (walletOrigem.getSaldo() >= valorTransferencia) {
                // Realize o saque na carteira de origem
                double novoSaldoOrigem = walletOrigem.getSaldo() - valorTransferencia;
                walletOrigem.setSaldo(novoSaldoOrigem);
                walletRepository.save(walletOrigem);

                // Crie a movimentação de saída da transferência
                walletListener.createMovimentation(walletOrigem, TipoMovimentacao.TRANSFERENCIA, valorTransferencia, "Transferência para outra carteira");

                // Realize o depósito na carteira de destino
                double novoSaldoDestino = walletDestino.getSaldo() + valorTransferencia;
                walletDestino.setSaldo(novoSaldoDestino);
                walletRepository.save(walletDestino);

                // Crie a movimentação de entrada da transferência
                walletListener.createMovimentation(walletDestino, TipoMovimentacao.TRANSFERENCIA, valorTransferencia, "Transferência recebida de outra carteira");

                return ResponseEntity.ok("Transferência realizada com sucesso.");
            } else {
                return ResponseEntity.badRequest().body("Saldo insuficiente para a transferência.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
