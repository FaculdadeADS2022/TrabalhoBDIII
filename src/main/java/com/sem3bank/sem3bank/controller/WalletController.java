package com.sem3bank.sem3bank.controller;

import com.sem3bank.sem3bank.Listener.WalletListener;
import com.sem3bank.sem3bank.config.JwtTokenUtil;
import com.sem3bank.sem3bank.dto.DepositoRequestDTO;
import com.sem3bank.sem3bank.dto.SaqueRequestDTO;
import com.sem3bank.sem3bank.dto.TransferenciaRequestDTO;
import com.sem3bank.sem3bank.model.TipoMovimentacao;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.model.Wallet;
import com.sem3bank.sem3bank.repository.UserRepository;
import com.sem3bank.sem3bank.repository.WalletRepository;
import com.sem3bank.sem3bank.service.JwtUserDetailsService;
import com.sem3bank.sem3bank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/carteira")
public class WalletController {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletListener walletListener;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final UserService userService;

    @Autowired
    public WalletController(
            WalletRepository walletRepository,
            UserRepository userRepository,
            WalletListener walletListener,
            JwtTokenUtil jwtTokenUtil,
            JwtUserDetailsService jwtUserDetailsService,
            UserService userService
    ) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.walletListener = walletListener;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.userService = userService;
    }

    private User getUserFromToken(HttpServletRequest request) {
        String jwtToken = extractJwtTokenFromRequest(request);

        if (jwtToken != null) {
            return userService.getUserFromToken(jwtToken);
        }

        return null;
    }

    @GetMapping("/saldo")
    public ResponseEntity<String> obterSaldo(HttpServletRequest request) {
        User user = getUserFromToken(request);

        if (user != null) {
            Optional<Wallet> walletOptional = userService.getUserWallet(user);

            if (walletOptional.isPresent()) {
                double saldo = walletOptional.get().getSaldo();
                return ResponseEntity.ok(String.format("Seu saldo atual é de: R$%.2f", saldo));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não possui uma carteira associada.");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou usuário não autorizado.");
    }

    @PostMapping("/deposito")
    public ResponseEntity<String> realizarDeposito(HttpServletRequest request, @RequestBody DepositoRequestDTO requestDTO) {
        User user = getUserFromToken(request);

        if (user != null) {
            Optional<User> userOptional = userRepository.findById(user.getId());

            if (userOptional.isPresent()) {
                User currentUser = userOptional.get();
                Wallet wallet = currentUser.getCarteira();

                if (wallet != null) {
                    double valorDeposito = requestDTO.getValorDeposito();
                    double novoSaldo = wallet.getSaldo() + valorDeposito;
                    wallet.setSaldo(novoSaldo);
                    walletRepository.save(wallet);

                    walletListener.createMovimentation(wallet, TipoMovimentacao.DEPOSITO, valorDeposito, "Depósito na carteira");

                    return ResponseEntity.ok("Depósito realizado com sucesso.");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("O usuário não possui uma carteira associada.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou usuário não autorizado.");
    }

    @PostMapping("/saque")
    public ResponseEntity<String> realizarSaque(HttpServletRequest request, @RequestBody SaqueRequestDTO requestDTO) {
        User user = getUserFromToken(request);

        if (user != null) {
            Optional<Wallet> walletOptional = walletRepository.findById(user.getId());

            if (walletOptional.isPresent()) {
                Wallet wallet = walletOptional.get();
                double saldoAtual = wallet.getSaldo();
                double valorSaque = requestDTO.getValorSaque();

                if (saldoAtual >= valorSaque) {
                    double novoSaldo = saldoAtual - valorSaque;
                    wallet.setSaldo(novoSaldo);
                    walletRepository.save(wallet);

                    walletListener.createMovimentation(wallet, TipoMovimentacao.SAQUE, valorSaque, "Saque da carteira");

                    return ResponseEntity.ok("Saque realizado com sucesso.");
                } else {
                    return ResponseEntity.badRequest().body("Saldo insuficiente.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou usuário não autorizado.");
    }

    @PostMapping("/transferencia")
    public ResponseEntity<String> realizarTransferencia(HttpServletRequest request, @RequestBody TransferenciaRequestDTO requestDTO) {
        User user = getUserFromToken(request);

        if (user != null) {
            // Verificar se o usuário está tentando transferir para ele mesmo
                if (user.getId() == requestDTO.getIdUsuarioDestino()) {
                    return ResponseEntity.badRequest().body("Você não pode fazer uma transferência para você mesmo.");
                }

            Optional<Wallet> walletOrigemOptional = walletRepository.findById(user.getId());
            Optional<Wallet> walletDestinoOptional = walletRepository.findById(requestDTO.getIdUsuarioDestino());

            if (walletOrigemOptional.isPresent() && walletDestinoOptional.isPresent()) {
                Wallet walletOrigem = walletOrigemOptional.get();
                Wallet walletDestino = walletDestinoOptional.get();
                User usuarioDestino = userRepository.findById(requestDTO.getIdUsuarioDestino()).get();
                double valorTransferencia = requestDTO.getValorTransferencia();

                if (walletOrigem.getSaldo() >= valorTransferencia) {
                    double novoSaldoOrigem = walletOrigem.getSaldo() - valorTransferencia;
                    walletOrigem.setSaldo(novoSaldoOrigem);
                    walletRepository.save(walletOrigem);

                    walletListener.createMovimentation(walletOrigem,
                            TipoMovimentacao.TRANSFERENCIA,
                            valorTransferencia,
                            "Transferência realizada para carteira: " +
                                    usuarioDestino.getCarteira().getId().toString() +
                                    " - " +
                                    usuarioDestino.getNome());

                    double novoSaldoDestino = walletDestino.getSaldo() + valorTransferencia;
                    walletDestino.setSaldo(novoSaldoDestino);
                    walletRepository.save(walletDestino);

                    walletListener.createMovimentation(walletDestino,
                            TipoMovimentacao.TRANSFERENCIA,
                            valorTransferencia,
                            "Transferência recebida da carteira: " +
                                    user.getCarteira().getId().toString() +
                                    " - " +
                                    user.getNome());

                    return ResponseEntity.ok("Transferência realizada com sucesso.");
                } else {
                    return ResponseEntity.badRequest().body("Saldo insuficiente para a transferência.");
                }
            } else {
                if (walletOrigemOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carteira de origem não encontrada.");
                } else if (walletDestinoOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carteira de destino não encontrada.");
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou usuário não autorizado.");
    }


    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return requestTokenHeader.substring(7);
        }

        return null;
    }
}
