package com.hetpatel.nexusops.identity.permission.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PredefinedPermission {

    // Organization

    ORGANIZATION_READ(
            "Organization Read",
            "View organization information."
    ),

    ORGANIZATION_CREATE(
            "Organization Create",
            "Create organizations."
    ),

    ORGANIZATION_UPDATE(
            "Organization Update",
            "Update organization information."
    ),

    ORGANIZATION_DELETE(
            "Organization Delete",
            "Delete organizations."
    ),

    ORGANIZATION_RESTORE(
            "Organization Restore",
            "Restore deleted organizations."
    ),

    ORGANIZATION_SETTINGS_READ(
            "Organization Settings Read",
            "View organization settings."
    ),

    ORGANIZATION_SETTINGS_UPDATE(
            "Organization Settings Update",
            "Update organization settings."
    ),


    // User

    USER_READ(
            "User Read",
            "View user information."
    ),

    USER_CREATE(
            "User Create",
            "Create users."
    ),

    USER_UPDATE(
            "User Update",
            "Update user information."
    ),

    USER_DELETE(
            "User Delete",
            "Delete users."
    ),

    USER_RESTORE(
            "User Restore",
            "Restore deleted users."
    ),

    USER_ACTIVATE(
            "User Activate",
            "Activate users."
    ),

    USER_DEACTIVATE(
            "User Deactivate",
            "Deactivate users."
    ),

    USER_ACCESS_REVIEW(
            "User Access Review",
            "Review user access and assigned permissions."
    ),


    // Role

    ROLE_READ(
            "Role Read",
            "View roles."
    ),

    ROLE_CREATE(
            "Role Create",
            "Create organization roles."
    ),

    ROLE_UPDATE(
            "Role Update",
            "Update organization roles."
    ),

    ROLE_DELETE(
            "Role Delete",
            "Delete organization roles."
    ),

    ROLE_RESTORE(
            "Role Restore",
            "Restore deleted organization roles."
    ),

    ROLE_ASSIGN(
            "Role Assign",
            "Assign roles to users."
    ),

    ROLE_UNASSIGN(
            "Role Unassign",
            "Remove roles from users."
    ),


    // Permission

    PERMISSION_READ(
            "Permission Read",
            "View the application's available permission catalog."
    ),


    // Supplier

    SUPPLIER_READ(
            "Supplier Read",
            "View supplier information."
    ),

    SUPPLIER_CREATE(
            "Supplier Create",
            "Create suppliers."
    ),

    SUPPLIER_UPDATE(
            "Supplier Update",
            "Update supplier information."
    ),

    SUPPLIER_DELETE(
            "Supplier Delete",
            "Delete suppliers."
    ),

    SUPPLIER_RESTORE(
            "Supplier Restore",
            "Restore deleted suppliers."
    ),

    SUPPLIER_CONTACT_READ(
            "Supplier Contact Read",
            "View supplier contact information."
    ),

    SUPPLIER_CONTACT_MANAGE(
            "Supplier Contact Manage",
            "Create, update, and delete supplier contacts."
    ),

    SUPPLIER_CATEGORY_READ(
            "Supplier Category Read",
            "View supplier categories."
    ),

    SUPPLIER_CATEGORY_MANAGE(
            "Supplier Category Manage",
            "Create, update, and delete supplier categories."
    ),

    SUPPLIER_TAX_READ(
            "Supplier Tax Read",
            "View supplier tax information."
    ),

    SUPPLIER_TAX_UPDATE(
            "Supplier Tax Update",
            "Update supplier tax information."
    ),

    SUPPLIER_BILLING_READ(
            "Supplier Billing Read",
            "View supplier billing information."
    ),

    SUPPLIER_BILLING_UPDATE(
            "Supplier Billing Update",
            "Update supplier billing information."
    ),

    SUPPLIER_PERFORMANCE_READ(
            "Supplier Performance Read",
            "View supplier performance information."
    ),

    SUPPLIER_RISK_READ(
            "Supplier Risk Read",
            "View supplier risk information."
    ),

    SUPPLIER_RISK_UPDATE(
            "Supplier Risk Update",
            "Update supplier risk information."
    ),

    SUPPLIER_DOCUMENT_READ(
            "Supplier Document Read",
            "View supplier documents."
    ),

    SUPPLIER_DOCUMENT_MANAGE(
            "Supplier Document Manage",
            "Upload, update, and delete supplier documents."
    ),


    // Product

    PRODUCT_READ(
            "Product Read",
            "View product information."
    ),

    PRODUCT_CREATE(
            "Product Create",
            "Create products."
    ),

    PRODUCT_UPDATE(
            "Product Update",
            "Update product information."
    ),

    PRODUCT_DELETE(
            "Product Delete",
            "Delete products."
    ),

    PRODUCT_RESTORE(
            "Product Restore",
            "Restore deleted products."
    ),

    PRODUCT_ACTIVATE(
            "Product Activate",
            "Activate products."
    ),

    PRODUCT_DEACTIVATE(
            "Product Deactivate",
            "Deactivate products."
    ),

    PRODUCT_CATEGORY_READ(
            "Product Category Read",
            "View product categories."
    ),

    PRODUCT_CATEGORY_MANAGE(
            "Product Category Manage",
            "Create, update, and delete product categories."
    ),

    PRODUCT_UNIT_READ(
            "Product Unit Read",
            "View product units."
    ),

    PRODUCT_UNIT_MANAGE(
            "Product Unit Manage",
            "Create, update, and delete product units."
    ),

    PRODUCT_SUPPLIER_READ(
            "Product Supplier Read",
            "View product-supplier relationships."
    ),

    PRODUCT_SUPPLIER_MANAGE(
            "Product Supplier Manage",
            "Create, update, and remove product-supplier relationships."
    ),

    PRODUCT_PRICE_READ(
            "Product Price Read",
            "View product pricing information."
    ),

    PRODUCT_PRICE_UPDATE(
            "Product Price Update",
            "Update product pricing information."
    ),

    PRODUCT_TAX_READ(
            "Product Tax Read",
            "View product tax information."
    ),

    PRODUCT_TAX_UPDATE(
            "Product Tax Update",
            "Update product tax information."
    ),

    PRODUCT_REORDER_THRESHOLD_READ(
            "Product Reorder Threshold Read",
            "View product reorder thresholds."
    ),

    PRODUCT_REORDER_THRESHOLD_UPDATE(
            "Product Reorder Threshold Update",
            "Update product reorder thresholds."
    ),


    // Purchase Request

    PURCHASE_REQUEST_READ(
            "Purchase Request Read",
            "View purchase requests."
    ),

    PURCHASE_REQUEST_CREATE(
            "Purchase Request Create",
            "Create purchase requests."
    ),

    PURCHASE_REQUEST_UPDATE(
            "Purchase Request Update",
            "Update purchase requests."
    ),

    PURCHASE_REQUEST_DELETE(
            "Purchase Request Delete",
            "Delete purchase requests."
    ),

    PURCHASE_REQUEST_SUBMIT(
            "Purchase Request Submit",
            "Submit purchase requests for processing."
    ),

    PURCHASE_REQUEST_CANCEL(
            "Purchase Request Cancel",
            "Cancel purchase requests."
    ),

    PURCHASE_REQUEST_STATUS_UPDATE(
            "Purchase Request Status Update",
            "Update purchase request status."
    ),


    // Purchase Order


    PURCHASE_ORDER_READ(
            "Purchase Order Read",
            "View purchase orders."
    ),

    PURCHASE_ORDER_CREATE(
            "Purchase Order Create",
            "Create purchase orders."
    ),

    PURCHASE_ORDER_UPDATE(
            "Purchase Order Update",
            "Update purchase orders."
    ),

    PURCHASE_ORDER_DELETE(
            "Purchase Order Delete",
            "Delete purchase orders."
    ),

    PURCHASE_ORDER_SUBMIT(
            "Purchase Order Submit",
            "Submit purchase orders for processing."
    ),

    PURCHASE_ORDER_CANCEL(
            "Purchase Order Cancel",
            "Cancel purchase orders."
    ),

    PURCHASE_ORDER_STATUS_UPDATE(
            "Purchase Order Status Update",
            "Update purchase order status."
    ),

    PURCHASE_ORDER_SUPPLIER_CHANGE(
            "Purchase Order Supplier Change",
            "Change the supplier associated with a purchase order."
    ),


    // Approval

    APPROVAL_READ(
            "Approval Read",
            "View approval requests."
    ),

    APPROVAL_APPROVE(
            "Approval Approve",
            "Approve approval requests."
    ),

    APPROVAL_REJECT(
            "Approval Reject",
            "Reject approval requests."
    ),

    APPROVAL_ESCALATE(
            "Approval Escalate",
            "Escalate approval requests."
    ),

    APPROVAL_CANCEL(
            "Approval Cancel",
            "Cancel approval requests."
    ),

    APPROVAL_RULE_READ(
            "Approval Rule Read",
            "View approval rules."
    ),

    APPROVAL_RULE_CREATE(
            "Approval Rule Create",
            "Create approval rules."
    ),

    APPROVAL_RULE_UPDATE(
            "Approval Rule Update",
            "Update approval rules."
    ),

    APPROVAL_RULE_DELETE(
            "Approval Rule Delete",
            "Delete approval rules."
    ),

    APPROVAL_HISTORY_READ(
            "Approval History Read",
            "View approval history."
    ),


    // Inventory

    INVENTORY_READ(
            "Inventory Read",
            "View inventory information."
    ),

    INVENTORY_UPDATE(
            "Inventory Update",
            "Update inventory information."
    ),

    INVENTORY_RESERVATION_READ(
            "Inventory Reservation Read",
            "View inventory reservations."
    ),

    INVENTORY_RESERVE(
            "Inventory Reserve",
            "Reserve inventory."
    ),

    INVENTORY_RELEASE(
            "Inventory Release",
            "Release inventory reservations."
    ),

    INVENTORY_RECEIPT_READ(
            "Inventory Receipt Read",
            "View inventory receipts."
    ),

    INVENTORY_RECEIPT_CREATE(
            "Inventory Receipt Create",
            "Create inventory receipts."
    ),

    INVENTORY_ADJUSTMENT_READ(
            "Inventory Adjustment Read",
            "View inventory adjustments."
    ),

    INVENTORY_ADJUSTMENT_CREATE(
            "Inventory Adjustment Create",
            "Create inventory adjustments."
    ),

    INVENTORY_TRANSFER_READ(
            "Inventory Transfer Read",
            "View inventory transfers."
    ),

    INVENTORY_TRANSFER_CREATE(
            "Inventory Transfer Create",
            "Create inventory transfers."
    ),

    INVENTORY_MOVEMENT_READ(
            "Inventory Movement Read",
            "View inventory movements."
    ),

    INVENTORY_RECONCILIATION_READ(
            "Inventory Reconciliation Read",
            "View inventory reconciliation information."
    ),

    INVENTORY_RECONCILIATION_EXECUTE(
            "Inventory Reconciliation Execute",
            "Execute inventory reconciliation."
    ),

    INVENTORY_LOW_STOCK_READ(
            "Inventory Low Stock Read",
            "View low-stock inventory information."
    ),


    // Warehouse

    WAREHOUSE_READ(
            "Warehouse Read",
            "View warehouse information."
    ),

    WAREHOUSE_CREATE(
            "Warehouse Create",
            "Create warehouses."
    ),

    WAREHOUSE_UPDATE(
            "Warehouse Update",
            "Update warehouse information."
    ),

    WAREHOUSE_DELETE(
            "Warehouse Delete",
            "Delete warehouses."
    ),

    WAREHOUSE_LOCATION_READ(
            "Warehouse Location Read",
            "View warehouse locations."
    ),

    WAREHOUSE_LOCATION_MANAGE(
            "Warehouse Location Manage",
            "Create, update, and delete warehouse locations."
    ),

    WAREHOUSE_RECEIVING_READ(
            "Warehouse Receiving Read",
            "View warehouse receiving operations."
    ),

    WAREHOUSE_RECEIVING_MANAGE(
            "Warehouse Receiving Manage",
            "Create and update warehouse receiving operations."
    ),

    WAREHOUSE_DISPATCH_READ(
            "Warehouse Dispatch Read",
            "View warehouse dispatch operations."
    ),

    WAREHOUSE_DISPATCH_MANAGE(
            "Warehouse Dispatch Manage",
            "Create and update warehouse dispatch operations."
    ),

    WAREHOUSE_TRANSFER_READ(
            "Warehouse Transfer Read",
            "View warehouse transfers."
    ),

    WAREHOUSE_TRANSFER_CREATE(
            "Warehouse Transfer Create",
            "Create warehouse transfers."
    ),

    WAREHOUSE_TRANSFER_APPROVE(
            "Warehouse Transfer Approve",
            "Approve warehouse transfers."
    ),

    WAREHOUSE_RECONCILIATION_READ(
            "Warehouse Reconciliation Read",
            "View warehouse reconciliation information."
    ),

    WAREHOUSE_RECONCILIATION_EXECUTE(
            "Warehouse Reconciliation Execute",
            "Execute warehouse reconciliation."
    ),


    // Invoice / Finance

    INVOICE_READ(
            "Invoice Read",
            "View invoices."
    ),

    INVOICE_CREATE(
            "Invoice Create",
            "Create invoices."
    ),

    INVOICE_UPDATE(
            "Invoice Update",
            "Update invoices."
    ),

    INVOICE_DELETE(
            "Invoice Delete",
            "Delete invoices."
    ),

    INVOICE_MATCH_READ(
            "Invoice Match Read",
            "View invoice matching information."
    ),

    INVOICE_MATCH_EXECUTE(
            "Invoice Match Execute",
            "Execute invoice matching."
    ),

    INVOICE_DISCREPANCY_READ(
            "Invoice Discrepancy Read",
            "View invoice discrepancies."
    ),

    INVOICE_DISCREPANCY_RESOLVE(
            "Invoice Discrepancy Resolve",
            "Resolve invoice discrepancies."
    ),

    INVOICE_DUE_DATE_READ(
            "Invoice Due Date Read",
            "View invoice due dates."
    ),

    INVOICE_DUE_DATE_UPDATE(
            "Invoice Due Date Update",
            "Update invoice due dates."
    ),

    INVOICE_PAYMENT_STATUS_READ(
            "Invoice Payment Status Read",
            "View invoice payment status."
    ),

    INVOICE_PAYMENT_STATUS_UPDATE(
            "Invoice Payment Status Update",
            "Update invoice payment status."
    ),

    INVOICE_AUDIT_READ(
            "Invoice Audit Read",
            "View invoice audit information."
    ),


    // Notification

    NOTIFICATION_READ(
            "Notification Read",
            "View notifications."
    ),

    NOTIFICATION_CREATE(
            "Notification Create",
            "Create notifications."
    ),

    NOTIFICATION_UPDATE(
            "Notification Update",
            "Update notifications."
    ),

    NOTIFICATION_DELETE(
            "Notification Delete",
            "Delete notifications."
    ),

    NOTIFICATION_MARK_READ(
            "Notification Mark Read",
            "Mark notifications as read."
    ),


    // Reporting

    REPORT_READ(
            "Report Read",
            "View reports."
    ),

    REPORT_EXPORT(
            "Report Export",
            "Export reports."
    ),

    SPEND_REPORT_READ(
            "Spend Report Read",
            "View spend reports."
    ),

    INVENTORY_REPORT_READ(
            "Inventory Report Read",
            "View inventory reports."
    ),

    PURCHASE_CYCLE_REPORT_READ(
            "Purchase Cycle Report Read",
            "View purchase cycle reports."
    ),

    SUPPLIER_PERFORMANCE_REPORT_READ(
            "Supplier Performance Report Read",
            "View supplier performance reports."
    ),

    INVOICE_AGEING_REPORT_READ(
            "Invoice Ageing Report Read",
            "View invoice ageing reports."
    ),

    OPERATIONAL_KPI_READ(
            "Operational KPI Read",
            "View operational KPI information."
    ),


    // Audit

    AUDIT_READ(
            "Audit Read",
            "View audit records."
    ),

    AUDIT_SEARCH(
            "Audit Search",
            "Search audit records."
    ),

    AUDIT_EXPORT(
            "Audit Export",
            "Export audit records."
    ),


    // AI

    AI_COPILOT_USE(
            "AI Copilot Use",
            "Use the NexusOps AI copilot."
    ),

    AI_SUPPLIER_PERFORMANCE_USE(
            "AI Supplier Performance Use",
            "Use AI-assisted supplier performance analysis."
    ),

    AI_SPEND_ANALYSIS_USE(
            "AI Spend Analysis Use",
            "Use AI-assisted spend analysis."
    ),

    AI_INVENTORY_HEALTH_USE(
            "AI Inventory Health Use",
            "Use AI-assisted inventory health analysis."
    ),

    AI_INVOICE_AGEING_USE(
            "AI Invoice Ageing Use",
            "Use AI-assisted invoice ageing analysis."
    ),


    // Security / Access

    ACCESS_REVIEW_READ(
            "Access Review Read",
            "View access review information."
    ),

    ACCESS_REVIEW_EXECUTE(
            "Access Review Execute",
            "Execute access reviews."
    ),

    SESSION_REVOKE(
            "Session Revoke",
            "Revoke active user sessions."
    ),

    ACCOUNT_LOCK(
            "Account Lock",
            "Lock user accounts."
    ),

    ACCOUNT_UNLOCK(
            "Account Unlock",
            "Unlock user accounts."
    );

    private final String displayName;
    private final String description;
}
