package com.company.repository;

import com.company.enums.VideoType;
import com.company.model.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<VideoEntity, String> {

    Optional<VideoEntity> findByUrlAndType(String url, VideoType type);
}