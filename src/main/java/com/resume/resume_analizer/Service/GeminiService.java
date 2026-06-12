package com.resume.resume_analizer.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private String callGroq(String prompt) {
        Map<String, Object> requestBody = Map.of(
            "model", "llama-3.3-70b-versatile",
            "messages", List.of(
                    Map.of("role", "user", "content", prompt)
            )
    );

    String response = webClient.post()
            .uri("https://api.groq.com/openai/v1/chat/completions")
            .header("Authorization", "Bearer " + apiKey.trim())
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    return extractContent(response);
}

    private final WebClient webClient = WebClient.builder().build();

    public String analyzeResume(String resumeText, String jobDescription) {

        Map<String, Object> requestBody = getStringObjectMap(resumeText, jobDescription);

        String response = webClient.post()
                .uri("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey.trim())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractContent(response);
    }

    private static Map<String, Object> getStringObjectMap(String resumeText, String jobDescription) {
        String prompt = """
                You are an ATS Resume Analyzer.

                Analyze the resume against the job description.

                Return:
                1. ATS Score out of 100
                2. Matched Skills
                3. Missing Skills
                4. Improvement Suggestions

                Resume:
                %s

                Job Description:
                %s
                """.formatted(resumeText, jobDescription);

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );
        return requestBody;
    }

    private String extractContent(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {
            return "Failed to parse AI response: " + e.getMessage();
        }
    }

    public String generateInterviewQuestions(String resumeText, String jobDescription) {
        String prompt = """
                You are an expert technical interviewer.
                Based on the resume and job description below, generate 10 relevant interview questions.
                Include a mix of technical, behavioral, and role-specific questions.
                Format each question with a number and a brief note on what it tests.

                Resume:
                %s

                Job Description:
                %s
                """.formatted(resumeText, jobDescription);

        return callGroq(prompt);
    }

    public String writeCoverLatter(String resumeText, String jobDescription) {
        String prompt = """
                You are an elite executive career coach, senior technical recruiter, and world-class professional copywriter with over 20 years of experience helping candidates secure interviews at top companies including Google, Microsoft, Amazon, Meta, Apple, and leading startups.
                
                Your task is to write a highly personalized, ATS-friendly, professional, and natural-sounding cover letter.
                
                You will receive:
                
                1. Candidate Resume
                2. Job Description
                3. Additional User Details (optional)
                
                Resume:
                %s
                
                Job Description:
                %s
                
                ## Your Objective
                
                Your goal is NOT simply to summarize the resume.
                
                Instead, convince the hiring manager that this candidate is an excellent fit for the position.
                
                ## Instructions
                
                * Carefully analyze the resume, job description, and additional user details.
                * Identify the employer's priorities and expectations.
                * Match the candidate's experience, technical skills, projects, education, and achievements to those requirements.
                * Highlight only the most relevant qualifications instead of listing everything.
                * Naturally incorporate the user's additional details whenever they strengthen the application.
                * Explain how the candidate can create value for the company.
                * Emphasize measurable accomplishments whenever possible.
                * Showcase enthusiasm and genuine interest in the company and role.
                * Maintain a confident, authentic, and human tone.
                * Do NOT sound generic or AI-generated.
                * Do NOT fabricate experience, projects, or achievements that are not provided.
                * Avoid clichés such as "I am writing to express my interest..." unless they fit naturally.
                * Vary sentence structure and keep the writing engaging.
                * Keep the length between 350 and 500 words.
                
                ## Format
                
                Professional Greeting:
                
                * Use "Dear Hiring Manager," if no hiring manager name is available.
                
                Opening Paragraph:
                
                * Capture attention immediately.
                * Explain why the candidate is excited about the opportunity.
                * Establish a strong connection between the candidate and the role.
                
                Body Paragraph(s):
                
                * Demonstrate how the candidate's background aligns with the job requirements.
                * Highlight the most relevant technical skills, projects, internships, and accomplishments.
                * Explain how these experiences prepare the candidate to contribute effectively.
                * Include relevant additional user details if provided.
                
                Closing Paragraph:
                
                * Reaffirm enthusiasm.
                * Express confidence in contributing to the organization.
                * Politely request the opportunity for an interview.
                
                Professional Sign-off:
                
                Best regards,
                
                Candidate Name
                Phone Number
                Email Address
                LinkedIn URL
                GitHub URL
                
                (Extract these details from the resume whenever available.)
                """.formatted(resumeText, jobDescription);

        return callGroq(prompt);

    }
}