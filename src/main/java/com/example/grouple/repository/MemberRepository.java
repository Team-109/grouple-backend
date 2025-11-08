package com.example.grouple.repository;

import com.example.grouple.entity.Member;
import com.example.grouple.entity.id.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, MemberId> {
}
