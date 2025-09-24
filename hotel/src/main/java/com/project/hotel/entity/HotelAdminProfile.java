package com.project.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hotel_admin_profile")
public class HotelAdminProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "business_name", nullable = false)
    String businessName;

    @Column(name = "business_address")
    String businessAddress;

    @Column(name = "tax_code")
    String taxCode;

    @Column(name = "license_number")
    String licenseNumber;

    @Column(name = "bank_account")
    String bankAccount;

    @Column(name = "id_card_or_passport")
    String idCardOrPassport;

    @Column(name = "verified")
    Boolean verified;
}