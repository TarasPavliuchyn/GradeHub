package io.gradehub.repository;

import io.gradehub.model.Organization;
import org.springframework.data.repository.CrudRepository;

/**
 * @author ptar
 * @since 1.0
 */
public interface OrganizationRepository extends CrudRepository<Organization, Long> {
}