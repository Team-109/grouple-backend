package com.example.grouple.service;

import com.example.grouple.dto.organization.request.OrgMembersDeleteRequest;
import com.example.grouple.dto.organization.request.OrgMembersRequest;
import com.example.grouple.dto.organization.response.OrgMembersDeleteResponse;
import com.example.grouple.dto.organization.response.OrgMembersResponse;
import com.example.grouple.entity.Member;
import com.example.grouple.entity.id.MemberId;
import com.example.grouple.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public OrgMembersResponse getOrgMembers(Integer orgId, OrgMembersRequest request) {
        String roleFilter = request != null ? request.getRole() : null;

        List<OrgMembersResponse.MemberInfo> members = memberRepository.findAllByOrganization_Id(orgId)
                .stream()
                .filter(member -> roleFilter == null || roleFilter.equalsIgnoreCase(member.getRole()))
                .map(this::mapToMemberInfo)
                .toList();

        return OrgMembersResponse.of(orgId, members);
    }

    private OrgMembersResponse.MemberInfo mapToMemberInfo(Member member) {
        return OrgMembersResponse.MemberInfo.builder()
                .memberId(member.getUser() != null ? member.getUser().getId() : null)
                .username(member.getUser() != null ? member.getUser().getUsername() : null)
                .email(member.getUser() != null ? member.getUser().getEmail() : null)
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }

    @Transactional
    public OrgMembersDeleteResponse deleteMember(Integer actorId,
                                                 Integer orgId,
                                                 Integer memberId,
                                                 OrgMembersDeleteRequest request) {
        Member member = memberRepository.findById(new MemberId(memberId, orgId))
                .orElseThrow(() -> new NoSuchElementException("조직 구성원을 찾을 수 없습니다."));

        validateDeletionPermission(actorId, member);

        memberRepository.delete(member);

        return OrgMembersDeleteResponse.builder()
                .organizationId(orgId)
                .memberId(memberId)
                .reason(request != null ? request.getReason() : null)
                .deletedAt(Instant.now())
                .build();
    }

    private void validateDeletionPermission(Integer actorId, Member target) {
        boolean isSelf = target.getUser() != null && target.getUser().getId().equals(actorId);
        boolean isOwner = target.getOrganization() != null
                && target.getOrganization().getOwner() != null
                && target.getOrganization().getOwner().getId().equals(actorId);

        if (!isSelf && !isOwner) {
            throw new AccessDeniedException("조직 구성원을 삭제할 권한이 없습니다.");
        }
    }
}
