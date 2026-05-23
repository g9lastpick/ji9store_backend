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

        return userList.stream()
                       .map(this::toDto)
                       .collect(Collectors.toList());
    }

    /** 유저 저장 */
    public void saveAll(List<UserSearchDto> dtoList) {
        List<UserMstEntity> entities = dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        userRepo.saveAll(entities);
    }

    
    private UserMstEntity toEntity(UserSearchDto dto) {
        String userId = dto.getUserId();
        
        // 이름이 없으면 userId로 대체
        String name = (dto.getName() == null || dto.getName().trim().isEmpty())
                        ? userId
                        : dto.getName();

        // 이메일도 없으면 userId로 대체
        String email = (dto.getEmail() == null || dto.getEmail().trim().isEmpty())
                        ? userId
                        : dto.getEmail();

        return UserMstEntity.builder()
                .id(dto.getId())
                .userId(userId)
                .password(dto.getPassword())
                .name(name)
                .email(email)
                .phone(dto.getPhone())
                .snsType(dto.getSnsType())
                .gender(dto.getGender())
                .profileImgUrl(dto.getProfileImgUrl())
                .useYn(dto.getUseYn())
                .build();
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
    	
    	return list ; 
    }

}