package com.hilti.booking.controller;

import com.hilti.booking.entity.FELeave;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.FELeaveRepository;
import com.hilti.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fe")
public class FEController {
    private final FELeaveRepository feLeaveRepository;
    private final UserRepository userRepository;

    public FEController(FELeaveRepository feLeaveRepository, UserRepository userRepository) {
        this.feLeaveRepository = feLeaveRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<?>> getAssignments(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        if (user.getRole() != Role.ROLE_FE) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/leave")
    public ResponseEntity<FELeave> submitLeave(Authentication authentication, @RequestParam String date) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        if (user.getRole() != Role.ROLE_FE) {
            return ResponseEntity.status(403).build();
        }
        FELeave leave = new FELeave();
        leave.setFieldExecutive(user);
        leave.setLeaveDate(LocalDate.parse(date));
        leave.setApproved(false);
        return ResponseEntity.ok(feLeaveRepository.save(leave));
    }
}
