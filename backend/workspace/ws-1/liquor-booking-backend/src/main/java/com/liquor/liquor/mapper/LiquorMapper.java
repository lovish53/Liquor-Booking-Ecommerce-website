package com.liquor.liquor.mapper;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.liquor.liquor.dto.LiquorRequest;
import com.liquor.liquor.dto.LiquorResponse;
import com.liquor.liquor.entity.Liquor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiquorMapper {

    private final ModelMapper modelMapper;

    public Liquor toEntity(LiquorRequest request){

        Liquor liquor = new Liquor();
        updateEntity(request, liquor);
        return liquor;

    }

    public LiquorResponse toResponse(Liquor liquor){

        LiquorResponse response =
                modelMapper.map(liquor, LiquorResponse.class);

        response.setBrandName(liquor.getBrand().getName());

        response.setCategoryName(
                liquor.getBrand()
                        .getCategory()
                        .getName());

        response.setFinalPrice(calculateFinalPrice(
                liquor.getSellingPrice(),
                liquor.getDiscountPercentage()));

        return response;

    }

    public void updateEntity(
            LiquorRequest request,
            Liquor liquor){

        liquor.setName(request.getName());
        liquor.setDescription(request.getDescription());
        liquor.setPurchasePrice(request.getPurchasePrice());
        liquor.setSellingPrice(request.getSellingPrice());
        liquor.setStock(request.getStock());
        liquor.setBottleSize(request.getBottleSize());
        liquor.setAlcoholPercentage(request.getAlcoholPercentage());
        liquor.setImagePath(request.getImagePath());
        liquor.setDiscountPercentage(request.getDiscountPercentage());

    }

    private BigDecimal calculateFinalPrice(
            BigDecimal sellingPrice,
            Integer discountPercentage){

        if(discountPercentage == null || discountPercentage == 0){
            return sellingPrice;
        }

        BigDecimal discount =
                sellingPrice.multiply(
                        BigDecimal.valueOf(discountPercentage))
                        .divide(BigDecimal.valueOf(100));

        return sellingPrice.subtract(discount);

    }

}
