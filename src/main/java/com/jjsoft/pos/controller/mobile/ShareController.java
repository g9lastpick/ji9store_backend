package com.jjsoft.pos.controller.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jjsoft.pos.service.admin.special.SpecialService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/share")
@RequiredArgsConstructor
@Log4j2
public class ShareController {
	private final SpecialService specialService;
	
	private String homeUrl = "";
	
	/**
     * 특가 공유 (SNS 미리보기 + Vue 리다이렉트)
     */
    @GetMapping("/special/{specialId}")
    @ResponseBody
    public String shareSpecial(@PathVariable("specialId") Long specialId) {
        // ✅ DB에서 특가 정보 가져오기
//        SpecialDto dto = specialService.findById(specialId);

        String title       = "";      // 특가명
        String description = "";    // 특가 설명
        String imageUrl    = "" != null
                ? "등럭한 이미지 url"
                : "https://jjpps.store/images/default-special.jpg";

        // ✅ date, locationId도 specialId로 조회 가능하므로 조립
        String date        = "특가시작일";
        Long locationId    = 1L;//특가 저장된 로케이션 이아디

        // Vue SPA 최종 URL
        String redirectUrl = homeUrl +"/mobile"
                           + "?specialId=" + specialId
                           + "&date=" + date
                           + "&locationId=" + locationId;

        // ✅ SNS 메타태그 + Vue 리다이렉트 HTML 반환
        return "<!DOCTYPE html>" +
               "<html lang='ko'>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta property='og:title' content='" + title + "' />" +
               "<meta property='og:description' content='" + description + "' />" +
               "<meta property='og:image' content='" + imageUrl + "' />" +
               "<meta property='og:url' content='" + redirectUrl + "' />" +
               "<meta property='og:type' content='website' />" +
               "</head>" +
               "<body>" +
               "<script>window.location.href='" + redirectUrl + "';</script>" +
               "</body>" +
               "</html>";
    }
}
