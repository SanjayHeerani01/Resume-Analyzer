package com.resume.resume_analizer.ResumeDTO;

import lombok.Data;

@Data
public class JobMatchRequestDTO {

    private String resumeText;
    private String jobDescription;
}
