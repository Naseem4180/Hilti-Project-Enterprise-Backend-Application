package com.hilti.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "anchor_capacity_config")
public class AnchorCapacityConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String anchorSize;
    private Integer maxPiecesPer2h;

    public AnchorCapacityConfig() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnchorSize() {
        return anchorSize;
    }

    public void setAnchorSize(String anchorSize) {
        this.anchorSize = anchorSize;
    }

    public Integer getMaxPiecesPer2h() {
        return maxPiecesPer2h;
    }

    public void setMaxPiecesPer2h(Integer maxPiecesPer2h) {
        this.maxPiecesPer2h = maxPiecesPer2h;
    }
}
