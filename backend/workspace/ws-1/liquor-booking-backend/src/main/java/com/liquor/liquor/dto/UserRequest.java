package com.liquor.liquor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
	 @NotBlank(message = "Name is required")
	    private String name;
	 
	 @Email(message = "Invalid email")
	    @NotBlank(message = "Email is required")
	    private String email;
	 
	 @NotBlank(message = "Password is required")
	    private String password;
	 
	 @Pattern(
	            regexp = "^[0-9]{10}$",
	            message = "Phone number must contain exactly 10 digits"
	    )
	    private String phoneNumber;
}	
