package com.example.data.local.converters

import androidx.room.TypeConverter
import com.example.data.local.entity.MarketDataFreshness
import com.example.data.local.entity.SyncStatus
import com.example.model.DestinationType
import com.example.model.LotStatus
import com.example.model.OfferStatus
import com.example.model.PaymentStatus
import com.example.model.TransactionStatus

/**
 * Robust TypeConverters for Room local persistence.
 * Avoids third-party JSON dependencies to maintain fast compile speeds and zero runtime reflection.
 */
class AgriTypeConverters {

    @TypeConverter
    fun fromIntList(list: List<Int>?): String {
        return list?.joinToString(separator = ",") ?: ""
    }

    @TypeConverter
    fun toIntList(data: String?): List<Int> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(separator = ";|;") ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(";|;").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromLotStatus(status: LotStatus?): String = status?.name ?: LotStatus.PUBLISHED.name

    @TypeConverter
    fun toLotStatus(name: String?): LotStatus {
        return try {
            if (name != null) LotStatus.valueOf(name) else LotStatus.PUBLISHED
        } catch (_: Exception) {
            LotStatus.PUBLISHED
        }
    }

    @TypeConverter
    fun fromOfferStatus(status: OfferStatus?): String = status?.name ?: OfferStatus.PENDING.name

    @TypeConverter
    fun toOfferStatus(name: String?): OfferStatus {
        return try {
            if (name != null) OfferStatus.valueOf(name) else OfferStatus.PENDING
        } catch (_: Exception) {
            OfferStatus.PENDING
        }
    }

    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus?): String =
        status?.name ?: TransactionStatus.OFFER_ACCEPTED.name

    @TypeConverter
    fun toTransactionStatus(name: String?): TransactionStatus {
        return try {
            if (name != null) TransactionStatus.valueOf(name) else TransactionStatus.OFFER_ACCEPTED
        } catch (_: Exception) {
            TransactionStatus.OFFER_ACCEPTED
        }
    }

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus?): String =
        status?.name ?: PaymentStatus.PENDING.name

    @TypeConverter
    fun toPaymentStatus(name: String?): PaymentStatus {
        return try {
            if (name != null) PaymentStatus.valueOf(name) else PaymentStatus.PENDING
        } catch (_: Exception) {
            PaymentStatus.PENDING
        }
    }

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus?): String =
        status?.name ?: SyncStatus.SYNCED.name

    @TypeConverter
    fun toSyncStatus(name: String?): SyncStatus {
        return try {
            if (name != null) SyncStatus.valueOf(name) else SyncStatus.SYNCED
        } catch (_: Exception) {
            SyncStatus.SYNCED
        }
    }

    @TypeConverter
    fun fromMarketFreshness(freshness: MarketDataFreshness?): String =
        freshness?.name ?: MarketDataFreshness.DEMO.name

    @TypeConverter
    fun toMarketFreshness(name: String?): MarketDataFreshness {
        return try {
            if (name != null) MarketDataFreshness.valueOf(name) else MarketDataFreshness.DEMO
        } catch (_: Exception) {
            MarketDataFreshness.DEMO
        }
    }

    @TypeConverter
    fun fromDestinationType(dest: DestinationType?): String =
        dest?.name ?: DestinationType.BUYER.name

    @TypeConverter
    fun toDestinationType(name: String?): DestinationType {
        return try {
            if (name != null) DestinationType.valueOf(name) else DestinationType.BUYER
        } catch (_: Exception) {
            DestinationType.BUYER
        }
    }
}
