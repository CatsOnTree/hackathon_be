package com.hackathon.service;

import com.hackathon.dto.ResumeAnalysis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MockResumeAnalysisService {

    private static final List<String> SKILLS = List.of(
            "java", "spring", "spring boot", "postgresql", "rest", "microservices",
            "react", "aws", "docker", "sql", "kubernetes", "problem solving");

    private final Tika tika = new Tika();

    public ResumeAnalysis analyze(MultipartFile resume, Integer experienceYears) {
        int experience = experienceYears == null ? 0 : experienceYears;

        String extracted = "";
        if (resume != null && !resume.isEmpty()) {
            try {
                extracted = tika.parseToString(resume.getInputStream()).toLowerCase();
            } catch (IOException | TikaException e) {
                // fallback to filename-based hints
                extracted = (resume.getOriginalFilename() == null ? "" : resume.getOriginalFilename()).toLowerCase();
            }
        }

        List<String> found = new ArrayList<>();
        for (String skill : SKILLS) {
            if (extracted.contains(skill)) {
                found.add(capitalizeSkill(skill));
            }
        }

        // base score + experience boost + skill boost (per found skill)
        int skillBoost = Math.min(20, found.size() * 4);
        int score = Math.min(98, 50 + (experience * 6) + skillBoost + (resume == null || resume.isEmpty() ? 0 : 3));

        String skills = found.isEmpty() ? String.join(", ", SKILLS.subList(0, Math.min(SKILLS.size(), 4)))
                : String.join(", ", found);
        return new ResumeAnalysis(skills, score);
    }

    private String capitalizeSkill(String s) {
        String[] parts = s.split(" ");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty())
                continue;
            b.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
            if (i < parts.length - 1)
                b.append(" ");
        }
        return b.toString();
    }
}
