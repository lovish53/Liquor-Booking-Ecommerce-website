package com.liquor.liquor.dto;

import com.liquor.liquor.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserUpdateRequest {

    private Boolean active;

    private Role role;
}
