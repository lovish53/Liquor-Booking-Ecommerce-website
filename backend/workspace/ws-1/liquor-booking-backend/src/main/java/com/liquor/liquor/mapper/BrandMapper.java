package com.liquor.liquor.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.liquor.liquor.dto.BrandRequest;
import com.liquor.liquor.dto.BrandResponse;
import com.liquor.liquor.entity.Brand;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BrandMapper {
	
	private final ModelMapper modelMapper;

    public Brand toEntity(BrandRequest request){

        return modelMapper.map(request,
                Brand.class);

    }
    
    public BrandResponse toResponse(
            Brand brand){

        BrandResponse response =
                modelMapper.map(brand,
                        BrandResponse.class);

        response.setCategoryName(
                brand.getCategory().getName());

        response.setTotalLiquors(
                brand.getLiquors().size());

        return response;

    }
    
    public void updateEntity(
            BrandRequest request,
            Brand brand){

        modelMapper.map(request,
                brand);

    }
    

}
