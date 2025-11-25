package com.example.Petbulance_BE.domain.review.service;

import com.example.Petbulance_BE.domain.hospital.dto.UserReviewSearchDto;
import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.repository.HospitalJpaRepository;
import com.example.Petbulance_BE.domain.review.dto.*;
import com.example.Petbulance_BE.domain.review.dto.req.FilterReqDto;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewImageCheckReqDto;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewSaveReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.*;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import com.example.Petbulance_BE.domain.review.entity.UserReviewImage;
import com.example.Petbulance_BE.domain.review.repository.ReviewImageJpaRepository;
import com.example.Petbulance_BE.domain.review.repository.ReviewJpaRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import com.example.Petbulance_BE.domain.review.dto.GeminiApiDto.*;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
public class ReviewService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String genimiApiUrl;
    private final String geoApiUrl;
    private final String geoKey;
    private final HospitalJpaRepository hospitalJpaRepository;
    private final ReviewJpaRepository reviewJpaRepository;
    private final UserUtil userUtil;
    private final JWTUtil jwtUtil;
    private final S3Service s3Service;
    private final ReviewImageJpaRepository reviewImageJpaRepository;

    public ReviewService(WebClient webClient, ObjectMapper objectMapper, @Value("${gemini.api.url-with-key}") String genimiApiUrl,
                         @Value("${geo.api.uri}") String geoApiUrl, @Value("${geo.api.key}") String geoKey, HospitalJpaRepository hospitalJpaRepository
            , ReviewJpaRepository reviewJpaRepository, UserUtil userUtil, JWTUtil jWTUtil, S3Service s3Service, ReviewImageJpaRepository reviewImageJpaRepository) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.genimiApiUrl = genimiApiUrl;
        this.geoApiUrl = geoApiUrl;
        this.geoKey = geoKey;
        this.hospitalJpaRepository = hospitalJpaRepository;
        this.reviewJpaRepository = reviewJpaRepository;
        this.userUtil = userUtil;
        this.jwtUtil = jWTUtil;
        this.s3Service = s3Service;
        this.reviewImageJpaRepository = reviewImageJpaRepository;
    }

    public Mono<ReceiptResDto> receiptExtractProcess(MultipartFile image) {

        log.info("🚀 [{}] 요청 시작", Thread.currentThread().getName());

         return getExtractedDataMono(image)
//                 .doOnSubscribe(s ->
//                         log.info("📡 [{}] Gemini API 호출 시작", Thread.currentThread().getName()))
//                 .doOnNext(data ->
//                         log.info("✅ [{}] Gemini 응답 수신", Thread.currentThread().getName()))
                 .flatMap(extractedData -> {

             DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
             DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

             LocalDateTime paymentDateTime;

             String address = extractedData.address();
             String addressType = extractedData.addressType();
             String time = extractedData.paymentTime();
             Long price = extractedData.totalAmount();

             try {
                 // 시간 정보 있는 경우
                 paymentDateTime = LocalDateTime.parse(time, dtf);
             } catch (DateTimeParseException e) {
                 // 시간 정보 없는 경우, LocalDate로 변환 후 00:00:00으로 처리
                 LocalDate dateOnly = LocalDate.parse(time, df);
                 paymentDateTime = dateOnly.atStartOfDay();
             }

             if(address == null || address.isEmpty()) {
                 return Mono.error(new CustomException(ErrorCode.NO_ADDRESS_FOUND));
             }

             LocalDateTime finalPaymentDateTime = paymentDateTime;

             return geocodeAddress(address, addressType)
//                     .doOnSubscribe(s ->
//                             log.info("🌍 [{}] 지오코딩 API 호출", Thread.currentThread().getName()))
//                     .doOnNext(point ->
//                             log.info("📍 [{}] 지오코딩 응답 수신", Thread.currentThread().getName()))
                     .publishOn(Schedulers.boundedElastic()) //이 시점 이후의 작업은 블로킹 스레드 풀에서 동작, 논블로킹 스레드 풀은 막히지 않음
//                     .doOnNext(point ->
//                             log.info("💾 [{}] DB 조회 시작 (boundedElastic)", Thread.currentThread().getName()))
                     .map(point ->{
                     double lng = point.x(); //경도
                     double lat = point.y(); //위도

                     log.info("x위도{}", lat);
                     log.info("y경도{}", lng);

                     List<Hospital> nearestHospitals = hospitalJpaRepository.findNearestHospitals(lng, lat,10000);
                     if(nearestHospitals.isEmpty()) {
                         throw new CustomException(ErrorCode.NOT_FOUND_RECEIPT_HOSPITAL);
                     }
                     Hospital hospital = nearestHospitals.get(0);

                     return ReceiptResDto.builder()
                             .hospitalId(hospital.getId())
                             .hospitalName(hospital.getName())
                             .visitDateTime(finalPaymentDateTime)
                             .price(price)
                             .build();
                 });

         });


    }

    //주소를 추출하여 지오 코딩으로 위도, 경도 값을 반환 받는 로직
    private Mono<GeoDto.PointDTO> geocodeAddress(String address, String addressType) {
        log.info("주소값 {}", address);
        return webClient.get()
                .uri(geoApiUrl, uriBuilder ->
                        uriBuilder
                                .queryParam("service", "address")
                                .queryParam("request", "getCoord")
                                .queryParam("format", "json")
                                .queryParam("crs", "epsg:4326")
                                .queryParam("key", geoKey)
                                .queryParam("type", addressType)
                                .queryParam("address", address)
                                .build()
                )
                .retrieve()
                .bodyToMono(GeoDto.GeoRootDTO.class)
                .flatMap(root -> {
                    if (root == null || root.response() == null || root.response().result().point() == null) {
                        log.error("📍 지오코딩 실패 — 결과가 없습니다. 응답: {}", root);
                        return Mono.error(new CustomException(ErrorCode.FAIL_GEOCODING));
                    }
                    return Mono.just(root.response().result().point());
                });
    }

    private Mono<ExtractedData> getExtractedDataMono(MultipartFile image) {
        String base64Image;
        try{
            base64Image = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            log.error("이미지 변환 실패");
            throw new RuntimeException(e);
        }

        String prompt = "당신은 영수증 분석 전문가입니다."
                + "1. 이 이미지가 '동물병원' 영수증인지 판단하세요."
                + "2. 동물병원 영수증이 아니면, `{\"status\": \"fail\", \"message\": \"동물병원 영수증이 아닙니다.\"}` 를 반환하세요."
                + "3. 동물병원 영수증이 맞다면, 아래 6개 항목을 추출하여 JSON 형식으로 반환하세요."
                + "   - `{\"status\": \"success\", \"data\": {\"storeName\": \"...\", \"totalAmount\": ..., \"address\": \"...\", \"paymentTime\": \"...\"}}`"
                + "   - storeName: 매장명 (String)"
                + "   - totalAmount: 총 결제 금액 (Integer, 숫자만)"
                + "   - address: '도로명 주소(괄호 안에 있는 값은 포함하지 않음)'. 만약 도로명 주소가 없으면 '지번 주소'를 추출 (String)"
                + "   - addressType: 'address필드이 값이 도로명 주소라면 (\"road\"), 지번 주소라면 (\"parcel\")을 출력하세요.'"
                + "   - paymentTime: 결제 시간 ('YYYY-MM-DD HH:MM:SS' 형식. 날짜만 있으면 'YYYY-MM-DD')"
                + "4. 동물병원 영수증이 맞지만, 위 5개 항목을 추출할 수 없다면 `{\"status\": \"fail\", \"message\": \"데이터 추출에 실패하였습니다.\"}` 를 반환하세요."
                + "5. 다른 설명 없이 오직 요청한 JSON 형식의 텍스트만 반환하세요.";

        Part textPart = new Part(prompt, null);
        Part imagePart = new Part(null, new InlineData(image.getContentType(), base64Image));

        Content content = new Content(List.of(textPart, imagePart));
        GeminiRequest requestBody = new GeminiRequest(List.of(content));

        return webClient.post()
                .uri(genimiApiUrl)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class)

                // (응답 Mono를 String Mono로 변환 - 기존과 동일)
                .map(geminiResponse -> {
                    try {
                        String rawText = geminiResponse.candidates().get(0)
                                .content().parts().get(0)
                                .text();
                        return rawText.replace("```json", "").replace("```", "").trim();
                    } catch (Exception e) {
                        // 이 경우 Gemini가 아예 잘못된 응답을 준 것
                        throw new RuntimeException("Gemini 응답 구조 파싱 실패", e);
                    }
                })

                //평탄화의 기능도 제공 Mono.just, Mono.error는 Mono<T>을 반환하기 때문
                .flatMap(jsonString -> {
                    try {
                        // Gemini가 만든 JSON을 DTO로 파싱
                        GeminiJsonOutput output = objectMapper.readValue(jsonString, GeminiJsonOutput.class);

                        if ("success".equals(output.status())) {
                            // 1. 성공: 'data' 객체를 담은 Mono 반환
                            return Mono.just(output.data());
                        } else {
                            // 2. 실패 (Gemini가 fail이라 응답):
                            // 'message'를 담은 Custom Exception을 Mono.error로 반환
                            return Mono.error(new CustomException(ErrorCode.FAIL_RECEIPT_EXTRACT));
                        }
                    } catch (Exception e) {
                        log.error("아예 반환 형식이 잘못됨");
                        return Mono.error(new RuntimeException("Gemini가 반환한 JSON 파싱 중 오류 발생", e));
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof CustomException ce) {
                        // 이미 처리한 비즈니스 예외면 다시 던지기
                        return Mono.error(ce);
                    } else if (e instanceof WebClientResponseException wcre) {
                        log.error("외부 API 호출 실패: {}", wcre.getResponseBodyAsString());
                        return Mono.error(new CustomException(ErrorCode.FAIL_API_CONNECT));
                    } else {
                        log.error("예상치 못한 오류 발생", e);
                        return Mono.error(new CustomException(ErrorCode.FAIL_WHILE_API));
                    }
                });
    }

    public FindHospitalResDto findHospitalProcess(String hospitalName) {
        List<Hospital> nameStartsWith = hospitalJpaRepository.findByNameStartsWith(hospitalName);

        List<FindHospitalResDto.HospitalDto> list = nameStartsWith.stream()
                .map(h ->
                        new FindHospitalResDto.HospitalDto(h.getId(), h.getName()))
                .toList();

        return new FindHospitalResDto(list);
    }

    public CursorPagingResDto<UserReviewSearchDto> searchReviewProcess(String value, Long cursorId, int size) {

        Pageable pageable = PageRequest.of(0, size + 1);

        List<UserReviewSearchDto> reviews = reviewJpaRepository.findByHospitalNameOrTreatmentService(value, cursorId, pageable);

        //reviews의 사이즈가 size보다 작으면 마지막
        boolean hasNext = reviews.size() > size;

        //size+1개 반환된 리스트에서 size만큼 목록 생성
        List<UserReviewSearchDto> limitedReviews = hasNext ? reviews.subList(0, size) : reviews;

        Long nextCursorId = null;

        if (hasNext) {
            nextCursorId = limitedReviews.get(limitedReviews.size() - 1).getId(); // UserReviewSearchDto에 getId()가 필요함
        }

        return new CursorPagingResDto<>(limitedReviews, nextCursorId, hasNext);

    }


    public CursorPagingResDto<FilterResDto> filterReviewProcess(FilterReqDto filterReqDto) {

        int size = filterReqDto.getSize();

        List<FilterResDto> filterResDtos = reviewJpaRepository.reviewFilterQuery(filterReqDto);

        boolean hasNext = filterResDtos.size()>size;

        List<FilterResDto> limitedList = hasNext ? filterResDtos.subList(0,size) : filterResDtos;

        Long nextCursorId = null;
        if (hasNext) {
            nextCursorId = limitedList.get(limitedList.size() -1 ).getId();
        }
        return new CursorPagingResDto<>(limitedList, nextCursorId, hasNext);

    }

    @Transactional(readOnly = true)
    public HospitalReviewsCursorResDto hospitalReviewsProcess(
            Long hospitalId, Boolean imageOnly, Long cursorId, Double cursorRating, Long cursorLikeCount, int size, String sortBy, String sortDirection // 사용하지 않음 (DESC 고정)
    ) {

        Users currentUser = userUtil.getCurrentUser();
        final boolean userLogined = currentUser != null;

        // 1. Pageable 설정 (size + 1 전략)
        Pageable pageable = PageRequest.of(0, size + 1);
        List<UserReview> reviews;

        // 2. 정렬 타입에 따른 Repository 메서드 호출
        switch (sortBy.toUpperCase()) {
            case "TOTALRATING" -> reviews = reviewJpaRepository.findByHospitalIdOrderByRating(
                    hospitalId, imageOnly, cursorRating, cursorId, pageable
            );
            case "LIKECOUNT" -> reviews = reviewJpaRepository.findByHospitalIdOrderByLikeCount(
                    hospitalId, imageOnly, cursorLikeCount, cursorId, pageable
            );
            case "CREATEDAT" -> reviews = reviewJpaRepository.findByHospitalIdOrderByLatest(
                    hospitalId, imageOnly, cursorId, pageable
            );
            default -> reviews = reviewJpaRepository.findByHospitalIdOrderByLatest(
                    hospitalId, imageOnly, cursorId, pageable
            );
        }

        // 3. 커서 페이징 로직 (hasNext 계산, 목록 자르기)
        boolean hasNext = reviews.size() > size;
        List<UserReview> limitedReviews = hasNext ? reviews.subList(0, size) : reviews;

        List<SearchResDto> list = limitedReviews.stream().map(r ->
                SearchResDto.builder()
                        .receiptCheck(r.getReceiptCheck())
                        .id(r.getId())
                        .treatmentService(r.getTreatmentService())
                        .animalType(r.getAnimalType())
                        .detailAnimalType(r.getDetailAnimalType())
                        .reviewContent(r.getReviewContent())
                        .totalRating(r.getOverallRating())
                        .reviewDate(r.getCreatedAt().toLocalDate())
                        .likeCount(r.getLikes().size())
                        .images(r.getImages().stream().map(i -> i.getImageUrl()).toList())
                        .liked(userLogined?r.getLikes().stream().anyMatch(l->l.getUser().getId().equals(currentUser.getId())):false)
                        .build()
        ).toList();

        Long nextCursorId = null;

        if (hasNext) {
            nextCursorId = list.get(list.size()-1).getId();
        }

        switch (sortBy.toUpperCase()){
            case "TOTALRATING" -> {
                return new HospitalReviewsCursorResDto(list, nextCursorId, list.size()>0?list.get(list.size()-1).getTotalRating():null, null, hasNext);
            }
            case "LIKECOUNT" -> {
                return new HospitalReviewsCursorResDto(list, nextCursorId, null, list.size()>0?list.get(list.size()-1).getLikeCount():null, hasNext);
            }
            case "CREATEDAT" -> {
                return new HospitalReviewsCursorResDto(list, nextCursorId, null, null, hasNext);
            }
            default -> {
                return new HospitalReviewsCursorResDto(list, nextCursorId, null, null, hasNext);
            }
        }

    }


    @Transactional
    public ReviewSaveResDto saveHospitalReviewProcess(ReviewSaveReqDto saveReqDto) {

        List<ReviewSaveReqDto.Images> images = saveReqDto.getImages();

        if(images.size()>5) throw new CustomException(ErrorCode.IMAGE_COUNT_EXCEEDED);

        Users currentUser = userUtil.getCurrentUser();
        Hospital hospital = hospitalJpaRepository.findById(saveReqDto.getHospitalId()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        Double facilityRating = saveReqDto.getFacilityRating();
        Double expertiseRating = saveReqDto.getExpertiseRating();
        Double kindnessRating = saveReqDto.getKindnessRating();

        Double overallRating = (expertiseRating + facilityRating + kindnessRating) / 3;

        UserReview userReview = UserReview.builder()
                .receiptCheck(saveReqDto.getReceiptChecked())
                .user(currentUser)
                .hospital(hospital)
                .visitDate(saveReqDto.getVisitDate())
                .animalType(saveReqDto.getAnimalType())
                .detailAnimalType(saveReqDto.getDetailAnimalType())
                .treatmentService(saveReqDto.getTreatmentService())
                .reviewContent(saveReqDto.getReviewComment())
                .expertiseRating(expertiseRating)
                .kindnessRating(kindnessRating)
                .facilityRating(facilityRating)
                .overallRating(overallRating)
                .totalPrice(saveReqDto.getTotalPrice())
                .build();

        reviewJpaRepository.save(userReview);

        List<ReviewSaveResDto.UrlAndId> list = new LinkedList<>();

        for(ReviewSaveReqDto.Images image : images){

            String filename = image.getFilename();
            String contentType = image.getContentType();

            String key = "reviewImage/" + UUID.randomUUID() + "_" + filename;

            URL presignedPutUrl = s3Service.createPresignedPutUrl(key, contentType, 300);

            list.add(new ReviewSaveResDto.UrlAndId(presignedPutUrl, key));
        }

        return new ReviewSaveResDto(userReview.getId(), list);

    }

    @Transactional
    public void reviewImageSaveCheckProcess(ReviewImageCheckReqDto reviewImageCheckReqDto) {

        Users currentUser = userUtil.getCurrentUser();

        Long reviewId = reviewImageCheckReqDto.getReviewId();
        List<String> keys = reviewImageCheckReqDto.getKeys();

        UserReview userReview = reviewJpaRepository.findByIdAndUserId(reviewId, currentUser.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        Boolean[] exists = new Boolean[keys.size()];
        Boolean allExists = true;

        for(int i=0; i<exists.length; i++){
            boolean b = s3Service.doesObjectExist(keys.get(i));
            if(!b){
                allExists = false;
            }
            exists[i] = b;
        }

        if(allExists){
            for(String key : keys){
                UserReviewImage userReviewImage = UserReviewImage.builder()
                        .imageUrl(s3Service.getObject(key))
                        .review(userReview)
                        .build();

                reviewImageJpaRepository.save(userReviewImage);
            }
        }else{
            for(int i=0; i<exists.length; i++){
                if(exists[i]){
                    try {
                        s3Service.deleteObject(keys.get(i));
                    } catch (Exception e) {
                        log.warn("S3 삭제 실패 : {}", keys.get(i), e);
                    }
                }
            }
            reviewJpaRepository.deleteById(reviewId);
            throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
        }


    }


}
