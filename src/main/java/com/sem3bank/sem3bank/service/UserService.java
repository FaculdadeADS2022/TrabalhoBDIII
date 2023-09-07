package com.sem3bank.sem3bank.service;
import com.sem3bank.sem3bank.config.JwtTokenUtil;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.model.Wallet;
import com.sem3bank.sem3bank.repository.UserRepository;
import com.sem3bank.sem3bank.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public UserService(
            JwtTokenUtil jwtTokenUtil,
            JwtUserDetailsService jwtUserDetailsService,
            UserRepository userRepository,
            WalletRepository walletRepository
    ) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public User getUserFromToken(String jwtToken) {
        String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        return userRepository.findByEmail(userDetails.getUsername());
    }

    public Optional<Wallet> getUserWallet(User user) {
        return walletRepository.findById(user.getId());
    }
}