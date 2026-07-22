package com.liquor.liquor.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	   @Column(nullable = false, length = 100)
	    private String name;

	    @Column(nullable = false, unique = true, length = 150)
	    private String email;

	    @Column(nullable = false)
	    private String password;

	    @Column(nullable = false, unique = true, length = 15)
	    private String phoneNumber;

	    
	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private Role role;
	    
	    @Column(nullable = false)
	    @Builder.Default
	    private Boolean active = true;
}
