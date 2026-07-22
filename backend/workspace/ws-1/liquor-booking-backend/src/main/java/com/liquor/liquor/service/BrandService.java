package com.liquor.liquor.service;

import java.util.List;
import java.util.UUID;

import com.liquor.liquor.dto.BrandRequest;
import com.liquor.liquor.dto.BrandResponse;
import com.liquor.liquor.entity.Brand;

public interface BrandService {
	 BrandResponse addBrand(BrandRequest request);

	    List<BrandResponse> getAllBrands();

	    BrandResponse getBrandById(UUID id);

	    BrandResponse updateBrand(UUID id,
	                              BrandRequest request);

	    void deleteBrand(UUID id);
	    
	    Brand getBrandEntity(UUID id);
}
