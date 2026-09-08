package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AgriRepository
import com.example.model.CropOption
import com.example.model.ProduceLot
import com.example.model.RecommendationCalculationState
import com.example.model.SellingOpportunity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LocationSource {
    NONE,
    CURRENT_LOCATION,
    MANUAL_SELECTION
}

enum class HarvestReadiness {
    NONE,
    READY_NOW,
    WITHIN_7_DAYS,
    LATER
}

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

enum class LotCreationStep(val stepNumber: Int, val stepTitleRes: Int) {
    PRODUCE_SUMMARY(1, R.string.title_step_produce_summary),
    CHECK_DETAILS(2, R.string.title_step_check_details),
    ADD_PHOTOS(3, R.string.title_add_photos),
    READY_TO_PUBLISH(4, R.string.title_ready_to_sell)
}

data class SellingUiState(
    val currentStep: SellingStep = SellingStep.CROP,
    val selectedCrop: CropOption = CropOption("soybean", R.string.crop_soybean, "🌱", 4850),
    val quantityQuintals: Int = 50,
    val qualityKey: String = "",
    val qualityRes: Int = R.string.produce_quality_good,
    val location: String = "",
    val locationRes: Int = 0,
    val locationSource: LocationSource = LocationSource.NONE,
    val harvestReadiness: HarvestReadiness = HarvestReadiness.NONE,
    val readyTiming: String = "",
    val readyTimingRes: Int = 0,
    val analysisProgressIndex: Int = 0,
    val recommendations: List<SellingOpportunity> = emptyList(),
    val bestRecommendation: SellingOpportunity? = null,
    val alternativeRecommendations: List<SellingOpportunity> = emptyList(),
    val calculationState: RecommendationCalculationState = RecommendationCalculationState.IDLE,
    val selectedOpportunity: SellingOpportunity? = null,
    val isLoadingRecommendations: Boolean = false,
    val recommendationError: Boolean = false,
    val createdLot: ProduceLot? = null,
    val lotCreationStep: LotCreationStep = LotCreationStep.PRODUCE_SUMMARY,
    val lotPhotos: List<String> = emptyList(),
    val isPublishingLot: Boolean = false,
    val publishError: Boolean = false,
    val publishErrorMessageRes: Int? = null,
    val publishErrorMessage: String? = null
) {
    val lotPublishError: String?
        get() = publishErrorMessage

    val readinessTiming: String
        get() = readyTiming.ifBlank { "Ready now" }
}

