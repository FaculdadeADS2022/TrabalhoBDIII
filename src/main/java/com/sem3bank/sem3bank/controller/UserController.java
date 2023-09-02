package com.sem3bank.sem3bank.controller;

import com.sem3bank.sem3bank.exception.ResourceNotFoundException;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.model.Wallet;
import com.sem3bank.sem3bank.repository.UserRepository;
import com.sem3bank.sem3bank.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("/usuarios")
    public List<User> getAllUsers(){
        return repository.findAll();
    }
    
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException{
            User user = repository.findById(userId).orElseThrow(() ->
                    new ResourceNotFoundException("Usuário não encontrado para este ID: " + userId));
            return ResponseEntity.ok().body(user);
    }
    
    @GetMapping("/usuarios/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable(value = "email") String userEmail){
        User user = repository.findByEmail(userEmail);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/usuarios")
    public User createUser(@Validated @RequestBody User user){

        //Cria carteira para o novo usuário.
            Wallet wallet = new Wallet();
            wallet.setSaldo(0.00);
            walletRepository.save(wallet);

        //Define a carteira para o novo usuário.
            user.setCarteira(wallet);

        return repository.save(user);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value= "id") Long userId,
                                           @Validated @RequestBody User detalhes)
        throws ResourceNotFoundException{
        User user = repository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado para este ID: " + userId));
        user.setNome(detalhes.getNome());
        user.setEmail(detalhes.getEmail());
        user.setSenha(detalhes.getSenha());
        user.setCpf(detalhes.getCpf());
        user.setCarteira(detalhes.getCarteira());
        final User updateUser = repository.save(user);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/usuarios/{id}")
    public Map<String, Boolean> deleteUser(
            @PathVariable(value = "id") Long userId
    ) throws Exception {
        User user = repository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("Usuario nao encontrado para este ID: " + userId));
        repository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}