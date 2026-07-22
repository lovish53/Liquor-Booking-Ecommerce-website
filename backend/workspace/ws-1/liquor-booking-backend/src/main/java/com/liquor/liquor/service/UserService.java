package com.liquor.liquor.service;

import java.util.List;
import java.util.UUID;

import com.liquor.liquor.dto.UserRequest;
import com.liquor.liquor.dto.UserResponse;
import com.liquor.liquor.entity.UserEntity;

public interface UserService {
	UserResponse registerUser(UserRequest request);
	
	List<UserResponse> getAllUsers();
	
	UserResponse getUserById(UUID id);
	
	UserResponse updateUser(UUID id,
            UserRequest request);
	
	void deleteUser(UUID id);

	UserEntity getUserEntity(UUID id);

	UserEntity getUserByEmail(String email);
}
