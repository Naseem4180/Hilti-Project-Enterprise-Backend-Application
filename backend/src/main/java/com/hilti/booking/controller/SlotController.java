package com.hilti.booking.controller;

import com.hilti.booking.entity.Slot;
import com.hilti.booking.entity.SlotType;
import com.hilti.booking.repository.SlotRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {
    private final SlotRepository slotRepository;

    public SlotController(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @GetMapping
    public ResponseEntity<List<Slot>> getSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam SlotType type
    ) {
        List<Slot> slots = slotRepository.findBySlotDateAndSlotType(date, type);
        return ResponseEntity.ok(slots);
    }
}
