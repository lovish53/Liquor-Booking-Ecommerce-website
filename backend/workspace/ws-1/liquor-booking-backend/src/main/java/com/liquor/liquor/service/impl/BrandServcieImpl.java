package com.liquor.liquor.service.impl;

import java.util.List;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.BrandRequest;
import com.liquor.liquor.dto.BrandResponse;
import com.liquor.liquor.entity.Brand;
import com.liquor.liquor.entity.Category;
import com.liquor.liquor.mapper.BrandMapper;
import com.liquor.liquor.repository.BrandRepository;
import com.liquor.liquor.service.BrandService;
import com.liquor.liquor.service.CategoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServcieImpl implements BrandService {

	private static final Logger logger =
            LoggerFactory.getLogger(BrandServcieImpl.class);
	private final BrandRepository brandRepository;

    private final CategoryService categoryService;

    private final BrandMapper brandMapper;
	
	@Override
	public BrandResponse addBrand(BrandRequest request) {
		
		 logger.info("Adding brand : {}", request.getName());
		 if (brandRepository.existsByName(request.getName())) {

		        logger.warn("Brand already exists : {}", request.getName());

		        throw new RuntimeException("Brand already exists");
		    }
		 Category category =
		            categoryService.getCategoryEntity(request.getCategoryId());
		 
		 Brand brand =
		            brandMapper.toEntity(request);
		
		    brand.setCategory(category);

		    brand.setActive(true);

		    brand = brandRepository.save(brand);

		    logger.info("Brand created successfully. Brand Id : {}", brand.getId());

		    return brandMapper.toResponse(brand);
	}

	@Override
	public List<BrandResponse> getAllBrands() {
		logger.info("Fetching all brands");

	    return brandRepository.findAll()
	            .stream()
	            .map(brandMapper::toResponse)
	            .toList();
	}

	@Override
	public BrandResponse getBrandById(UUID id) {
		Brand brand = findBrandById(id);

	    return brandMapper.toResponse(brand);
	}

	@Override
	public BrandResponse updateBrand(UUID id, BrandRequest request) {
		logger.info("Updating brand : {}", id);

	    Brand brand = findBrandById(id);
	    if (!brand.getName().equalsIgnoreCase(request.getName())
	            && brandRepository.existsByName(request.getName())) {

	        logger.warn("Brand already exists : {}", request.getName());

	        throw new RuntimeException("Brand already exists");
	    }
	    Category category =
	            categoryService.getCategoryEntity(request.getCategoryId());

	    brandMapper.updateEntity(request, brand);

	    brand.setCategory(category);

	    brand = brandRepository.save(brand);

	    logger.info("Brand updated successfully : {}", brand.getId());

	    return brandMapper.toResponse(brand);
	    
	    
	}

	@Override
	public void deleteBrand(UUID id) {
		logger.info("Deleting brand : {}", id);

	    Brand brand = findBrandById(id);

	    brandRepository.delete(brand);

	    logger.info("Brand deleted successfully : {}", id);
		
	}
	
	private Brand findBrandById(UUID id){

	    logger.info("Fetching brand : {}", id);

	    return brandRepository.findById(id)
	            .orElseThrow(() -> {

	                logger.warn("Brand not found : {}", id);

	                return new RuntimeException("Brand not found");

	            });

	}
	
	@Override
	public Brand getBrandEntity(UUID id) {

	    return findBrandById(id);

	}
	
}
