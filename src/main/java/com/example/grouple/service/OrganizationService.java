package com.example.grouple.service;

import com.example.grouple.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository repo;
    private final SecretKey jwtKey;

    /**
     * 아이디 중복 체크
     * 조직 생성 시 동일 ID 존재 여부 확인
     */
    public boolean existsById(Integer Id) {
        return repo.existsById(Id);
    }

//    @PreAuthorize("@authz.canManageOrg(req.id)")
//    @Transactional
//    public UserModifyResponse update(Orga req) throws Exception {
//        Organization org = repo.findById(req.getId())
//                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));
//        if (req.getUsername() != null)
//            org.setDescription(req.getUsername());
//        if (req.getEmail() != null)
//            org.setImage(req.getEmail());
//        if (req.getPhone() != null)
//            org.set(req.getPhone());
//        if (req.getPassword() != null && !req.getPassword().isBlank())
//            org.setPassword(encoder.encode(req.getPassword()));
//        User saved = repo.save(user);
//        return new UserModifyResponse(
//                saved.getId(),
//                saved.getUsername(),
//                saved.getEmail(),
//                saved.getPhone(),
//                saved.getCreatedAt()
//        );
//    }
}

