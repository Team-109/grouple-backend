package com.example.grouple.service;

import com.example.grouple.dto.joinrequest.request.JoinRequestCreateRequest;
import com.example.grouple.dto.joinrequest.request.JoinRequestDecisionRequest;
import com.example.grouple.dto.joinrequest.response.JoinRequestListResponse;
import com.example.grouple.dto.joinrequest.response.JoinRequestResponse;
import com.example.grouple.entity.JoinRequest;
import com.example.grouple.entity.JoinRequestStatus;
import com.example.grouple.entity.Member;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.entity.id.MemberId;
import com.example.grouple.repository.JoinRequestRepository;
import com.example.grouple.repository.MemberRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class JoinRequestService {

    private static final String DEFAULT_MEMBER_ROLE = "MEMBER";

    private final JoinRequestRepository joinRequestRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public JoinRequestResponse createJoinRequest(Integer userId,
                                                 String orgCode,
                                                 JoinRequestCreateRequest request) {
        if (!StringUtils.hasText(orgCode)) {
            throw new IllegalArgumentException("조직 코드를 입력해주세요.");
        }
        Organization organization = organizationRepository.findByCode(orgCode)
                .orElseThrow(() -> new NoSuchElementException("조직을 찾을 수 없습니다."));

        ensureNotMember(userId, organization.getId());
        ensureNoPendingRequest(userId, organization.getId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setOrganization(organization);
        joinRequest.setUser(user);
        joinRequest.setMessage(request != null ? request.getMessage() : null);
        joinRequest.setStatus(JoinRequestStatus.PENDING);
        joinRequest.setCreatedAt(Instant.now());

        return JoinRequestResponse.from(joinRequestRepository.save(joinRequest));
    }

    public JoinRequestListResponse getOrganizationJoinRequests(Integer actorId, Integer orgId) {
        Organization organization = loadOrgAndValidateOwner(actorId, orgId);
        List<JoinRequestResponse> requests = joinRequestRepository.findAllByOrganization_Id(orgId)
                .stream()
                .map(JoinRequestResponse::from)
                .toList();
        return JoinRequestListResponse.of(organization.getId(), requests);
    }

    public JoinRequestResponse getOrganizationJoinRequest(Integer actorId, Integer orgId, Integer reqId) {
        loadOrgAndValidateOwner(actorId, orgId);
        JoinRequest joinRequest = joinRequestRepository.findByIdAndOrganization_Id(reqId, orgId)
                .orElseThrow(() -> new NoSuchElementException("가입 요청을 찾을 수 없습니다."));
        return JoinRequestResponse.from(joinRequest);
    }

    @Transactional
    public JoinRequestResponse approveJoinRequest(Integer actorId,
                                                  Integer orgId,
                                                  Integer reqId,
                                                  JoinRequestDecisionRequest request) {
        Organization organization = loadOrgAndValidateOwner(actorId, orgId);
        JoinRequest joinRequest = loadPendingJoinRequest(orgId, reqId);

        joinRequest.setStatus(JoinRequestStatus.APPROVED);
        joinRequest.setDecisionReason(request != null ? request.getReason() : null);
        joinRequest.setDecidedAt(Instant.now());

        ensureMemberExists(joinRequest.getUser(), organization);

        return JoinRequestResponse.from(joinRequest);
    }

    @Transactional
    public JoinRequestResponse rejectJoinRequest(Integer actorId,
                                                 Integer orgId,
                                                 Integer reqId,
                                                 JoinRequestDecisionRequest request) {
        loadOrgAndValidateOwner(actorId, orgId);
        JoinRequest joinRequest = loadPendingJoinRequest(orgId, reqId);

        joinRequest.setStatus(JoinRequestStatus.REJECTED);
        joinRequest.setDecisionReason(request != null ? request.getReason() : null);
        joinRequest.setDecidedAt(Instant.now());

        return JoinRequestResponse.from(joinRequest);
    }

    private void ensureNotMember(Integer userId, Integer orgId) {
        if (memberRepository.existsById(new MemberId(userId, orgId))) {
            throw new IllegalStateException("이미 조직의 구성원입니다.");
        }
    }

    private void ensureNoPendingRequest(Integer userId, Integer orgId) {
        boolean exists = joinRequestRepository.existsByOrganization_IdAndUser_IdAndStatus(
                orgId, userId, JoinRequestStatus.PENDING);
        if (exists) {
            throw new IllegalStateException("이미 처리 중인 가입 요청이 있습니다.");
        }
    }

    private Organization loadOrgAndValidateOwner(Integer actorId, Integer orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NoSuchElementException("조직을 찾을 수 없습니다."));
        if (organization.getOwner() == null || !organization.getOwner().getId().equals(actorId)) {
            throw new AccessDeniedException("조직에 대한 권한이 없습니다.");
        }
        return organization;
    }

    private JoinRequest loadPendingJoinRequest(Integer orgId, Integer reqId) {
        JoinRequest joinRequest = joinRequestRepository.findByIdAndOrganization_Id(reqId, orgId)
                .orElseThrow(() -> new NoSuchElementException("가입 요청을 찾을 수 없습니다."));
        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        return joinRequest;
    }

    private void ensureMemberExists(User user, Organization organization) {
        if (user == null || organization == null) {
            return;
        }
        MemberId memberId = new MemberId(user.getId(), organization.getId());
        if (memberRepository.existsById(memberId)) {
            return;
        }
        Member member = new Member();
        member.setId(memberId);
        member.setUser(user);
        member.setOrganization(organization);
        member.setRole(DEFAULT_MEMBER_ROLE);
        memberRepository.save(member);
    }
}
