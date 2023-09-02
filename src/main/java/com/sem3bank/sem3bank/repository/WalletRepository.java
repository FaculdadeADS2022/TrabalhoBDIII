package com.sem3bank.sem3bank.repository;

import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
