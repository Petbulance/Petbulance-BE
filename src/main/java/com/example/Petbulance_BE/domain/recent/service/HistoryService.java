package com.example.Petbulance_BE.domain.recent.service;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.domain.hospital.repository.HospitalJpaRepository;
import com.example.Petbulance_BE.domain.recent.dto.*;
import com.example.Petbulance_BE.domain.recent.entity.History;
import com.example.Petbulance_BE.domain.recent.repository.HistoryJpaRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.Petbulance_BE.domain.recent.type.SearchType.SEARCHHOSPITAL;
import static com.example.Petbulance_BE.domain.recent.type.SearchType.WATCHHOSPITAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    UserUtil userUtil;
    private final HistoryJpaRepository historyJpaRepository;
    private final HospitalJpaRepository hospitalJpaRepository;

    public List<RecentHospitalResDto> recentHospitalsProcess() {

        Users currentUser = userUtil.getCurrentUser();

        List<History> histories = historyJpaRepository.findTop5ByUserAndSearchTypeOrderByCreatedAtDesc(currentUser, SEARCHHOSPITAL);

        List<RecentHospitalResDto> list = histories.stream().map(h -> RecentHospitalResDto.builder()
                .keywordId(h.getId())
                .keyword(h.getContent())
                .createdAt(h.getCreatedAt())
                .build()).toList();

        return list;
    }

    @Transactional
    public RecentHospitalSaveResDto recentHospitalSaveProcess(String hospitalName) {

        Users currentUser = userUtil.getCurrentUser();

        Optional<History> findHistory = historyJpaRepository.findByUserAndSearchTypeAndContent(currentUser, SEARCHHOSPITAL, hospitalName);

        History history;

        if(findHistory.isPresent()){
            history = findHistory.get();
            history.setCreatedAt(LocalDateTime.now());
        }else{
            history = History.builder()
                    .user(currentUser)
                    .searchType(SEARCHHOSPITAL)
                    .content(hospitalName)
                    .build();

            historyJpaRepository.save(history);
        }

        return new RecentHospitalSaveResDto(history.getId(), history.getContent(), history.getCreatedAt());
    }

    @Transactional
    public void recentHospitalDeleteProcess(Long keywordId) {
        Users currentUser = userUtil.getCurrentUser();
        if(!historyJpaRepository.existsByIdAndUser(keywordId, currentUser)){
            throw new CustomException(ErrorCode.NOT_FOUND_KEYWORD);
        }else{
            historyJpaRepository.deleteByIdAndUser(keywordId, currentUser);
        }



    }

    public List<ViewedHospitalDto> viewedHospitalProcess() {

        Users currentUser = userUtil.getCurrentUser();

        List<History> historyList = historyJpaRepository.findTop5ByUserAndSearchTypeOrderByCreatedAtDesc(currentUser, WATCHHOSPITAL);

        List<ViewedHospitalDto> list = historyList.stream().map(h -> ViewedHospitalDto.builder()
                .hospitalId(h.getHospitalId())
                .name(h.getHospitalName())
                .viewedAt(h.getCreatedAt())
                .build()).toList();

        return list;

    }

    @Transactional
    public ViewedHospitalSaveResDto viewedHospitalSaveProcess(ViewedHospitalSaveReqDto saveReqDto) {



        Hospital hospital = hospitalJpaRepository.findById(saveReqDto.getHospitalId()).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));
        Users currentUser = userUtil.getCurrentUser();

        Optional<History> optionalHistory = historyJpaRepository.findByUserAndSearchTypeAndHospitalId(currentUser, WATCHHOSPITAL, saveReqDto.getHospitalId());

        History history;

        if(optionalHistory.isPresent()){
            history = optionalHistory.get();
            history.setCreatedAt(LocalDateTime.now());
        }else{
            history = History.builder()
                    .searchType(WATCHHOSPITAL)
                    .hospitalName(hospital.getName())
                    .hospitalId(hospital.getId())
                    .user(currentUser)
                    .createdAt(LocalDateTime.now())
                    .build();

            historyJpaRepository.save(history);
        }

        return new ViewedHospitalSaveResDto(hospital.getId(), history.getCreatedAt());
    }

    @Transactional
    public void viewedHospitalDeleteProcess(Long hospitalId) {
        Users currentUser = userUtil.getCurrentUser();
        History hospital = historyJpaRepository.findByUserAndSearchTypeAndHospitalId(currentUser, WATCHHOSPITAL, hospitalId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HOSPITAL));

        historyJpaRepository.delete(hospital);

    }
}
