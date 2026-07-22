package com.liquor.liquor.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity{
	 @Id
	    @GeneratedValue(strategy = GenerationType.UUID)
	    private UUID id;
	 
	 @Column(nullable = false, unique = true, length = 100)
	    private String name;

	    @Column(length = 300)
	    private String description;

	    @Column(nullable = false)
	    @Builder.Default
	    private Boolean active = true;
	    
	    @OneToMany(
	            mappedBy = "category",
	            cascade = CascadeType.ALL,
	            fetch = FetchType.LAZY
	    )
	    @Builder.Default
	    private List<Brand> brands = new ArrayList<>();
}
