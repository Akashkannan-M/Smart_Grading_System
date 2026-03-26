package com.smartgrading.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import com.smartgrading.repository.MarkRepository;
import com.smartgrading.repository.UserRepository;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam String type) {
        // Implement OpenPDF generation here
        // This is a stub for the PDF implementation
        byte[] pdfContent = "Dummy PDF Content".getBytes();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "report.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }
}
