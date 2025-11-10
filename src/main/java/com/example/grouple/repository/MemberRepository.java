package com.example.grouple.repository;

import com.example.grouple.entity.Member;
import com.example.grouple.entity.id.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, MemberId> {
    List<Member> findAllByOrganization_Id(Integer orgId);
}
