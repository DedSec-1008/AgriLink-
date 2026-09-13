package com.example.data.local.entity

/**
 * Offline synchronization status flag for durable Room entities.
 * Supports future cloud sync engines (WorkManager / REST sync).
 */
enum class SyncStatus {
    PENDING_UPLOAD,
    SYNCED,
    FAILED,
    CONFLICT
}

/**
 * Freshness status for cached market prices.
 * Explicitly separates demo/local data from real-time live feeds.
 */
enum class MarketDataFreshness {
    DEMO,
    CACHED,
    LIVE,
    STALE
}
