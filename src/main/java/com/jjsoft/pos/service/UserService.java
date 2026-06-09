package com.jjsoft.pos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jjsoft.pos.dto.user.UserDto;
import com.jjsoft.pos.dto.user.UserSearchDto;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.mapper.UserMapper;
import com.jjsoft.pos.repository.UserMstRepository;
import com.jjsoft.pos.util.UserPiiMasker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMstRepository userRepo;
    private final UserMapper userMapper;

    /** 유저 리스트 조회 */
    public List<UserSearchDto> getUserList(String userId) {
        List<UserMstEntity> userList;

        if (userId == null || userId.trim().isEmpty()) {
            // 전체 조회 + 생성일 기준 최신순 정렬
            userList = userRepo.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
        } else {
            // userId로 필터링 + 정렬 포함
//            userList = userRepo.findByUserIdContainingOrderByCreateDateDesc(userId);
            userList = userRepo.searchUsers(userId);
        }

        // 어드민 응답 PII 마스킹 (화면·엑셀 동시 적용)
        return UserPiiMasker.maskSearchDtoList(
                userList.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()));
    }

    /** 유저 저장 */
    public void saveAll(List<UserSearchDto> dtoList) {
        List<UserMstEntity> entities = dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        userRepo.saveAll(entities);
    }

    
    private UserMstEntity toEntity(UserSearchDto dto) {
        // 기존 회원이면 DB 원본을 불러온다. 조회 응답이 마스킹돼 있으므로
        // 마스킹된 값(또는 빈 값)으로 평문 원본을 덮어쓰지 않도록 보존한다.
        UserMstEntity existing = (dto.getId() != null)
                ? userRepo.findById(dto.getId()).orElse(null)
                : null;

        String userId = keepIfMasked(dto.getUserId(), existing == null ? null : existing.getUserId());

        // 이름/이메일이 없으면 userId로 대체 (신규 회원 기존 동작 유지)
        String name = keepIfMasked(dto.getName(), existing == null ? null : existing.getName());
        if (name == null || name.trim().isEmpty()) name = userId;

        String email = keepIfMasked(dto.getEmail(), existing == null ? null : existing.getEmail());
        if (email == null || email.trim().isEmpty()) email = userId;

        return UserMstEntity.builder()
                .id(dto.getId())
                .userId(userId)
                .ssoId(keepIfMasked(dto.getSsoId(), existing == null ? null : existing.getSsoId()))
                .password(keepIfMasked(dto.getPassword(), existing == null ? null : existing.getPassword()))
                .name(name)
                .email(email)
                .phone(keepIfMasked(dto.getPhone(), existing == null ? null : existing.getPhone()))
                .snsType(dto.getSnsType())
                // 성별은 응답에서 제거되므로 들어온 값은 무시하고 기존 값 보존
                .gender(existing != null ? existing.getGender() : dto.getGender())
                .profileImgUrl(dto.getProfileImgUrl())
                .useYn(dto.getUseYn())
                .build();
    }

    /** 들어온 값이 비었거나 마스킹('*' 포함)이면 기존 DB 값을 유지 */
    private static String keepIfMasked(String incoming, String existing) {
        if (incoming == null || incoming.trim().isEmpty()) return existing;
        if (incoming.indexOf('*') >= 0) return existing;
        return incoming;
    }

    private UserSearchDto toDto(UserMstEntity e) {
        UserSearchDto dto = new UserSearchDto();
        dto.setId(e.getId());
        dto.setUserId(e.getUserId());
        dto.setSsoId(e.getSsoId());
        dto.setPassword(e.getPassword());
        dto.setName(e.getName());
        dto.setEmail(e.getEmail());
        dto.setPhone(e.getPhone());
        dto.setSnsType(e.getSnsType());
        dto.setGender(e.getGender());
        dto.setProfileImgUrl(e.getProfileImgUrl());
        dto.setUseYn(e.getUseYn());
        return dto;
    }
    
    
    /** 픽업정보 조회 */
    public List<UserDto> getPickupResultList(UserSearchDto dto) {
    	List<UserDto> list = userMapper.selectPickupList(dto);
    	// 어드민 응답 PII 마스킹 (화면·엑셀 동시 적용)
    	return UserPiiMasker.maskUserDtoList(list);
    }

}