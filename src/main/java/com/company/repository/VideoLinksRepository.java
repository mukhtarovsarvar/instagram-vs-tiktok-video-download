package com.company.repository;

import com.company.model.VideoLinksEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoLinksRepository extends JpaRepository<VideoLinksEntity, String> {

    List<VideoLinksEntity> findByUrl(String url);
}