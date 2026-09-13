package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Room entity representing the farmer profile.
 * Stores verified identification, primary location, and FPO association.
 * Sensitive documents are never stored in plaintext fields.
 */
@Entity(tableName = "farmer_profile")
data class FarmerProfileEntity(
    @PrimaryKey val id: String = "primary_farmer",
    val fullName: String = "Ramesh Patil",
    val role: String = "Organic Soybean & Wheat Cultivator",
    val village: String = "Katol",
    val district: String = "Nagpur",
    val state: String = "Maharashtra",
    val fpoName: String = "Nagpur Krishi Vikas FPO",
    val fpoMemberId: String = "FPO-NGP-2024-8841",
    val helpline: String = "1800-180-1551",
    val preferredLanguageCode: String = "en",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
