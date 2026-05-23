package com.jjsoft.pos.service.common;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jjsoft.pos.dto.common.ImageUploadRequest;
import com.jjsoft.pos.entity.DrawMstEntity;
import com.jjsoft.pos.entity.GroupbuyMstEntity;
import com.jjsoft.pos.entity.ProductImageEntity;
import com.jjsoft.pos.entity.SpecialDtlEntity;
import com.jjsoft.pos.repository.DrawMstRepository;
import com.jjsoft.pos.repository.GroupbuyMstRepository;
import com.jjsoft.pos.repository.ProductImageRepository;
import com.jjsoft.pos.repository.ProductMstRepository;
import com.jjsoft.pos.repository.SpecialDtlRepository;
import com.jjsoft.pos.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageManagerService {
	
	private final ProductImageRepository productImageRepository;
    private final ProductMstRepository productMstRepository;
    
    private final SpecialDtlRepository specialDtlRepository;

  //주석 나중에 풀어야함. 2020202020
//    private final GroupbuyMstRepository groupbuyMstRepository;
//    private final DrawMstRepository drawMstRepository;
    
    private final S3Service s3Service;

    @Value("${file.upload.image-path}")
    private String imagePath;

    @Value("${file.upload.access-url-prefix}")
    private String urlPrefix;
    
	@Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;
	
    @Transactional
    public boolean saveImages(ImageUploadRequest dto) {
        // S3 롤백용
        List<String> uploadedKeys = new ArrayList<>();

        try {
            List<MultipartFile> files = dto.getImageFileList();
            if (files == null || files.isEmpty()) {
                throw new RuntimeException("업로드할 이미지가 없습니다.");
            }

            List<ProductImageEntity> savedImages = new ArrayList<>();
            
         // 1) 기존 최대 sortOrder 조회
            Integer maxOrder = productImageRepository.findMaxSortOrderByProductId(dto.getProductId());
            if (maxOrder == null) {
                maxOrder = 0; // 이미지가 없으면 0부터 시작
            }

            
            int order = maxOrder + 1;

            for (MultipartFile file : files) {
                // S3 업로드
                String fileUrl = s3Service.uploadFile(file);
                // fileKey만 저장
                String fileKey = fileUrl.replace("https://" + bucketName + ".s3." + region + ".amazonaws.com/", "");
                uploadedKeys.add(fileKey);

                // DB 저장
                ProductImageEntity entity = ProductImageEntity.builder()
                        .productId(dto.getProductId())
                        .imageUrl(fileUrl)
                        .sortOrder(order++)
                        .description(dto.getDescription())
                        .useYn("Y")
                        .createUser("admin")
                        .createDate(LocalDateTime.now())
                        .build();

                savedImages.add(productImageRepository.save(entity));
            }

            return true;

        } catch (Exception e) {
            // 업로드된 파일 삭제
            uploadedKeys.forEach(s3Service::deleteFile);
            log.error("이미지 업로드 중 오류 발생", e);
            return false;
        }
    }
    
    
    public boolean deleteImageById(Long imgId) {
    	
    	try {
    		
    		ProductImageEntity entity = productImageRepository.findById(imgId)
    				.orElseThrow(() -> new RuntimeException("이미지 없음"));
    		
    		// 실제 파일도 같이 삭제
    		String fullPath = imagePath + entity.getImageUrl().replace(urlPrefix, "");
    		File file = new File(fullPath);
    		if (file.exists()) file.delete();
    		
    		if(s3Service.deleteFile(entity.getImageUrl())) {
    			productImageRepository.deleteById(imgId);
    			return true;
    		}
    		return false;
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    
    /* 특가 이미지 등록 */
    @Transactional
    public boolean saveSpecialDtlImages(ImageUploadRequest dto) {
        // S3 롤백용
        List<String> uploadedKeys = new ArrayList<>();

        try {
            List<MultipartFile> files = dto.getImageFileList();
            if (files == null || files.isEmpty()) {
                throw new RuntimeException("업로드할 이미지가 없습니다.");
            }

            
            if(dto.getSpecialDtlId() == null) {
            	return false;
            }
         // 1) 기존 최대 sortOrder 조회
            SpecialDtlEntity special = specialDtlRepository.findById(dto.getSpecialDtlId())
                    .orElseThrow(() -> new RuntimeException("특가 정보를 찾을 수 없습니다."));
           

            // ✅ 파일은 1개만 허용
            HashMap<String,String> resultMap = setS3ImageSave(files.get(0));

            // S3 업로드
            String fileUrl = resultMap.get("fileUrl");
            uploadedKeys.add(resultMap.get("fileKey"));

            // DB 업데이트
            special.setImageUrl(fileUrl);
            
            
            return true;

        } catch (Exception e) {
            // 업로드된 파일 삭제
            uploadedKeys.forEach(s3Service::deleteFile);
            log.error("이미지 업로드 중 오류 발생", e);
            return false;
        }
    }
    
    
    public boolean deleteSpecialDtlImageById(Long specialDtlId) {
    	
    	try {
    		
    		SpecialDtlEntity entity = specialDtlRepository.findById(specialDtlId)
    				.orElseThrow(() -> new RuntimeException("특가 없음"));
    		
    		// 실제 파일도 같이 삭제
    		
    		if(s3Service.deleteFile(entity.getImageUrl())) {
    			entity.setImageUrl(null);
    			return true;
    		}
    		return false;
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    
    
    /** 공동구매 이미지 등록 */
    @Transactional
    public boolean saveGroupbuyImages(ImageUploadRequest dto) {
        // S3 롤백용
    	return false;
    	//주석 나중에 풀어야함. 2020202020
//        List<String> uploadedKeys = new ArrayList<>();
//
//        try {
//            List<MultipartFile> files = dto.getImageFileList();
//            if (files == null || files.isEmpty()) {
//                throw new RuntimeException("업로드할 이미지가 없습니다.");
//            }
//
//            if(dto.getGroupbuyId() == null) {
//            	return false;
//            }
//         // 1) 기존 최대 sortOrder 조회
//            GroupbuyMstEntity groupbuy = groupbuyMstRepository.findById(dto.getGroupbuyId())
//                    .orElseThrow(() -> new RuntimeException("공동구매 정보를 찾을 수 없습니다."));
//           
//
//            // ✅ 파일은 1개만 허용
//            HashMap<String,String> resultMap = setS3ImageSave(files.get(0));
//
//            // S3 업로드
//            String fileUrl = resultMap.get("fileUrl");
//            uploadedKeys.add(resultMap.get("fileKey"));
//
//            // DB 업데이트
//            groupbuy.setImageUrl(fileUrl);
//            
//            return true;
//
//        } catch (Exception e) {
//            // 업로드된 파일 삭제
//            uploadedKeys.forEach(s3Service::deleteFile);
//            log.error("이미지 업로드 중 오류 발생", e);
//            return false;
//        }
    }
    
    /** 공동구매 이미지 삭제 */
    public boolean deleteGroupbuyImageById(Long groupbuyId) {
    	return false;
    	//주석 나중에 풀어야함. 2020202020
//    	try {
//    		GroupbuyMstEntity entity = groupbuyMstRepository.findById(groupbuyId)
//    				.orElseThrow(() -> new RuntimeException("공동구매 객체 없음"));
//    		
//    		// 실제 파일도 같이 삭제
//    		if(s3Service.deleteFile(entity.getImageUrl())) {
//    			entity.setImageUrl(null);
//    			return true;
//    		}
//    		return false;
//    	}catch(Exception e) {
//    		e.printStackTrace();
//    		return false;
//    	}
    }
    
    
    /** 드로우 이미지 등록 */
    @Transactional
    public boolean saveDrawImages(ImageUploadRequest dto) {
        // S3 롤백용
    	return false;
    	//주석 나중에 풀어야함. 2020202020
//        List<String> uploadedKeys = new ArrayList<>();
//
//        try {
//            List<MultipartFile> files = dto.getImageFileList();
//            if (files == null || files.isEmpty()) {
//                throw new RuntimeException("업로드할 이미지가 없습니다.");
//            }
//
//            if(dto.getGroupbuyId() == null) {
//            	return false;
//            }
//         // 1) 기존 최대 sortOrder 조회
//            DrawMstEntity groupbuy = drawMstRepository.findById(dto.getDrawId())
//                    .orElseThrow(() -> new RuntimeException("드로우 정보를 찾을 수 없습니다."));
//           
//
//            // ✅ 파일은 1개만 허용
//            HashMap<String,String> resultMap = setS3ImageSave(files.get(0));
//
//            // S3 업로드
//            String fileUrl = resultMap.get("fileUrl");
//            uploadedKeys.add(resultMap.get("fileKey"));
//
//            // DB 업데이트
//            groupbuy.setImageUrl(fileUrl);
//            
//            return true;
//
//        } catch (Exception e) {
//            // 업로드된 파일 삭제
//            uploadedKeys.forEach(s3Service::deleteFile);
//            log.error("드로우 이미지 업로드 중 오류 발생", e);
//            return false;
//        }
    }
    
    /** 드로우 이미지 삭제 */
    public boolean deleteDrawImageById(Long drawId) {
    	return false;
    	//주석 나중에 풀어야함. 2020202020
//    	try {
//    		
//    		DrawMstEntity entity = drawMstRepository.findById(drawId)
//    				.orElseThrow(() -> new RuntimeException("드로우 객체 없음"));
//    		
//    		// 실제 파일도 같이 삭제
//    		if(s3Service.deleteFile(entity.getImageUrl())) {
//    			entity.setImageUrl(null);
//    			return true;
//    		}
//    		return false;
//    	}catch(Exception e) {
//    		e.printStackTrace();
//    		return false;
//    	}
    }
    
    
    /** s3 이미지 저장 */
    public HashMap<String , String> setS3ImageSave(MultipartFile file) {
    	try {
    		List<String> uploadedKeys = new ArrayList<>();
    		HashMap<String,String> resultMap = new HashMap<>();
    		
    		String fileUrl = s3Service.uploadFile(file);
            String fileKey = fileUrl.replace("https://" + bucketName + ".s3." + region + ".amazonaws.com/", "");
            uploadedKeys.add(fileKey);
            resultMap.put("fileKey", fileKey);
            resultMap.put("fileUrl", fileUrl);
            
            return resultMap;
    		
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    	
    }


}
