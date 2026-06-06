package com.jjsoft.pos.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.user.UserDto;
import com.jjsoft.pos.dto.user.UserSearchDto;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.UserService;
import com.jjsoft.pos.util.PiiMaskUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

    /** 유저 목록 조회 */
	@GetMapping("/getUserList")
	public List<UserSearchDto> getUserList(
	    @RequestParam(name = "userId", required = false) String userId
	) {
	    return userService.getUserList(userId);
	}

    /** 유저 저장 */
    @PostMapping("/userSave")
    public boolean saveUserList(@RequestBody List<UserSearchDto> list) {
        userService.saveAll(list);
        return true;
    }
    
    /** 유저별 픽업 현황  */
    @GetMapping("/getPickupList")
    public ResponseEntity<ApiResponse<Object>> getPickupList(@ModelAttribute UserSearchDto dto) {
    	List<UserDto>  list = userService.getPickupResultList(dto);

        // 개인정보 마스킹 (평문 노출 방지)
        if (list != null) {
            for (UserDto u : list) {
                u.setName(PiiMaskUtil.maskName(u.getName()));
                u.setPhone(PiiMaskUtil.maskPhone(u.getPhone()));
                u.setAddress(PiiMaskUtil.maskAddress(u.getAddress()));
                u.setBirthday(PiiMaskUtil.maskBirthday(u.getBirthday()));
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    
    
    
}
