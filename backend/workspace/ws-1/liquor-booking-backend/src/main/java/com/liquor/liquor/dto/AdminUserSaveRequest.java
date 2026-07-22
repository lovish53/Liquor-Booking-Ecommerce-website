package com.liquor.liquor.dto;

import com.liquor.liquor.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserSaveRequest {

    private String name;

    private String email;

    private String password;

    private String phoneNumber;

    private Role role;

    private Boolean active;
}
