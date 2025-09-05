package com.gepardec.lernelamalesen.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PromptService {
    
    @ConfigProperty(name = "llm.prompt.system", 
                   defaultValue = "Sie sind ein KI-Assistent, der darauf spezialisiert ist, handschriftlich ausgefüllte Formulare zu analysieren und strukturierte Daten zu extrahieren.")
    String systemPrompt;
    
    @ConfigProperty(name = "llm.prompt.user", 
                   defaultValue = "Analysieren Sie dieses handschriftlich ausgefüllte Formular und extrahieren Sie alle lesbaren Informationen in einem strukturierten JSON-Format. Geben Sie nur güliges JSON zurück.")
    String userPrompt;
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public String getUserPrompt() {
        return userPrompt;
    }
}