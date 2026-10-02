package com.hetpatel.nexusops.identity.role.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PredefinedRole {

    ORG_ADMIN(
            "Organization Administrator",
            "Manages users, roles, and organization-level configuration."
    ),

    EMPLOYEE(
            "Employee",
            "General organization user with limited operational access."
    ),

    SUPPLIER_MANAGER(
            "Supplier Manager",
            "Manages suppliers and supplier-related information."
    ),

    PRODUCT_MANAGER(
            "Product Manager",
            "Manages products, categories, units, SKUs, and supplier-product information."
    ),

    PROCUREMENT_MANAGER(
            "Procurement Manager",
            "Manages procurement operations and procurement activities."
    ),

    PROCUREMENT_OFFICER(
            "Procurement Officer",
            "Handles day-to-day procurement activities."
    ),

    PURCHASE_APPROVER(
            "Purchase Approver",
            "Approves or rejects purchase requests and orders according to configured authority."
    ),

    APPROVAL_MANAGER(
            "Approval Manager",
            "Manages approval rules, thresholds, and approval workflows."
    ),

    INVENTORY_MANAGER(
            "Inventory Manager",
            "Manages inventory, stock, reservations, adjustments, and reconciliation."
    ),

    WAREHOUSE_MANAGER(
            "Warehouse Manager",
            "Manages warehouses, locations, receiving, dispatch, and stock transfers."
    ),

    TRANSPORT_MANAGER(
            "Transport Manager",
            "Manages transportation and operational movement activities."
    ),

    FINANCE_MANAGER(
            "Finance Manager",
            "Manages invoices, purchase-order matching, discrepancies, and payment status."
    ),

    REPORTING_MANAGER(
            "Reporting Manager",
            "Manages operational and business reporting."
    ),

    AUDITOR(
            "Auditor",
            "Provides read and audit-oriented access to organization information."
    );

    private final String displayName;
    private final String description;
}
