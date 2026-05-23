package com.jjsoft.pos.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class ComUtil {

	public LocalDate parseDate(String dateStr) {
	    try {
	        return (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : null;
	    } catch (Exception e) {
	        throw new RuntimeException("날짜 형식 오류: " + dateStr);
	    }
	}
	public LocalDateTime strToLocalDateTime(String dateString) {
        try {
            // 날짜 형식에 맞는 DateTimeFormatter 정의 (시간은 생략된 형태)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 문자열을 LocalDate로 변환
            LocalDate date = LocalDate.parse(dateString, formatter);
            // 현재 시간
            LocalTime currentTime = LocalTime.now();
            // LocalDate와 현재 시간 결합하여 LocalDateTime 생성
            LocalDateTime dateTime = LocalDateTime.of(date, currentTime);
            // 출력
            System.out.println("LocalDateTime: " + dateTime);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("날짜 형식 오류: ");
        }
    }
	
	public String localDateTimeToStr(LocalDateTime localDateTime) {
        // 날짜 형식에 맞는 DateTimeFormatter 정의
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        // LocalDateTime을 "yyyy-MM-dd" 형식의 문자열로 변환
	        String formattedDate = localDateTime.format(formatter);

	        // 출력
	        System.out.println("Formatted Date: " + formattedDate);
	        return formattedDate;
		} catch (Exception e) {
			e.printStackTrace();
            throw new RuntimeException("날짜 형식 오류: ");
		}
        
    }

}
