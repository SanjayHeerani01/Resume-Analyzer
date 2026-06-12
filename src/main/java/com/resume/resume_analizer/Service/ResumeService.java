package com.resume.resume_analizer.Service;

import com.resume.resume_analizer.ResumeDTO.ExtractRequestDTO;
import com.resume.resume_analizer.ResumeDTO.ExtractResponseDTO;
import com.resume.resume_analizer.ResumeDTO.JobMatchRequestDTO;
import com.resume.resume_analizer.ResumeDTO.JobMatchResponseDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ResumeService {

    public ExtractResponseDTO extractResume(ExtractRequestDTO requestDTO) throws IOException {
        String fileName = requestDTO.getFileName();

        Path path = Paths.get("uploads", fileName);

        if (!Files.exists(path)) {
            throw new RuntimeException("File not found");
        }
        PDDocument document =
                Loader.loadPDF(path.toFile());
        PDFTextStripper stripper =
                new PDFTextStripper();
        String text =
                stripper.getText(document);
        document.close();

        ExtractResponseDTO responseDTO =
                new ExtractResponseDTO();

        responseDTO.setFileName(fileName);
        responseDTO.setExtractedText(text);

        return responseDTO;
    }
}