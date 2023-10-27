package com.sem3bank.sem3bank.controller;

import com.itextpdf.text.DocumentException;
import com.sem3bank.sem3bank.dto.ExtratoRequestDTO;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.service.ExtractService;
import com.sem3bank.sem3bank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/extrato")
public class ExtractController {

    @Autowired
    private ExtractService extratoService;
    private final UserService userService;

    @Autowired
    public ExtractController (
            UserService userService
    ) {
        this.userService = userService;
    }

    private User getUserFromToken(HttpServletRequest request) {
        String jwtToken = extractJwtTokenFromRequest(request);

        if (jwtToken != null) {
            return userService.getUserFromToken(jwtToken);
        }

        return null;
    }

    @PostMapping("/gerar/{dataQuantidadeDias}")
    public ResponseEntity<String> gerarExtratoPDF(HttpServletRequest request, @ModelAttribute ExtratoRequestDTO extratoRequestDTO) throws IOException, DocumentException {

        LocalDate date = null;
        User user = getUserFromToken(request);

        if (extratoRequestDTO.getDataQuantidadeDias() == 30) {
            date = getDateXDaysAgo(30);
        } else if (extratoRequestDTO.getDataQuantidadeDias() == 60) {
            date = getDateXDaysAgo(60);
        } else if (extratoRequestDTO.getDataQuantidadeDias() == 90) {
            date = getDateXDaysAgo(90);
        }

        try {
            // Use o serviço para gerar o PDF do extrato e obter o link público
            String publicLink = extratoService.gerarExtratoPDFAndGetPublicLink(user, date);

            // Retorne o link público como resposta
            return ResponseEntity.ok(publicLink);
        } catch (IOException | DocumentException | NoSuchAlgorithmException e) {
            // Lide com exceções adequadamente
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor.");
        }
    }

    public LocalDate getDateXDaysAgo(int daysAgo) {
        LocalDate currentDate = LocalDate.now(); // Obtém a data atual
        LocalDate dateXDaysAgo = currentDate.minusDays(daysAgo); // Subtrai dias

        return dateXDaysAgo;
    }

    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return requestTokenHeader.substring(7);
        }

        return null;
    }
}
