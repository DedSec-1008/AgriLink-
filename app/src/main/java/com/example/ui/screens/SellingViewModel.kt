package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AgriRepository
import com.example.model.CropOption
import com.example.model.ProduceLot
import com.example.model.SellingOpportunity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SellingStep(val stepNumber: Int) {
    CROP(1),
    QUANTITY(2),
    QUALITY(3),
    LOCATION(4),
    READY_DATE(5),
    REVIEW(6),
    ANALYSIS(7),
    RECOMMENDATIONS(8),
    CONFIRM_SELL(9),
    LOT_CREATED(10)
}

data class SellingUiState(
    val currentStep: SellingStep = SellingStep.CROP,
    val selectedCrop: CropOption = CropOption("soybean", R.string.crop_soybean, "🌱", 4850),
    val quantityQuintals: Int = 50,
    val qualityKey: String = "good",
    val qualityRes: Int = R.string.produce_quality_good,
    val location: String = "Nagpur, Maharashtra",
    val locationRes: Int = R.string.loc_nagpur,
    val readyTiming: String = "Ready now",
    val readyTimingRes: Int = R.string.timing_ready_now,
    val analysisProgressIndex: Int = 0,
    val recommendations: List<SellingOpportunity> = emptyList(),
    val selectedOpportunity: SellingOpportunity? = null,
    val isLoadingRecommendations: Boolean = false,
    val recommendationError: Boolean = false,
    val createdLot: ProduceLot? = null
)

class SellingViewModel(
    private val repository: AgriRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellingUiState())
    val uiState: StateFlow<SellingUiState> = _uiState.asStateFlow()

    fun selectCrop(crop: CropOption) {
        _uiState.update { it.copy(selectedCrop = crop, currentStep = SellingStep.QUANTITY) }
    }

    fun updateQuantity(quantity: Int) {
        if (quantity in 1..2000) {
            _uiState.update { it.copy(quantityQuintals = quantity) }
        }
    }

    fun selectQuality(qualityKey: String, qualityRes: Int) {
        val displayRes = when (qualityKey.lowercase()) {
            "good" -> R.string.produce_quality_good
            "average" -> R.string.quality_average
            "poor" -> R.string.quality_poor
            else -> qualityRes
        }
        _uiState.update { it.copy(qualityKey = qualityKey, qualityRes = displayRes) }
    }

    fun selectLocation(locationName: String, locationRes: Int) {
        _uiState.update { it.copy(location = locationName, locationRes = locationRes) }
    }

    fun selectReadyTiming(timing: String, timingRes: Int) {
        _uiState.update { it.copy(readyTiming = timing, readyTimingRes = timingRes) }
    }

    fun goToStep(step: SellingStep) {
        _uiState.update { it.copy(currentStep = step) }
    }

    fun goBack(): Boolean {
        val currentState = _uiState.value
        val prevStep = when (currentState.currentStep) {
            SellingStep.CROP -> null
            SellingStep.QUANTITY -> SellingStep.CROP
            SellingStep.QUALITY -> SellingStep.QUANTITY
            SellingStep.LOCATION -> SellingStep.QUALITY
            SellingStep.READY_DATE -> SellingStep.LOCATION
            SellingStep.REVIEW -> SellingStep.READY_DATE
            SellingStep.ANALYSIS -> SellingStep.REVIEW
            SellingStep.RECOMMENDATIONS -> SellingStep.REVIEW
            SellingStep.CONFIRM_SELL -> SellingStep.RECOMMENDATIONS
            SellingStep.LOT_CREATED -> SellingStep.RECOMMENDATIONS
        }
        return if (prevStep != null) {
            _uiState.update { it.copy(currentStep = prevStep) }
            true
        } else {
            false
        }
    }

    fun startAnalysisAndFindOptions() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentStep = SellingStep.ANALYSIS,
                    isLoadingRecommendations = true,
                    recommendationError = false,
                    analysisProgressIndex = 0
                )
            }

            // Step through reassuring progress stages
            for (stage in 1..4) {
                delay(260)
                _uiState.update { it.copy(analysisProgressIndex = stage) }
            }

            val state = _uiState.value
            val result = repository.recommendationService.getSellingRecommendations(
                cropId = state.selectedCrop.id,
                quantityQuintals = state.quantityQuintals,
                qualityKey = state.qualityKey,
                location = state.location,
                readyTiming = state.readyTiming
            )

            result.onSuccess { recs ->
                _uiState.update {
                    it.copy(
                        recommendations = recs,
                        selectedOpportunity = recs.firstOrNull(),
                        isLoadingRecommendations = false,
                        currentStep = SellingStep.RECOMMENDATIONS
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoadingRecommendations = false,
                        recommendationError = true
                    )
                }
            }
        }
    }

    fun selectOpportunityToSell(opportunity: SellingOpportunity) {
        _uiState.update {
            it.copy(
                selectedOpportunity = opportunity,
                currentStep = SellingStep.CONFIRM_SELL
            )
        }
    }

    fun confirmCreateLot() {
        // Section 18: Duplicate Lot Protection
        if (_uiState.value.createdLot != null && _uiState.value.currentStep == SellingStep.LOT_CREATED) {
            return
        }
        viewModelScope.launch {
            val state = _uiState.value
            val buyerRes = state.selectedOpportunity?.buyerNameRes ?: R.string.buyer_abc_foods
            val lot = repository.addLot(
                cropRes = state.selectedCrop.nameRes,
                emoji = state.selectedCrop.emoji,
                quantity = state.quantityQuintals,
                qualityRes = state.qualityRes,
                buyerNameRes = buyerRes
            )
            _uiState.update {
                it.copy(
                    createdLot = lot,
                    currentStep = SellingStep.LOT_CREATED
                )
            }
        }
    }

    fun startSellingToBuyer(buyerId: String) {
        val buyer = repository.getBuyerById(buyerId) ?: return
        _uiState.update {
            it.copy(
                currentStep = SellingStep.REVIEW,
                quantityQuintals = buyer.currentDemandMinQ.coerceAtLeast(50),
                location = "Nagpur, Maharashtra",
                locationRes = R.string.loc_nagpur,
                readyTiming = "Ready now",
                readyTimingRes = R.string.timing_ready_now
            )
        }
    }

    fun resetFlow() {
        _uiState.update {
            SellingUiState(
                selectedCrop = repository.getAvailableCrops().firstOrNull() ?: it.selectedCrop
            )
        }
    }
}
