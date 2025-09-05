package com.gepardec.lernelamalesen.domain.port;

import com.gepardec.lernelamalesen.domain.model.FormData;
import java.util.List;
import java.util.Optional;

public interface FormDataRepositoryPort {
    FormData save(FormData formData);
    Optional<FormData> findById(String id);
    List<FormData> findAll();
    void deleteById(String id);
}