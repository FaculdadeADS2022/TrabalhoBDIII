package com.sem3bank.sem3bank.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.sem3bank.sem3bank.config.JwtTokenUtil;
import com.sem3bank.sem3bank.model.Movimentation;
import com.sem3bank.sem3bank.model.User;
import com.sem3bank.sem3bank.repository.MovimentationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class ExtractService {

    private final FileUploadService fileUploadService;
    private final MovimentationRepository movimentationRepository;

    @Autowired
    public ExtractService (
            FileUploadService fileUploadService,
            MovimentationRepository movimentationRepository
    ) {
        this.fileUploadService = fileUploadService;
        this.movimentationRepository = movimentationRepository;
    }

    public String gerarExtratoPDFAndGetPublicLink(User user, LocalDate dataExtrato)
            throws IOException, DocumentException, NoSuchAlgorithmException {

        // Define o nome do arquivo com base no CPF do usuário
        String pdfFileName = user.getCpf().substring(0, 5) + "_extrato.pdf";

        // Configura a proteção por senha
        String userCpf = user.getCpf().substring(0, 5);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.setEncryption(userCpf.getBytes(), null, PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);

        document.open();

        // Busca movimentações para adicionar no PDF
        Date actualDate = new Date();
        Instant instant = actualDate.toInstant();
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        List<Movimentation> movimentations = movimentationRepository.findAllByPeriod(user.getId(), dataExtrato, localDate);

        // Adicione conteúdo ao PDF (texto, tabelas, etc.)
        document.add(new Paragraph("Extrato Bancário"));

        // Adicione as movimentações ao PDF
        for (Movimentation movimentation : movimentations) {
            document.add(new Paragraph("Data: " + movimentation.getData()));
            document.add(new Paragraph("Descrição: " + movimentation.getDescricao()));
            document.add(new Paragraph("Valor: " + movimentation.getValor()));
            // Adicione outras informações da movimentação conforme necessário
            document.add(new Paragraph("")); // Adicione uma linha em branco entre as movimentações
        }

        // Feche o documento
        document.close();

        // Converta o PDF em bytes
        byte[] pdfBytes = outputStream.toByteArray();

        // Faça o upload do arquivo PDF e obtenha o link público
        String publicLink = fileUploadService.uploadFileAndGetPublicLink(pdfBytes, pdfFileName);

        return publicLink;
    }
}
