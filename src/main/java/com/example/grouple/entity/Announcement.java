package com.example.grouple.entity;

import jakarta.persistence.*;
<<<<<<< HEAD
=======
import lombok.AccessLevel;
>>>>>>> 2167a65 (Add Announcement entity)
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
<<<<<<< HEAD
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false)
=======
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(nullable = false, length = 255)
>>>>>>> 2167a65 (Add Announcement entity)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

<<<<<<< HEAD
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Boolean star = Boolean.FALSE;
=======

    @Column(nullable = false)
    private String star;
>>>>>>> 2167a65 (Add Announcement entity)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
<<<<<<< HEAD
=======

    @CreatedDate
    @Column(nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;



>>>>>>> 2167a65 (Add Announcement entity)
}
