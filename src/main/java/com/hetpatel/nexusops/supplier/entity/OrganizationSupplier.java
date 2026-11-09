package com.hetpatel.nexusops.supplier.entity;

import com.hetpatel.nexusops.common.persistence.BaseEntity;

import com.hetpatel.nexusops.organization.entity.Organization;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "organization_suppliers")
@SQLDelete(sql = "UPDATE organization_suppliers SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OrganizationSupplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrganizationSupplierStatus status = OrganizationSupplierStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private SupplierRiskLevel riskLevel = SupplierRiskLevel.LOW;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
