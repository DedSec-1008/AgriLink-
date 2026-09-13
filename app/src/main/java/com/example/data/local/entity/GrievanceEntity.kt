package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.GrievanceIssue

/**
 * Local Room entity representing a trade dispute or grievance reported by the farmer.
 * Indexed by transactionId.
 */
@Entity(
    tableName = "grievances",
    indices = [
        Index(value = ["transactionId"])
    ]
)
data class GrievanceEntity(
    @PrimaryKey val id: String,
    val transactionId: String,
    val issueTypeRes: Int,
    val details: String = "",
    val createdAt: String = "Today",
    val status: String = "SUBMITTED",
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD,
    val createdTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): GrievanceIssue = GrievanceIssue(
        id = id,
        transactionId = transactionId,
        issueTypeRes = issueTypeRes,
        details = details,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(g: GrievanceIssue, syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD): GrievanceEntity =
            GrievanceEntity(
                id = g.id,
                transactionId = g.transactionId,
                issueTypeRes = g.issueTypeRes,
                details = g.details,
                createdAt = g.createdAt,
                syncStatus = syncStatus
            )
    }
}
