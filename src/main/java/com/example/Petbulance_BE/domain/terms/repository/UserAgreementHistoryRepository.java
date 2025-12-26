package com.example.Petbulance_BE.domain.terms.repository;

import com.example.Petbulance_BE.domain.terms.enums.TermsType;
import com.example.Petbulance_BE.domain.terms.entity.UserAgreementHistory;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAgreementHistoryRepository extends JpaRepository<UserAgreementHistory, Long> {

    @Query("SELECT uh FROM UserAgreementHistory uh JOIN FETCH uh.terms t WHERE uh.user = :user AND uh.terms.type = :type ORDER BY uh.agreedAt DESC")
    List<UserAgreementHistory> findLatestAgreement(@Param("user") Users user, @Param("type") TermsType type);

    @Query("SELECT count(DISTINCT uh.terms.type) FROM UserAgreementHistory uh WHERE uh.user = :userAND AND uh.isAgreed = true AND (uh.terms.type = 'SERVICE' OR uh.terms.type = 'PRIVACY' OR uh.terms.type = 'LOCATION')")
    Integer countUserAgreement(@Param("user") Users user);

    @Query("SELECT uah FROM UserAgreementHistory uah " +
            "JOIN FETCH uah.terms t " +
            "WHERE uah.id IN (" +
            "  SELECT MAX(uah2.id) " +
            "  FROM UserAgreementHistory uah2 " +
            "  WHERE uah2.user.id = :userId " +
            "  AND uah2.isAgreed = true" +
            "  GROUP BY uah2.terms.type" +
            ")")
    List<UserAgreementHistory> findLatestThumbprints(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE UserAgreementHistory uah SET uah.isAgreed = false WHERE uah.terms.type = :type AND uah.user = :user AND uah.isAgreed = true")
    Integer disAgreeTerms(@Param("type") TermsType type, @Param("user") Users user);
}
