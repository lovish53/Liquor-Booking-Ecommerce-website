package com.liquor.liquor.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.liquor.liquor.entity.BottleSize;
import com.liquor.liquor.entity.Brand;
import com.liquor.liquor.entity.Category;
import com.liquor.liquor.entity.Liquor;
import com.liquor.liquor.entity.Role;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.repository.BrandRepository;
import com.liquor.liquor.repository.CategoryRepository;
import com.liquor.liquor.repository.LiquorRepository;
import com.liquor.liquor.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final LiquorRepository liquorRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedCatalog();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@liquor.local")) {
            return;
        }

        userRepository.save(UserEntity.builder()
                .name("Store Admin")
                .email("admin@liquor.local")
                .phoneNumber("9999999999")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .build());
    }

    private void seedCatalog() {
        if (liquorRepository.count() > 0) {
            return;
        }

        Category whisky = saveCategory("Whisky", "Single malts, blends and premium reserve bottles.");
        Category wine = saveCategory("Wine", "Red, white and sparkling selections for every occasion.");
        Category vodka = saveCategory("Vodka", "Clean classics and flavored bottles for cocktails.");

        Brand oakReserve = saveBrand("Oak Reserve", "Aged barrels with a smoky finish.", whisky);
        Brand duskVine = saveBrand("Dusk Vineyards", "Bright fruit-forward estate wines.", wine);
        Brand crystalNorth = saveBrand("Crystal North", "Ultra smooth vodka for modern serves.", vodka);

        saveLiquor("Oak Reserve 12", "Honey, oak spice and a long warming finish.",
                new BigDecimal("1450.00"), 28, 46.0, BottleSize.ML_750,
                "https://images.unsplash.com/photo-1527281400683-1aae777175f8?auto=format&fit=crop&w=900&q=80",
                oakReserve);
        saveLiquor("Midnight Barrel", "Dark caramel, charred oak and toasted vanilla.",
                new BigDecimal("1850.00"), 18, 48.0, BottleSize.ML_750,
                "https://images.unsplash.com/photo-1569529465841-dfecdab7503b?auto=format&fit=crop&w=900&q=80",
                oakReserve);
        saveLiquor("Dusk Merlot", "Soft tannins with plum, cocoa and berry aromatics.",
                new BigDecimal("980.00"), 36, 13.5, BottleSize.ML_750,
                "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?auto=format&fit=crop&w=900&q=80",
                duskVine);
        saveLiquor("Crystal North Classic", "Clean, chilled and citrus-ready.",
                new BigDecimal("1120.00"), 42, 40.0, BottleSize.ML_750,
                "https://images.unsplash.com/photo-1608885898957-a1dd7017f629?auto=format&fit=crop&w=900&q=80",
                crystalNorth);
    }

    private Category saveCategory(String name, String description) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .description(description)
                        .active(true)
                        .build()));
    }

    private Brand saveBrand(String name, String description, Category category) {
        return brandRepository.findByName(name)
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name(name)
                        .description(description)
                        .category(category)
                        .active(true)
                        .build()));
    }

    private void saveLiquor(String name,
                            String description,
                            BigDecimal sellingPrice,
                            Integer stock,
                            Double alcoholPercentage,
                            BottleSize bottleSize,
                            String imagePath,
                            Brand brand) {
        if (liquorRepository.existsByName(name)) {
            return;
        }

        liquorRepository.save(Liquor.builder()
                .name(name)
                .description(description)
                .purchasePrice(sellingPrice.multiply(new BigDecimal("0.65")))
                .sellingPrice(sellingPrice)
                .stock(stock)
                .bottleSize(bottleSize)
                .alcoholPercentage(alcoholPercentage)
                .imagePath(imagePath)
                .discountPercentage(0)
                .active(true)
                .brand(brand)
                .build());
    }
}
