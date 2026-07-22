package com.liquor.liquor.service.impl;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.LiquorRequest;
import com.liquor.liquor.dto.LiquorResponse;
import com.liquor.liquor.entity.Brand;
import com.liquor.liquor.entity.Liquor;
import com.liquor.liquor.mapper.LiquorMapper;
import com.liquor.liquor.repository.LiquorRepository;
import com.liquor.liquor.service.BrandService;
import com.liquor.liquor.service.LiquorService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LiquorServiceImpl implements LiquorService {

    private static final Logger logger =
            LoggerFactory.getLogger(LiquorServiceImpl.class);

    private final LiquorRepository liquorRepository;

    private final BrandService brandService;

    private final LiquorMapper liquorMapper;

    @Override
    public LiquorResponse addLiquor(LiquorRequest request) {

        logger.info("Adding liquor : {}", request.getName());

        if (liquorRepository.existsByName(request.getName())) {

            logger.warn("Liquor already exists : {}", request.getName());

            throw new RuntimeException("Liquor already exists");
        }

        Brand brand =
                brandService.getBrandEntity(request.getBrandId());

        Liquor liquor =
                liquorMapper.toEntity(request);

        liquor.setBrand(brand);

        liquor.setActive(true);

        liquor = liquorRepository.save(liquor);

        logger.info("Liquor created successfully : {}", liquor.getId());

        return liquorMapper.toResponse(liquor);

    }

    @Override
    public List<LiquorResponse> getAllLiquors() {

        logger.info("Fetching all liquors");

        return liquorRepository.findAll()
                .stream()
                .map(liquorMapper::toResponse)
                .toList();

    }

    @Override
    public LiquorResponse getLiquorById(UUID id) {

        Liquor liquor = findLiquorById(id);

        return liquorMapper.toResponse(liquor);

    }

    @Override
    public LiquorResponse updateLiquor(UUID id,
                                       LiquorRequest request) {

        logger.info("Updating liquor : {}", id);

        Liquor liquor = findLiquorById(id);

        if (!liquor.getName().equalsIgnoreCase(request.getName())
                && liquorRepository.existsByName(request.getName())) {

            logger.warn("Liquor already exists : {}", request.getName());

            throw new RuntimeException("Liquor already exists");
        }

        Brand brand =
                brandService.getBrandEntity(request.getBrandId());

        liquorMapper.updateEntity(request, liquor);

        liquor.setBrand(brand);

        liquor = liquorRepository.save(liquor);

        logger.info("Liquor updated successfully : {}", liquor.getId());

        return liquorMapper.toResponse(liquor);

    }

    @Override
    public void deleteLiquor(UUID id) {

        logger.info("Deleting liquor : {}", id);

        Liquor liquor = findLiquorById(id);

        liquorRepository.delete(liquor);

        logger.info("Liquor deleted successfully : {}", id);

    }

    @Override
    public Liquor getLiquorEntity(UUID id) {

        return findLiquorById(id);

    }

    private Liquor findLiquorById(UUID id) {

        logger.info("Fetching liquor : {}", id);

        return liquorRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Liquor not found : {}", id);

                    return new RuntimeException("Liquor not found");

                });

    }

}
