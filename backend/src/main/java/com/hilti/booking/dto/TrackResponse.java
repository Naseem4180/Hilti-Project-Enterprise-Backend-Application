package com.hilti.booking.dto;

public record TrackResponse(Long bookingId,
                            String status,
                            String fieldExecutive,
                            Integer etaMinutes,
                            String currentLocation) {
}
