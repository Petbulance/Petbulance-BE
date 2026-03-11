package com.example.Petbulance_BE.domain.review.service;

import com.example.Petbulance_BE.domain.dashboard.service.DashboardMetricRedisService;
import com.example.Petbulance_BE.domain.device.entity.Device;
import com.example.Petbulance_BE.domain.device.repository.DeviceJpaRepository;
import com.example.Petbulance_BE.domain.hospital.dto.UserReviewSearchDto;
import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.repository.HospitalJpaRepository;
import com.example.Petbulance_BE.domain.notification.service.NotificationService;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.notification.type.NotificationType;
import com.example.Petbulance_BE.domain.review.aop.CheckReviewAvailable;
import com.example.Petbulance_BE.domain.review.aop.DailyLimit;
import com.example.Petbulance_BE.domain.review.dto.*;
import com.example.Petbulance_BE.domain.review.dto.MyReviewGetDto;
import com.example.Petbulance_BE.domain.review.dto.req.FilterReqDto;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewImageCheckReqDto;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewModifyReqDto;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewSaveReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.*;
import com.example.Petbulance_BE.domain.review.entity.UserReview;
import com.example.Petbulance_BE.domain.review.entity.UserReviewImage;
import com.example.Petbulance_BE.domain.review.entity.UserReviewLike;
import com.example.Petbulance_BE.domain.review.repository.ReviewImageJpaRepository;
import com.example.Petbulance_BE.domain.review.repository.ReviewJpaRepository;
import com.example.Petbulance_BE.domain.review.repository.ReviewLikeJpaRepository;
import com.example.Petbulance_BE.domain.review.type.ImageSaveType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.firebase.FcmService;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import com.example.Petbulance_BE.domain.review.dto.GeminiApiDto.*;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final HospitalJpaRepository hospitalJpaRepository;
    private final ReviewJpaRepository reviewJpaRepository;
    private final UserUtil userUtil;
    private final JWTUtil jwtUtil;
    private final S3Service s3Service;
    private final ReviewImageJpaRepository reviewImageJpaRepository;
    private final ReviewLikeJpaRepository reviewLikeJpaRepository;
    private final DashboardMetricRedisService dashboardMetricRedisService;
    private final UsersJpaRepository usersJpaRepository;
    private final NotificationService notificationService;
    private final DeviceJpaRepository deviceJpaRepository;
    private final FcmService fcmService;

    @Value("${gemini.api.url-with-key}")
    private String genimiApiUrl;

    @Value("${geo.api.uri}")
    private String geoApiUrl;

    @Value("${geo.api.key}")
    private String geoKey;

    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .optionalStart()
            .appendPattern(" HH:mm")
            .optionalStart()
            .appendPattern(":ss")
            .optionalEnd()
            .optionalEnd()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    @DailyLimit
    public Mono<ReceiptResDto> receiptExtractProcess(MultipartFile image) {

        //log.info("🚀 [{}] 요청 시작", Thread.currentThread().getName());

         return getExtractedDataMono(image)
//                 .doOnSubscribe(s ->
//                         log.info("📡 [{}] Gemini API 호출 시작", Thread.currentThread().getName()))
//                 .doOnNext(data ->
//                         log.info("✅ [{}] Gemini 응답 수신", Thread.currentThread().getName()))
                 .flatMap(extractedData -> {

             String address = extractedData.address();
             String addressType = extractedData.addressType();
             String time = extractedData.paymentTime();
             Long price = extractedData.totalAmount();
             List<Item> items = extractedData.items();

                     if(address == null || address.isEmpty()) {
                 return Mono.error(new CustomException(ErrorCode.NO_ADDRESS_FOUND));
             }

             LocalDateTime finalPaymentDateTime = LocalDateTime.parse(extractedData.paymentTime().trim(), FLEXIBLE_FORMATTER);

             return geocodeAddress(address, addressType)
//                     .doOnSubscribe(s ->
//                             log.info("🌍 [{}] 지오코딩 API 호출", Thread.currentThread().getName()))
//                     .doOnNext(point ->
//                             log.info("📍 [{}] 지오코딩 응답 수신", Thread.currentThread().getName()))
                     .publishOn(Schedulers.boundedElastic()) //이 시점 이후의 작업은 블로킹 스레드 풀에서 동작, 논블로킹 스레드 풀은 막히지 않음
//                     .doOnNext(point ->
//                             log.info("💾 [{}] DB 조회 시작 (boundedElastic)", Thread.currentThread().getName()))
                     .flatMap(point -> { // 1. map 대신 flatMap 사용
                         double lat = point.y();
                         double lng = point.x();

                         log.info("x위도{}", lat);
                         log.info("y경도{}", lng);

                         List<Hospital> nearestHospitals = hospitalJpaRepository.findNearestHospitals(lat, lng, 3000);

                         if(nearestHospitals.isEmpty()) {

                             return Mono.error(new CustomException(ErrorCode.NOT_FOUND_RECEIPT_HOSPITAL));
                         }

                         Hospital hospital = nearestHospitals.get(0);

                         // 3. 정상 결과는 Mono.just로 감싸서 반환
                         return Mono.just(ReceiptResDto.builder()
                                 .hospitalId(hospital.getId())
                                 .hospitalName(hospital.getName())
                                 .visitDateTime(finalPaymentDateTime)
                                 .price(price)
                                 .items(items)
                                 .build());
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
                    if (root == null || root.response() == null || root.response().result() == null || root.response().result().point() == null) {
                        log.error("📍 지오코딩 실패 — 결과가 없습니다. 응답: {}", root);
                        return Mono.error(new CustomException(ErrorCode.FAIL_GEOCODING));
                    }
                    return Mono.just(root.response().result().point());
                });
    }

    private Mono<ExtractedData> getExtractedDataMono(MultipartFile image) {
        GeminiRequest requestBody = createGeminiRequest(image);

        return webClient.post()
                .uri(genimiApiUrl)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .flatMap(this::extractRawText)        // 1. 텍스트 추출 및 정제
                .flatMap(this::parseAndValidateJson)  // 2. JSON 파싱 및 검증
                .onErrorResume(this::handleError);    // 3. 에러 핸들링
    }

    private GeminiRequest createGeminiRequest(MultipartFile image) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String prompt = GeminiApiDto.RECEIPT_EXTRACTION_PROMPT;

            Part textPart = new Part(prompt, null);
            Part imagePart = new Part(null, new InlineData(image.getContentType(), base64Image));

            return new GeminiRequest(List.of(new Content(List.of(textPart, imagePart))));
        } catch (IOException e) {
            log.error("이미지 변환 실패");
            throw new CustomException(ErrorCode.FAIL_RECEIPT_EXTRACT); // 런타임 예외보다 커스텀 예외 권장
        }
    }

    private Mono<String> extractRawText(GeminiResponse response) {
        return Mono.fromCallable(() -> {
            String rawText = response.candidates().get(0).content().parts().get(0).text();
            return rawText.replace("```json", "").replace("```", "").trim();
        }).onErrorResume(e -> Mono.error(new CustomException(ErrorCode.FAIL_RECEIPT_EXTRACT)));
    }

    private Mono<ExtractedData> parseAndValidateJson(String jsonString) {
        return Mono.fromCallable(() -> objectMapper.readValue(jsonString, GeminiJsonOutput.class))
                .flatMap(output -> {
                    if ("success".equals(output.status())) {
                        return Mono.just(output.data());
                    }
                    return Mono.error(new CustomException(ErrorCode.FAIL_RECEIPT_EXTRACT));
                })
                .onErrorResume(e -> {
                    if (e instanceof CustomException) return Mono.error(e);
                    log.error("Gemini JSON 파싱 오류: {}", e.getMessage());
                    return Mono.error(new RuntimeException("JSON 파싱 오류", e));
                });
    }

    private Mono<ExtractedData> handleError(Throwable e) {
        if (e instanceof CustomException ce) {
            return Mono.error(ce);
        } else if (e instanceof WebClientResponseException wcre) {
            log.error("외부 API 호출 실패: {}", wcre.getResponseBodyAsString());
            return Mono.error(new CustomException(ErrorCode.FAIL_API_CONNECT));
        }
        log.error("예상치 못한 오류 발생", e);
        return Mono.error(new CustomException(ErrorCode.FAIL_WHILE_API));
    }

    public FindHospitalResDto findHospitalProcess(String hospitalName) {
        List<Hospital> nameStartsWith = hospitalJpaRepository.findByNameStartsWith(hospitalName);

        List<FindHospitalResDto.HospitalDto> list = nameStartsWith.stream()
                .map(h ->
                        new FindHospitalResDto.HospitalDto(h.getId(), h.getName()))
                .toList();

        return new FindHospitalResDto(list);
    }

    @Transactional(readOnly = true)
    public CursorPagingResDto<UserReviewSearchDto> searchReviewProcess(String value, Long cursorId, int size) {

        Users currentUser = userUtil.getCurrentUser();

        Pageable pageable = PageRequest.of(0, size + 1);

        List<UserReview> ur = reviewJpaRepository.findByHospitalNameOrTreatmentService(value, cursorId, pageable);

        List<Long> reviewIds = ur.stream().map(UserReview::getId).toList();

        Set<Long> likedReviewIds = (currentUser != null && !reviewIds.isEmpty())
                ? reviewJpaRepository.findLikedReviewIds(currentUser.getId(), reviewIds)
                : Collections.emptySet();

        List<UserReviewSearchDto> reviews = ur.stream().map(r -> UserReviewSearchDto.builder()
                .userNickname(r.getUser().getNickname())
                .receiptCheck(r.getReceiptCheck())
                .id(r.getId())
                .hospitalImage(r.getHospital().getImage())
                .hospitalId(r.getHospital().getId())
                .hospitalName(r.getHospital().getName())
                .treatmentService(r.getTreatmentService())
                .animalType(r.getAnimalType())
                .detailAnimalType(r.getDetailAnimalType().name())
                .reviewContent(r.getReviewContent())
                .overallRating(r.getOverallRating())
                .createdDate(r.getCreatedAt().toLocalDate())
                .images(r.getImages().stream().map(UserReviewImage::getImageUrl).toList())
                .totalPrice(r.getTotalPrice())
                .likedCount((long) r.getLikes().size())
                .liked(likedReviewIds.contains(r.getId()))
                .build()).toList();

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

        if (!limitedList.isEmpty()) {
            // 1. 결과 리스트에서 리뷰 ID들만 추출
            List<Long> reviewIds = limitedList.stream()
                    .map(FilterResDto::getId)
                    .toList();

            // 2. ID 리스트를 이용해 이미지 맵 가져오기 (리뷰ID : 이미지URL리스트)
            Map<Long, List<String>> imageMap = reviewJpaRepository.findImagesByReviewIds(reviewIds);

            // 3. 각 DTO에 이미지 셋팅 (이미지가 없는 경우 빈 리스트)
            limitedList.forEach(dto ->
                    dto.setImages(imageMap.getOrDefault(dto.getId(), Collections.emptyList()))
            );
        }

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
                        .userNickname(r.getUser().getNickname())
                        .receiptCheck(r.getReceiptCheck())
                        .id(r.getId())
                        .treatmentService(r.getTreatmentService())
                        .animalType(r.getAnimalType())
                        .detailAnimalType(r.getDetailAnimalType().name())
                        .reviewContent(r.getReviewContent())
                        .totalRating(r.getOverallRating())
                        .totalPrice(r.getTotalPrice())
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


    @CheckReviewAvailable
    @Transactional
    public ReviewSaveResDto saveHospitalReviewProcess(ReviewSaveReqDto saveReqDto) {

        List<ReviewSaveReqDto.Images> images = saveReqDto.getImages();

        if(images.size()>5) throw new CustomException(ErrorCode.IMAGE_COUNT_EXCEEDED);

        Users currentUser = userUtil.getCurrentUser();

        Hospital hospital = hospitalJpaRepository.findById(saveReqDto.getHospitalId()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        Double facilityRating = saveReqDto.getFacilityRating();
        Double expertiseRating = saveReqDto.getExpertiseRating();
        Double kindnessRating = saveReqDto.getKindnessRating();

        String combinedService = saveReqDto.getReceiptItems().stream()
                .map(item -> item.getName() + "(" + String.format("%,d", item.getPrice()) + "원)")
                .collect(Collectors.joining(", "));

        Double overallRating = (expertiseRating + facilityRating + kindnessRating) / 3;

        UserReview userReview = UserReview.builder()
                .title(saveReqDto.getTitle())
                .receiptCheck(saveReqDto.getReceiptChecked())
                .user(currentUser)
                .hospital(hospital)
                .visitDate(saveReqDto.getVisitDate())
                .animalType(saveReqDto.getAnimalType())
                .detailAnimalType(saveReqDto.getDetailAnimalType())
                .treatmentService(combinedService)
                .reviewContent(saveReqDto.getReviewComment())
                .expertiseRating(expertiseRating)
                .kindnessRating(kindnessRating)
                .facilityRating(facilityRating)
                .overallRating(overallRating)
                .totalPrice(saveReqDto.getTotalPrice())
                .build();

        reviewJpaRepository.save(userReview);

        try {
            dashboardMetricRedisService.incrementTodayReviewCreated();
        } catch (Exception e) {
            log.warn("Failed to increment review_created_count", e);
        }

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
    public Map<String, String> reviewImageSaveCheckProcess(ReviewImageCheckReqDto reviewImageCheckReqDto) {

        Users currentUser = userUtil.getCurrentUser();

        ImageSaveType type = reviewImageCheckReqDto.getType();

        Long reviewId = reviewImageCheckReqDto.getReviewId();
        List<String> keys = reviewImageCheckReqDto.getKeys();

        UserReview userReview = reviewJpaRepository.findByIdAndUserId(reviewId, currentUser).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        Boolean[] exists = new Boolean[keys.size()];
        Boolean allExists = true;

        for(int i=0; i<exists.length; i++){
            boolean b = s3Service.doesObjectExist(keys.get(i));
            if(!b){
                allExists = false;
            }
            exists[i] = b;
        }

        if(allExists && type.equals(ImageSaveType.NEW)){
            for(String key : keys){
                UserReviewImage userReviewImage = UserReviewImage.builder()
                        .imageUrl(s3Service.getObject(key))
                        .review(userReview)
                        .build();

                reviewImageJpaRepository.save(userReviewImage);
            }
        }else if(allExists && type.equals(ImageSaveType.UPDATE)) {
            reviewImageJpaRepository.deleteByReviewId(reviewId);
            for(String key : keys) {
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
            if(type.equals(ImageSaveType.NEW)){
                reviewJpaRepository.deleteById(reviewId);
            }

            throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
        }

        return Map.of("message", "success");


    }

    public MyReviewGetResDto myReviewGetProcess(Long cursorId, int size){

        Users currentUser = userUtil.getCurrentUser();

        if(currentUser==null){
            throw new CustomException(ErrorCode.NON_EXIST_USER);
        }

        Pageable pageable = PageRequest.of(0,size+1);

        List<MyReviewGetDto> list = reviewJpaRepository.findByUserIdAndCursorId(currentUser, cursorId, pageable);

        Boolean hasNext = list.size() > size;

        Long nextCursorId = null;

        List<MyReviewGetDto> limitedList = hasNext ? list.subList(0, size): list;

        nextCursorId = hasNext ? limitedList.get(limitedList.size()-1).getId() : null;


        return new MyReviewGetResDto(limitedList, nextCursorId, hasNext);

    }

    @Transactional
    public void myReviewDeleteProcess(List<Long> receiptIds) {

        Users currentUserById = userUtil.getCurrentUser();

        for(Long receiptId:receiptIds) {

            Optional<UserReview> review = reviewJpaRepository.findByUserId(currentUserById, receiptId);

            if (review.isEmpty()) {
                throw new CustomException(ErrorCode.NOT_FOUND_REVIEW);
            }

            review.get().setDeleted(true);
        }

    }

    @Transactional
    public Map<String, String> reviewLikeProcess(Long reviewId) {

        Users currentUser = userUtil.getCurrentUser();
        Boolean exists = reviewLikeJpaRepository.existsByUserAndReviewId(currentUser, reviewId);
        log.info("{}", exists);
        UserReview reviewProxy = reviewJpaRepository.getReferenceById(reviewId);

        if(exists){
            throw new CustomException(ErrorCode.ALREADY_LIKED_REVIEW);
        }

        UserReviewLike userReviewLike = new UserReviewLike();
        userReviewLike.setUser(currentUser);
        userReviewLike.setReview(reviewProxy);
        reviewLikeJpaRepository.save(userReviewLike);


        sendReviewPushAlram(reviewProxy, currentUser);

        return Map.of("message", "success");
    }

    private void sendReviewPushAlram(UserReview reviewProxy, Users currentUser) {
        Device device = deviceJpaRepository.findByUserId(reviewProxy.getUser().getId());

        String message = reviewProxy.getHospital().getName() + "병원에 남긴 후기가 다른 보호자에게 도움이 되었어요.";

        if (device != null && device.getFcm_token() != null) {
            String fcmToken = device.getFcm_token();

            Map<String, String> data = new HashMap<>();
            data.put("type", "REVIEW");                          // 이동할 페이지 타입 (병원 후기)
            data.put("targetId", String.valueOf(reviewProxy.getId())); // 해당 후기의 PK

            String title = "후기 도움 알림";
            fcmService.sendPushNotification(fcmToken, title, message, data);
        }

        notificationService.createNotification(
                reviewProxy.getUser(),
                currentUser,
                NotificationType.REVIEW_HELPFUL,
                NotificationTargetType.REVIEW,
                reviewProxy.getId(),
                message
        );
    }

    public Map<String, String> reviewLikeCancelProcess(Long reviewId) {

        Users currentUser = userUtil.getCurrentUser();
        Optional<UserReviewLike> byUserAndReviewId = reviewLikeJpaRepository.findByUserAndReviewId(currentUser, reviewId);

        if(byUserAndReviewId.isEmpty()){
            throw new CustomException(ErrorCode.NON_EXIST_LIKE);
        }

        reviewLikeJpaRepository.delete(byUserAndReviewId.get());

        return Map.of("message", "success");

    }

    //서비스 코드
    public Mono<ReceiptResDto> receiptExtractProcessMock(MultipartFile image) {
        log.info("[MOCK TEST] 요청 시작");

        // 메서드의 시작이 곧 return이며, 세미콜론(;)은 마지막에 딱 한 번 나옵니다.
        return getMockExtractedData()
                .flatMap(this::enrichWithHospitalData);
    }

    private Mono<ReceiptResDto> enrichWithHospitalData(ExtractedData data) {
        // 흐름: 시간 파싱 -> 주소 검증 -> 지오코딩 -> DB 조회 -> DTO 생성
        return parseTimeMono(data.paymentTime())
                .flatMap(time -> validateAndGeocode(data.address(), data.addressType())
                        .publishOn(Schedulers.boundedElastic())
                        .map(point -> findNearestHospital(point.y(), point.x()))
                        .map(hospital -> ReceiptResDto.builder()
                                .hospitalId(hospital.getId())
                                .hospitalName(hospital.getName())
                                .visitDateTime(time)
                                .price(data.totalAmount())
                                .build())
                );
    }

    private Mono<GeoDto.PointDTO> validateAndGeocode(String address, String addressType) {
        return Mono.justOrEmpty(address)
                .filter(addr -> !addr.isBlank())
                // 주소가 비어있으면 바로 에러 Mono를 반환하여 흐름 중단
                .switchIfEmpty(Mono.error(new CustomException(ErrorCode.NO_ADDRESS_FOUND)))
                .flatMap(addr -> geocodeAddress(addr, addressType));
    }

    private Mono<LocalDateTime> parseTimeMono(String time) {
        return Mono.fromCallable(() -> {
            try {
                return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                return LocalDate.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            }
        }).onErrorReturn(LocalDateTime.now()); // 에러 발생 시 기본값 반환으로 흐름 유지
    }

    private Hospital findNearestHospital(double lat, double lng) {
        return hospitalJpaRepository.findNearestHospitals(lat, lng, 3000)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RECEIPT_HOSPITAL));
    }


    //테스트용 Mock 데이터 생성기 (1초 딜레이)
    private Mono<ExtractedData> getMockExtractedData() {
        return Mono.delay(Duration.ofSeconds(1)) // 1초 Non-blocking 대기
                .map(ignore -> new ExtractedData(
                        "테스트 동물병원",
                        List.of(),
                        15000L,
                        "서울특별시 양천구 지양로 7", // 테스트용 고정 주소
                        "road",
                        "2025-01-01 12:00:00"
                ));
    }

    public DetailResDto getDetailReviewProcess(Long reviewId) {

        if(!reviewJpaRepository.existsById(reviewId)) throw new CustomException(ErrorCode.NOT_FOUND_REVIEW);

        DetailResDto filterResDto = reviewJpaRepository.reviewFilterQuery(reviewId);

        Map<Long, List<String>> imagesByReviewIds = reviewJpaRepository.findImagesByReviewIds(List.of(reviewId));

        filterResDto.setImages(imagesByReviewIds.get(reviewId));

        return filterResDto;

    }

    @Transactional
    @CheckReviewAvailable
    public ReviewSaveResDto modifyReviewProcess(ReviewModifyReqDto reviewModifyReqDto) {

        Users currentUser = userUtil.getCurrentUser();

        Long reviewId = reviewModifyReqDto.getReviewId();

        UserReview userReview1 = reviewJpaRepository.findByIdAndUserId(reviewId, currentUser).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        List<ReviewSaveReqDto.Images> images = reviewModifyReqDto.getImages();

        if(images.size()>5) throw new CustomException(ErrorCode.IMAGE_COUNT_EXCEEDED);

        Hospital hospital = hospitalJpaRepository.findById(reviewModifyReqDto.getHospitalId()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        Double facilityRating = reviewModifyReqDto.getFacilityRating();
        Double expertiseRating = reviewModifyReqDto.getExpertiseRating();
        Double kindnessRating = reviewModifyReqDto.getKindnessRating();

        String combinedService = reviewModifyReqDto.getReceiptItems().stream()
                .map(item -> item.getName() + "(" + String.format("%,d", item.getPrice()) + "원)")
                .collect(Collectors.joining(", "));

        Double overallRating = (expertiseRating + facilityRating + kindnessRating) / 3;

        userReview1.setTitle(reviewModifyReqDto.getTitle());
        userReview1.setReceiptCheck(reviewModifyReqDto.getReceiptChecked());
        userReview1.setUser(currentUser);
        userReview1.setHospital(hospital);
        userReview1.setVisitDate(reviewModifyReqDto.getVisitDate());
        userReview1.setAnimalType(reviewModifyReqDto.getAnimalType());
        userReview1.setDetailAnimalType(reviewModifyReqDto.getDetailAnimalType());
        userReview1.setTreatmentService(combinedService);
        userReview1.setReviewContent(reviewModifyReqDto.getReviewComment());
        userReview1.setExpertiseRating(expertiseRating);
        userReview1.setKindnessRating(kindnessRating);
        userReview1.setFacilityRating(facilityRating);
        userReview1.setOverallRating(overallRating);
        userReview1.setTotalPrice(reviewModifyReqDto.getTotalPrice());

        List<ReviewSaveResDto.UrlAndId> list = new LinkedList<>();

        for(ReviewSaveReqDto.Images image : images){

            String filename = image.getFilename();
            String contentType = image.getContentType();

            String key = "reviewImage/" + UUID.randomUUID() + "_" + filename;

            URL presignedPutUrl = s3Service.createPresignedPutUrl(key, contentType, 300);

            list.add(new ReviewSaveResDto.UrlAndId(presignedPutUrl, key));
        }

        return new ReviewSaveResDto(userReview1.getId(), list);

    }
}
