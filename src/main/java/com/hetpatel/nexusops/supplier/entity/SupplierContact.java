package com.hetpatel.nexusops.supplier.entity;

import com.hetpatel.nexusops.common.persistence.BaseEntity;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "supplier_contacts")
@SQLDelete(sql = "UPDATE supplier_contacts SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SupplierContact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "supplier_id", nullable = false)
    @ToString.Include
    private Long supplierId;

    @Column(name = "first_name", nullable = false, length = 100)
    @ToString.Include
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    @ToString.Include
    private String lastName;

    @Column(length = 100)
    private String designation;

    @Column(length = 100)
    private String department;

    private String email;

    @Column(name = "phone_country_code", length = 5)
    private String phoneCountryCode;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "alternate_phone_country_code", length = 5)
    private String alternatePhoneCountryCode;

    @Column(name = "alternate_phone_number", length = 20)
    private String alternatePhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, length = 30)
    private SupplierContactType contactType;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupplierContactStatus status = SupplierContactStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
