package com.gepardec.lernelamalesen.domain.port;

import com.gepardec.lernelamalesen.domain.model.LlmRequest;
import com.gepardec.lernelamalesen.domain.model.LlmResponse;

public interface LlmPort {
    LlmResponse analyzeDocument(LlmRequest request);
}