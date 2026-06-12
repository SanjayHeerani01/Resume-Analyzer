package com.resume.resume_analizer.ResumeDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ExtractResponseDTO {

    private String fileName;
    private String extractedText;
}
