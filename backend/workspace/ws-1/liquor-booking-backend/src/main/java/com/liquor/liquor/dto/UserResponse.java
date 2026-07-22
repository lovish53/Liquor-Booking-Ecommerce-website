package com.liquor.liquor.dto;

import java.util.UUID;

import com.liquor.liquor.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private UUID id;

    private String name;

    private String email;

    private String phoneNumber;

    private Role role;

    private Boolean active;
}
