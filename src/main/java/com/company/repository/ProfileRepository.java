package com.company.repository;

import com.company.model.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {


    Optional<ProfileEntity> findByUserId(Long id);
}