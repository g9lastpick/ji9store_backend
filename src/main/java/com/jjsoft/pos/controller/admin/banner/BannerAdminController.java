package com.jjsoft.pos.controller.admin.banner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.banner.StripBannerSaveRequest;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.banner.StripBannerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/** 어드민 - 특가 탭 띠배너 관리 */
@RestController
@RequestMapping("/api/admin/banner")
@RequiredArgsConstructor
@Log4j2
public class BannerAdminController {

    private final StripBannerService stripBannerService;

    /** 목록 */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> list(@RequestParam("storeId") Long storeId) {
        return ResponseEntity.ok(ApiResponse.ok(stripBannerService.listForAdmin(storeId)));
    }

    /** 등록/수정 (multipart: 이미지 + 필드) */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Object>> save(@ModelAttribute StripBannerSaveRequest request) {
        Long id = stripBannerService.save(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(id));
    }

    /** 삭제 */
    @PostMapping("/delete/{bannerId}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("bannerId") Long bannerId) {
        stripBannerService.delete(bannerId);
        return ResponseEntity.ok(ApiResponse.ok(true));
    }
}
