package com.sem3bank.sem3bank.service;

import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tenta encontrar pelo email.
            User user = repository.findByUsername(username);

        User user2;

        if (user == null) {
            // Tenta encontrar pelo usuário.
                user2 = repository.findByEmail(username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getSenha(), new ArrayList<>());
        }

        if (user2 == null) {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }
        return new org.springframework.security.core.userdetails.User(user2.getEmail(), user2.getSenha(), new ArrayList<>());
    }

    public User save(User user) {
        User novo = new User();
        novo.setNome(user.getNome());
        novo.setUsername(user.getUsername());
        novo.setEmail(user.getEmail());
        novo.setCpf(user.getCpf());
        novo.setCarteira(user.getCarteira());
        novo.setSenha(bcryptEncoder.encode(user.getSenha()));
        return repository.save(novo);
    }
}
