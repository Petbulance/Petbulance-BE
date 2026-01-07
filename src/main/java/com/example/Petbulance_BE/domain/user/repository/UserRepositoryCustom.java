package com.example.Petbulance_BE.domain.user.repository;

import com.example.Petbulance_BE.domain.admin.user.dto.GetUserQueryParam;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUsersResDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {

    PageImpl<GetUsersResDto> adminGetUsers(Pageable pageable, GetUserQueryParam getUserQueryParam);

}
