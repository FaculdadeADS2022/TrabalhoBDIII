package com.sem3bank.sem3bank.controller;

import com.sem3bank.sem3bank.config.JwtTokenUtil;
import com.sem3bank.sem3bank.model.JwtRequest;
import com.sem3bank.sem3bank.model.JwtResponse;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.model.Wallet;
import com.sem3bank.sem3bank.repository.WalletRepository;
import com.sem3bank.sem3bank.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private WalletRepository walletRepository;

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("Usuario desabilitado", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciais invalidas", e);
        } catch (Exception e) {
            throw new Exception("Erro ao autenticar", e);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> SaveUser(@RequestBody User user) throws Exception {

        //Cria carteira para o novo usuário.
            Wallet wallet = new Wallet();
            wallet.setSaldo(0.00);
            walletRepository.save(wallet);

        //Define a carteira para o novo usuário.
            user.setCarteira(wallet);

        return ResponseEntity.ok(userDetailsService.save(user));
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {
        authenticate(request.getUsername(), request.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
