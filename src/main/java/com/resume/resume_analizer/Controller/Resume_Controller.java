package com.resume.resume_analizer.Controller;

import com.resume.resume_analizer.ResumeDTO.ExtractRequestDTO;
import com.resume.resume_analizer.ResumeDTO.ExtractResponseDTO;
import com.resume.resume_analizer.ResumeDTO.JobMatchRequestDTO;
import com.resume.resume_analizer.ResumeDTO.JobMatchResponseDTO;
import com.resume.resume_analizer.Service.GeminiService;
import com.resume.resume_analizer.Service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:8080/")
public class Resume_Controller {


    @Autowired
    private ResumeService resumeService;

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }
        Path filePath = Paths.get(uploadDir + file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
        return ResponseEntity.ok("File uploaded successfully " + file.getOriginalFilename());
    }

    @PostMapping("/extract")
    public ResponseEntity<ExtractResponseDTO> extractResume(
            @RequestBody ExtractRequestDTO requestDTO) throws IOException {

        return ResponseEntity.ok(
                resumeService.extractResume(requestDTO)
        );
    }
    @PostMapping("/ai-analyze")
    public ResponseEntity<String> aiAnalyze(
            @RequestBody JobMatchRequestDTO requestDTO){

        return ResponseEntity.ok(geminiService.analyzeResume(
                requestDTO.getResumeText(),
                requestDTO.getJobDescription()
        ));
    }
    @PostMapping("/write-coverlatter")
    public ResponseEntity<String> writeCoverLatter(
            @RequestBody JobMatchRequestDTO requestDTO){

        return ResponseEntity.ok(geminiService.writeCoverLatter(
                requestDTO.getResumeText(),
                requestDTO.getJobDescription()
        ));
    }
}
