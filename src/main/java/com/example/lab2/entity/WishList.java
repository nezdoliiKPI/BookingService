package com.example.lab2.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wish_lists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishList {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "list")
    private List<Long> list;
}
