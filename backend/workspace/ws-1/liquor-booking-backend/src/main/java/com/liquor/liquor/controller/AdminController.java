package com.liquor.liquor.controller;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.liquor.liquor.dto.AdminDashboardResponse;
import com.liquor.liquor.dto.AdminUserSaveRequest;
import com.liquor.liquor.dto.AdminUserUpdateRequest;
import com.liquor.liquor.dto.LiquorResponse;
import com.liquor.liquor.dto.OrderResponse;
import com.liquor.liquor.dto.PaymentStatusRequest;
import com.liquor.liquor.dto.RevenuePointResponse;
import com.liquor.liquor.dto.StockUpdateRequest;
import com.liquor.liquor.dto.UserResponse;
import com.liquor.liquor.entity.Liquor;
import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.OrderStatus;
import com.liquor.liquor.entity.Payment;
import com.liquor.liquor.entity.PaymentStatus;
import com.liquor.liquor.entity.Role;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.mapper.LiquorMapper;
import com.liquor.liquor.repository.LiquorRepository;
import com.liquor.liquor.repository.OrderRepository;
import com.liquor.liquor.repository.PaymentRepository;
import com.liquor.liquor.repository.UserRepository;
import com.liquor.liquor.service.OrderService;
import com.liquor.liquor.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;

    private final UserService userService;

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final LiquorRepository liquorRepository;

    private final UserRepository userRepository;

    private final LiquorMapper liquorMapper;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody AdminUserSaveRequest request) {
        validateUserRequest(request, true, null);

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() == null ? Role.USER : request.getRole())
                .active(request.getActive() == null || request.getActive())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toUserResponse(userRepository.save(user)));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody AdminUserSaveRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateUserRequest(request, false, userId);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        return ResponseEntity.ok(toUserResponse(userRepository.save(user)));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        List<Payment> successfulPayments = paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCESS)
                .toList();

        BigDecimal totalRevenue = successfulPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RevenuePointResponse> monthlyRevenue = successfulPayments.stream()
                .filter(payment -> payment.getCreatedAt() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        payment -> payment.getCreatedAt().getMonth(),
                        java.util.stream.Collectors.mapping(Payment::getAmount,
                                java.util.stream.Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getValue()))
                .map(entry -> RevenuePointResponse.builder()
                        .label(entry.getKey().name().substring(0, 3))
                        .revenue(entry.getValue())
                        .build())
                .toList();

        List<RevenuePointResponse> yearlyRevenue = successfulPayments.stream()
                .filter(payment -> payment.getCreatedAt() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        payment -> String.valueOf(payment.getCreatedAt().getYear()),
                        java.util.stream.Collectors.mapping(Payment::getAmount,
                                java.util.stream.Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet()
                .stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(entry -> RevenuePointResponse.builder()
                        .label(entry.getKey())
                        .revenue(entry.getValue())
                        .build())
                .toList();

        int stockUnits = liquorRepository.findAll()
                .stream()
                .map(Liquor::getStock)
                .filter(stock -> stock != null)
                .mapToInt(Integer::intValue)
                .sum();

        long activeUsers = userRepository.findAll()
                .stream()
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .count();

        return ResponseEntity.ok(AdminDashboardResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(orderRepository.count())
                .paidPayments((long) successfulPayments.size())
                .activeUsers(activeUsers)
                .stockUnits(stockUnits)
                .monthlyRevenue(monthlyRevenue)
                .yearlyRevenue(yearlyRevenue)
                .build());
    }

    @PatchMapping("/orders/{orderId}/payment-status")
    public ResponseEntity<OrderResponse> updatePaymentStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody PaymentStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(request.getStatus());
        if (request.getStatus() == PaymentStatus.SUCCESS) {
            order.setStatus(OrderStatus.PAID);
        }
        else if (request.getStatus() == PaymentStatus.FAILED) {
            order.setStatus(OrderStatus.PENDING);
        }
        orderRepository.save(order);
        paymentRepository.save(payment);

        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PatchMapping("/liquors/{liquorId}/stock")
    public ResponseEntity<LiquorResponse> updateStock(
            @PathVariable UUID liquorId,
            @Valid @RequestBody StockUpdateRequest request) {
        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new RuntimeException("Liquor not found"));
        liquor.setStock(request.getStock());
        return ResponseEntity.ok(liquorMapper.toResponse(liquorRepository.save(liquor)));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUserAccess(
            @PathVariable UUID userId,
            @RequestBody AdminUserUpdateRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        UserEntity savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(toUserResponse(savedUser));
    }

    private void validateUserRequest(AdminUserSaveRequest request, boolean requirePassword, UUID existingUserId) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Name is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (request.getPhoneNumber() == null || !request.getPhoneNumber().matches("^[0-9]{10}$")) {
            throw new RuntimeException("Phone number must contain exactly 10 digits");
        }
        if (requirePassword && (request.getPassword() == null || request.getPassword().isBlank())) {
            throw new RuntimeException("Password is required");
        }

        userRepository.findByEmail(request.getEmail())
                .filter(user -> !user.getId().equals(existingUserId))
                .ifPresent(user -> {
                    throw new RuntimeException("Email already exists");
                });

        userRepository.findAll()
                .stream()
                .filter(user -> request.getPhoneNumber().equals(user.getPhoneNumber()))
                .filter(user -> !user.getId().equals(existingUserId))
                .findFirst()
                .ifPresent(user -> {
                    throw new RuntimeException("Phone number already exists");
                });
    }

    private UserResponse toUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }
}
