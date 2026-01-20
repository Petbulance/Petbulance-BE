package com.example.Petbulance_BE.domain.admin.version.dto;

import com.example.Petbulance_BE.domain.admin.version.type.RegionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionAddReqDto {

    @NotBlank
    private String regionVersion;

    @NotBlank
    private RegionType type;

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private Long superior;

    @AssertTrue(message = "superior은 필수 입력값입니다.")
    public boolean isSuperior() {

        if(type == RegionType.REGION1){
            return true;
        }
        return superior != null;
    }

}
