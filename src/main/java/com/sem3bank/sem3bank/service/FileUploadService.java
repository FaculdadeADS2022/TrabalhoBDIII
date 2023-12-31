package com.sem3bank.sem3bank.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class FileUploadService {

    @Value("${FILEIO_API_URL}")
    private String fileIoApiUrl;

    public String uploadFileAndGetPublicLink(byte[] fileBytes, String fileName) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        headers.add("Authorization", "Bearer BEOHMYC.NA1Y16K-H63MWJ2-N331FYM-JYZQSFA");

        body.add("file", new HttpEntity<>(fileBytes, headers));
        body.add("expires", new HttpEntity<>("1d", headers));
        body.add("maxDownloads", new HttpEntity<>("2", headers));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(fileIoApiUrl, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Erro ao fazer upload do arquivo para File.io");
        }
    }
}

