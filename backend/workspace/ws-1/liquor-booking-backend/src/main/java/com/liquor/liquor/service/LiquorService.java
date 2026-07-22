package com.liquor.liquor.service;

import java.util.List;
import java.util.UUID;

import com.liquor.liquor.dto.LiquorRequest;
import com.liquor.liquor.dto.LiquorResponse;
import com.liquor.liquor.entity.Liquor;

public interface LiquorService {

    LiquorResponse addLiquor(LiquorRequest request);

    List<LiquorResponse> getAllLiquors();

    LiquorResponse getLiquorById(UUID id);

    LiquorResponse updateLiquor(UUID id, LiquorRequest request);

    void deleteLiquor(UUID id);

    Liquor getLiquorEntity(UUID id);

}