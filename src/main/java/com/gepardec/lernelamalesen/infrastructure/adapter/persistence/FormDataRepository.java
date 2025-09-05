package com.gepardec.lernelamalesen.infrastructure.adapter.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormDataRepository implements PanacheRepositoryBase<FormDataEntity, String> {
}