class SellingViewModel(
    private val repository: AgriRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellingUiState())
    val uiState: StateFlow<SellingUiState> = _uiState.asStateFlow()

    fun setCrop(crop: CropOption) {
        _uiState.update { it.copy(selectedCrop = crop) }
    }

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

    fun selectLocation(
        locationName: String,
        locationRes: Int,
        source: LocationSource = LocationSource.MANUAL_SELECTION
    ) {
        _uiState.update {
            it.copy(
                location = locationName,
                locationRes = locationRes,
                locationSource = source
            )
        }
    }

    fun selectHarvestReadiness(readiness: HarvestReadiness) {
        val (timing, res) = when (readiness) {
            HarvestReadiness.READY_NOW -> "Ready now" to R.string.timing_ready_now
            HarvestReadiness.WITHIN_7_DAYS -> "Within 7 days" to R.string.timing_within_7_days
            HarvestReadiness.LATER -> "Later" to R.string.timing_later
            HarvestReadiness.NONE -> "" to 0
        }
        _uiState.update {
            it.copy(
                harvestReadiness = readiness,
                readyTiming = timing,
                readyTimingRes = res
            )
        }
    }

    fun selectReadyTiming(timing: String, timingRes: Int) {
        val readiness = when (timing) {
            "Ready now" -> HarvestReadiness.READY_NOW
            "Within 7 days" -> HarvestReadiness.WITHIN_7_DAYS
            "Later" -> HarvestReadiness.LATER
            else -> HarvestReadiness.NONE
        }
        _uiState.update {
            it.copy(
                readyTiming = timing,
                readyTimingRes = timingRes,
                harvestReadiness = readiness
            )
        }
    }

    fun goToStep(step: SellingStep) {
        _uiState.update { it.copy(currentStep = step) }
    }

    fun goBack(): Boolean {
        val currentState = _uiState.value
        if (currentState.currentStep == SellingStep.CONFIRM_SELL && currentState.lotCreationStep != LotCreationStep.PRODUCE_SUMMARY) {
            previousLotCreationStep()
            return true
        }
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
                    calculationState = RecommendationCalculationState.CALCULATING,
                    analysisProgressIndex = 0
                )
            }

            // Step through reassuring progress stages
            for (stage in 1..4) {
                delay(200)
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
                if (recs.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            recommendations = emptyList(),
                            bestRecommendation = null,
                            alternativeRecommendations = emptyList(),
                            selectedOpportunity = null,
                            isLoadingRecommendations = false,
                            recommendationError = true,
                            calculationState = RecommendationCalculationState.ERROR
                        )
                    }
                } else {
                    val top = recs.firstOrNull { it.isTopRecommendation } ?: recs.first()
                    val alternatives = recs.filter { it.id != top.id }
                    _uiState.update {
                        it.copy(
                            recommendations = recs,
                            bestRecommendation = top,
                            alternativeRecommendations = alternatives,
                            selectedOpportunity = top,
                            isLoadingRecommendations = false,
                            recommendationError = false,
                            calculationState = RecommendationCalculationState.SUCCESS,
                            currentStep = SellingStep.RECOMMENDATIONS
                        )
                    }
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        recommendations = emptyList(),
                        bestRecommendation = null,
                        alternativeRecommendations = emptyList(),
                        isLoadingRecommendations = false,
                        recommendationError = true,
                        calculationState = RecommendationCalculationState.ERROR
                    )
                }
            }
        }
    }

    fun selectOpportunityToSell(opportunity: SellingOpportunity) {
        _uiState.update {
            it.copy(
                selectedOpportunity = opportunity,
                currentStep = SellingStep.CONFIRM_SELL,
                lotCreationStep = LotCreationStep.PRODUCE_SUMMARY,
                publishError = false,
                publishErrorMessageRes = null,
                publishErrorMessage = null,
                isPublishingLot = false
            )
        }
    }

    fun goToLotCreationStep(step: LotCreationStep) {
        _uiState.update { it.copy(lotCreationStep = step) }
    }

    fun nextLotCreationStep() {
        val next = when (_uiState.value.lotCreationStep) {
            LotCreationStep.PRODUCE_SUMMARY -> LotCreationStep.CHECK_DETAILS
            LotCreationStep.CHECK_DETAILS -> LotCreationStep.ADD_PHOTOS
            LotCreationStep.ADD_PHOTOS -> LotCreationStep.READY_TO_PUBLISH
            LotCreationStep.READY_TO_PUBLISH -> LotCreationStep.READY_TO_PUBLISH
        }
        _uiState.update { it.copy(lotCreationStep = next) }
    }

    fun previousLotCreationStep() {
        when (_uiState.value.lotCreationStep) {
            LotCreationStep.PRODUCE_SUMMARY -> {
                _uiState.update { it.copy(currentStep = SellingStep.RECOMMENDATIONS) }
            }
            LotCreationStep.CHECK_DETAILS -> {
                _uiState.update { it.copy(lotCreationStep = LotCreationStep.PRODUCE_SUMMARY) }
            }
            LotCreationStep.ADD_PHOTOS -> {
                _uiState.update { it.copy(lotCreationStep = LotCreationStep.CHECK_DETAILS) }
            }
            LotCreationStep.READY_TO_PUBLISH -> {
                _uiState.update { it.copy(lotCreationStep = LotCreationStep.ADD_PHOTOS) }
            }
        }
    }

    fun addPhoto(photoUri: String) {
        if (photoUri.isNotBlank()) {
            _uiState.update { it.copy(lotPhotos = it.lotPhotos + photoUri) }
        }
    }

    fun removePhoto(index: Int) {
        _uiState.update {
            val updated = it.lotPhotos.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
            }
            it.copy(lotPhotos = updated)
        }
    }

    fun removePhoto(uri: String) {
        _uiState.update { it.copy(lotPhotos = it.lotPhotos - uri) }
    }

    fun clearPhotos() {
        _uiState.update { it.copy(lotPhotos = emptyList()) }
    }

    fun validateLotCreation(): Int? {
        val state = _uiState.value
        if (state.selectedCrop.id.isBlank()) return R.string.validation_select_crop
        if (state.quantityQuintals <= 0) return R.string.validation_enter_quantity
        if (state.qualityKey.isBlank() && state.qualityRes == 0) return R.string.validation_select_quality
        if (state.location.isBlank() && state.locationRes == 0) return R.string.validation_confirm_location
        if (state.selectedOpportunity == null && state.bestRecommendation == null) return R.string.validation_select_destination
        return null
    }

    fun publishLot() {
        // Prevent duplicate publishing
        if (_uiState.value.createdLot != null && _uiState.value.currentStep == SellingStep.LOT_CREATED) {
            return
        }
        val errorRes = validateLotCreation()
        if (errorRes != null) {
            _uiState.update { it.copy(publishError = true, publishErrorMessageRes = errorRes) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPublishingLot = true, publishError = false, publishErrorMessageRes = null, publishErrorMessage = null) }
            try {
                val state = _uiState.value
                val opp = state.selectedOpportunity ?: state.bestRecommendation
                val buyerRes = opp?.buyerNameRes ?: R.string.buyer_abc_foods
                val quotedPrice = opp?.quotedPricePerQ ?: 4850
                val estimatedNet = opp?.estimatedNetPricePerQ ?: 4700

                val lot = repository.publishLot(
                    cropRes = state.selectedCrop.nameRes,
                    emoji = state.selectedCrop.emoji,
                    quantity = state.quantityQuintals,
                    qualityRes = state.qualityRes,
                    buyerNameRes = buyerRes,
                    location = state.location.ifBlank { "Nagpur, Maharashtra" },
                    readyTiming = state.readyTiming.ifBlank { "Ready now" },
                    expectedPricePerQ = quotedPrice,
                    estimatedNetPerQ = estimatedNet,
                    photos = state.lotPhotos
                )
                _uiState.update {
                    it.copy(
                        createdLot = lot,
                        isPublishingLot = false,
                        publishError = false,
                        currentStep = SellingStep.LOT_CREATED
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isPublishingLot = false,
                        publishError = true,
                        publishErrorMessage = e.message ?: "Failed to publish lot"
                    )
                }
            }
        }
    }

    fun setRecommendationsForTest(recs: List<SellingOpportunity>) {
        val top = recs.firstOrNull { it.isTopRecommendation } ?: recs.firstOrNull()
        val alternatives = if (top != null) recs.filter { it.id != top.id } else emptyList()
        _uiState.update {
            it.copy(
                recommendations = recs,
                bestRecommendation = top,
                alternativeRecommendations = alternatives,
                selectedOpportunity = top,
                calculationState = RecommendationCalculationState.SUCCESS,
                currentStep = SellingStep.RECOMMENDATIONS
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
                locationSource = LocationSource.MANUAL_SELECTION,
                harvestReadiness = HarvestReadiness.READY_NOW,
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
