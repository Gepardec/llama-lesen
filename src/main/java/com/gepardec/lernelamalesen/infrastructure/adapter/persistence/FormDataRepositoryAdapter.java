package com.gepardec.lernelamalesen.infrastructure.adapter.persistence;

import com.gepardec.lernelamalesen.domain.model.FormData;
import com.gepardec.lernelamalesen.domain.port.FormDataRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class FormDataRepositoryAdapter implements FormDataRepositoryPort {
    
    private static final Logger LOG = Logger.getLogger(FormDataRepositoryAdapter.class);
    
    @Inject
    FormDataRepository repository;
    
    @Inject
    ObjectMapper objectMapper;
    
    @Override
    public FormData save(FormData formData) {
        try {
            FormDataEntity entity = toEntity(formData);
            
            // Check if entity already exists, if so update it, otherwise persist
            Optional<FormDataEntity> existing = repository.findByIdOptional(formData.id());
            if (existing.isPresent()) {
                FormDataEntity existingEntity = existing.get();
                existingEntity.originalFilename = entity.originalFilename;
                existingEntity.extractedFields = entity.extractedFields;
                existingEntity.status = entity.status;
                existingEntity.processedAt = entity.processedAt;
                existingEntity.errorMessage = entity.errorMessage;
                repository.flush();
                LOG.infof("Updated existing form data with ID: %s", formData.id());
            } else {
                repository.persistAndFlush(entity);
                LOG.infof("Saved new form data with ID: %s", formData.id());
            }
            return formData;
        } catch (Exception e) {
            LOG.errorf(e, "Error saving form data: %s", formData.id());
            throw new RuntimeException("Failed to save form data", e);
        }
    }
    
    @Override
    public Optional<FormData> findById(String id) {
        return repository.findByIdOptional(id)
                .map(this::toDomain);
    }
    
    @Override
    public List<FormData> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
    
    private FormDataEntity toEntity(FormData formData) {
        FormDataEntity entity = new FormDataEntity();
        entity.id = formData.id();
        entity.originalFilename = formData.originalFilename();
        entity.status = mapStatus(formData.status());
        entity.processedAt = formData.processedAt();
        entity.errorMessage = formData.errorMessage();
        
        try {
            entity.extractedFields = formData.extractedFields() != null ? 
                objectMapper.writeValueAsString(formData.extractedFields()) : "{}";
        } catch (Exception e) {
            LOG.warnf("Failed to serialize extracted fields for %s: %s", formData.id(), e.getMessage());
            entity.extractedFields = "{}";
        }
        
        return entity;
    }
    
    private FormData toDomain(FormDataEntity entity) {
        try {
            @SuppressWarnings("unchecked")
            var extractedFields = entity.extractedFields != null ? 
                objectMapper.readValue(entity.extractedFields, java.util.Map.class) : 
                java.util.Map.of();
            
            return new FormData(
                entity.id,
                entity.originalFilename,
                extractedFields,
                mapStatus(entity.status),
                entity.processedAt,
                entity.errorMessage
            );
        } catch (Exception e) {
            LOG.warnf("Failed to deserialize extracted fields for %s: %s", entity.id, e.getMessage());
            return new FormData(
                entity.id,
                entity.originalFilename,
                java.util.Map.of(),
                mapStatus(entity.status),
                entity.processedAt,
                entity.errorMessage
            );
        }
    }
    
    private FormDataEntity.ProcessingStatus mapStatus(FormData.ProcessingStatus status) {
        return FormDataEntity.ProcessingStatus.valueOf(status.name());
    }
    
    private FormData.ProcessingStatus mapStatus(FormDataEntity.ProcessingStatus status) {
        return FormData.ProcessingStatus.valueOf(status.name());
    }
}