package com.local.connect.event.repository;

import com.local.connect.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByContentId(String contentId);

    Page<Event> findByRegionContaining(String region, Pageable pageable);
}
