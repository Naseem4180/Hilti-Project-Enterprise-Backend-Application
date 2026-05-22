package com.hilti.booking.repository;

import com.hilti.booking.entity.Slot;
import com.hilti.booking.entity.SlotType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findBySlotDateAndSlotType(LocalDate slotDate, SlotType slotType);
    List<Slot> findBySlotDate(LocalDate slotDate);
}
