package com.hetpatel.nexusops.supplier.entity;

import com.hetpatel.nexusops.common.persistence.BaseEntity;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "suppliers")
@SQLDelete(sql = "UPDATE suppliers SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "legal_name", nullable = false, length = 200)
    @ToString.Include
    private String legalName;

    @Column(name = "display_name", length = 200)
    @ToString.Include
    private String displayName;

    @Column(name = "supplier_code", nullable = false, length = 50)
    @ToString.Include
    private String supplierCode;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false, length = 30)
    private SupplierRegistrationStatus registrationStatus;

    private String website;

    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
