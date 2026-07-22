package com.liquor.liquor.service.impl;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.UserRequest;
import com.liquor.liquor.dto.UserResponse;
import com.liquor.liquor.entity.Role;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.repository.UserRepository;
import com.liquor.liquor.service.UserService;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class UserServiceImpl implements UserService{

	
	private static final Logger logger=LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserRepository  userRepository;
	
	@Autowired
	 private  ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserResponse registerUser(UserRequest request) {
		
		logger.info("Registering with email : {}",request.getEmail());
		
		 if(userRepository.existsByEmail(request.getEmail())){
			 logger.warn("Registration failed. Email already exists: {}", request.getEmail());
		        throw new RuntimeException("Email already exists");
		    }

		    if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
		    	logger.warn("Registration failed. Phone number already exists: {}", request.getPhoneNumber());
		    	throw new RuntimeException("Phone number already exists");
		    }
		    
		    UserEntity user=modelMapper.map(request, UserEntity.class);
		    user.setPassword(passwordEncoder.encode(request.getPassword()));
		    
		    user.setRole(Role.USER);
		    user.setActive(true);
		    user=userRepository.save(user);
		    logger.info("User registered successfully with id: {}", user.getId());
		    
		    return modelMapper.map(user, UserResponse.class);
		
	}

	@Override
	public List<UserResponse> getAllUsers() {
		logger.info("Fetching all users");

		List<UserEntity>users=userRepository.findAll();
		
		  logger.info("Total users fetched: {}", users.size());
		return	users.stream().map(user->modelMapper.map(user, UserResponse.class)).toList();
	}

	@Override
	public UserResponse getUserById(UUID id) {

	    logger.info("Fetching user with id: {}", id);

	    UserEntity user = userRepository.findById(id)
	            .orElseThrow(() -> {

	                logger.warn("User not found with id: {}", id);

	                return new RuntimeException("User not found");
	            });

	    logger.info("User fetched successfully. User Id: {}", id);

	    return modelMapper.map(user, UserResponse.class);
	}
	
	
	@Override
	public UserResponse updateUser(UUID id, UserRequest request) {
		  UserEntity user = userRepository.findById(id)
		            .orElseThrow(() -> {

		                logger.warn("User not found while updating. User Id: {}", id);

		                return new RuntimeException("User not found");
		            });
		
		 user.setName(request.getName());

		    user.setEmail(request.getEmail());

		    user.setPhoneNumber(request.getPhoneNumber());
		    
		    if (request.getPassword() != null && !request.getPassword().isBlank()) {
		    	user.setPassword(passwordEncoder.encode(request.getPassword()));
		    }

		    userRepository.save(user);
		    logger.info("User updated successfully. User Id: {}", id);

		    return modelMapper.map(user,
		            UserResponse.class);
		
	}

	@Override
	public void deleteUser(UUID id) {

	    logger.info("Deleting user with id: {}", id);

	    UserEntity user = userRepository.findById(id)
	            .orElseThrow(() -> {

	                logger.warn("User not found while deleting. User Id: {}", id);

	                return new RuntimeException("User not found");
	            });

	    userRepository.delete(user);

	    logger.info("User deleted successfully. User Id: {}", id);
	}

	@Override
	public UserEntity getUserEntity(UUID id) {
	    return userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public UserEntity getUserByEmail(String email) {
	    return userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	}
	
	
	

}
