package com.example.grouple.repository;

import com.example.grouple.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AnouncementRepository extends JpaRepository<Announcement, Integer> {
    
}
