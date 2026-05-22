package com.hilti.booking.config;

import com.hilti.booking.entity.*;
import com.hilti.booking.repository.AnchorCapacityConfigRepository;
import com.hilti.booking.repository.SlotRepository;
import com.hilti.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   SlotRepository slotRepository,
                                   AnchorCapacityConfigRepository capacityConfigRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@hilti.com")) {
                User admin = new User();
                admin.setFullName("Hilti Admin");
                admin.setEmail("admin@hilti.com");
                admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
                admin.setRole(Role.ROLE_ADMIN);
                admin.setActive(true);
                userRepository.save(admin);
            }
            if (!userRepository.existsByEmail("customer@hilti.com")) {
                User customer = new User();
                customer.setFullName("Hilti Customer");
                customer.setEmail("customer@hilti.com");
                customer.setPasswordHash(passwordEncoder.encode("Customer123!"));
                customer.setRole(Role.ROLE_CUSTOMER);
                customer.setCustomerType("ROUTINE");
                customer.setActive(true);
                userRepository.save(customer);
            }
            if (!userRepository.existsByEmail("fe@hilti.com")) {
                User fe = new User();
                fe.setFullName("Field Executive");
                fe.setEmail("fe@hilti.com");
                fe.setPasswordHash(passwordEncoder.encode("Fe123!"));
                fe.setRole(Role.ROLE_FE);
                fe.setActive(true);
                userRepository.save(fe);
            }
            if (!userRepository.existsByEmail("manager@hilti.com")) {
                User manager = new User();
                manager.setFullName("Hilti Manager");
                manager.setEmail("manager@hilti.com");
                manager.setPasswordHash(passwordEncoder.encode("Manager123!"));
                manager.setRole(Role.ROLE_MANAGER);
                manager.setActive(true);
                userRepository.save(manager);
            }

            if (capacityConfigRepository.count() == 0) {
                capacityConfigRepository.saveAll(List.of(
                        createConfig("10mm", 8),
                        createConfig("12mm", 6),
                        createConfig("16mm", 4)
                ));
            }

            if (slotRepository.count() == 0) {
                LocalDate today = LocalDate.now();
                slotRepository.saveAll(List.of(
                        createSlot(today, LocalTime.of(9, 0), SlotType.ROUTINE, 10, 0),
                        createSlot(today, LocalTime.of(11, 0), SlotType.ACCOUNT_PRIORITY, 10, 0),
                        createSlot(today.plusDays(1), LocalTime.of(14, 0), SlotType.NORMAL, 10, 0)
                ));
            }
        };
    }

    private AnchorCapacityConfig createConfig(String size, int maxPieces) {
        AnchorCapacityConfig config = new AnchorCapacityConfig();
        config.setAnchorSize(size);
        config.setMaxPiecesPer2h(maxPieces);
        return config;
    }

    private Slot createSlot(LocalDate date, LocalTime time, SlotType type, int cap, int booked) {
        Slot slot = new Slot();
        slot.setSlotDate(date);
        slot.setSlotTime(time);
        slot.setSlotType(type);
        slot.setCapacity(cap);
        slot.setBookedCount(booked);
        return slot;
    }
}
