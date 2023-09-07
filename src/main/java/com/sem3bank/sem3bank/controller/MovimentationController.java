package com.sem3bank.sem3bank.controller;

import com.sem3bank.sem3bank.config.JwtTokenUtil;
import com.sem3bank.sem3bank.dto.MovimentationRequestDTO;
import com.sem3bank.sem3bank.model.Movimentation;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.repository.MovimentationRepository;
import com.sem3bank.sem3bank.repository.UserRepository;
import com.sem3bank.sem3bank.service.JwtUserDetailsService;
import com.sem3bank.sem3bank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movimentacao")
public class MovimentationController {

    private final MovimentationRepository movimentationRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final UserService userService;

    @Autowired
    public MovimentationController(
            MovimentationRepository movimentationRepository,
            UserRepository userRepository,
            JwtTokenUtil jwtTokenUtil,
            JwtUserDetailsService jwtUserDetailsService,
            UserService userService
    ) {
        this.movimentationRepository = movimentationRepository;
        this.userRepository = userRepository;
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

    @GetMapping("/{dataInicio}/{dataFim}")
    public ResponseEntity<List<Movimentation>> obterMovimentacao(HttpServletRequest request, @ModelAttribute MovimentationRequestDTO requestDTO) {

        User user = getUserFromToken(request);

        List<Movimentation> movimentations;

        if (user != null) {
            LocalDate dataI = LocalDate.parse(requestDTO.getDataInicio());
            LocalDate dataF = LocalDate.parse(requestDTO.getDataFim());
            movimentations = movimentationRepository.findAllByPeriod(user.getId(), dataI, dataF);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (movimentations != null && !movimentations.isEmpty()) {
            return ResponseEntity.ok(movimentations);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return requestTokenHeader.substring(7);
        }

        return null;
    }
}
