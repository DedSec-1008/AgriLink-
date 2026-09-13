package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.AgriRepository
import com.example.ui.screens.selling.*
import com.example.ui.theme.AgriBackground

/**
 * Orchestrator Composable for the multi-step guided Selling Flow.
 * Delegates individual step rendering and user interactions to focused
 * components in [com.example.ui.screens.selling].
 */
@Composable
fun SellScreen(
    repository: AgriRepository,
    viewModel: SellingViewModel,
    onNavigateToMyLots: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    onViewLotDetails: ((String) -> Unit)? = null,
    onViewOffers: ((String) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = uiState.currentStep != SellingStep.CROP) {
        viewModel.goBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgriBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Render step by step
        when (uiState.currentStep) {
            SellingStep.CROP -> StepCropScreen(
                crops = repository.getAvailableCrops(),
                selectedCrop = uiState.selectedCrop,
                onCropSelected = { viewModel.setCrop(it) },
                onContinue = { viewModel.goToStep(SellingStep.QUANTITY) },
                onBack = onNavigateToHome
            )

            SellingStep.QUANTITY -> StepQuantityScreen(
                selectedCrop = uiState.selectedCrop,
                quantity = uiState.quantityQuintals,
                onQuantityChanged = { viewModel.updateQuantity(it) },
                onContinue = { viewModel.goToStep(SellingStep.QUALITY) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.QUALITY -> StepQualityScreen(
                selectedCrop = uiState.selectedCrop,
                quantity = uiState.quantityQuintals,
                selectedQualityKey = uiState.qualityKey,
                onQualitySelected = { key, resId ->
                    viewModel.selectQuality(key, resId)
                },
                onContinue = { viewModel.goToStep(SellingStep.LOCATION) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.LOCATION -> StepLocationScreen(
                selectedCrop = uiState.selectedCrop,
                quantity = uiState.quantityQuintals,
                qualityRes = uiState.qualityRes,
                selectedLocation = uiState.location,
                locationSource = uiState.locationSource,
                onLocationSelected = { name, resId, source ->
                    viewModel.selectLocation(name, resId, source)
                },
                onContinue = { viewModel.goToStep(SellingStep.READY_DATE) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.READY_DATE -> StepReadyDateScreen(
                selectedCrop = uiState.selectedCrop,
                quantity = uiState.quantityQuintals,
                qualityRes = uiState.qualityRes,
                selectedLocation = uiState.location,
                harvestReadiness = uiState.harvestReadiness,
                selectedTiming = uiState.readyTiming,
                onReadinessSelected = { readiness ->
                    viewModel.selectHarvestReadiness(readiness)
                },
                onContinue = { viewModel.goToStep(SellingStep.REVIEW) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.REVIEW -> StepReviewScreen(
                uiState = uiState,
                onEditStep = { step -> viewModel.goToStep(step) },
                onFindBestOption = { viewModel.startAnalysisAndFindOptions() },
                onBack = { viewModel.goBack() }
            )

            SellingStep.ANALYSIS -> StepAnalysisScreen(
                progressIndex = uiState.analysisProgressIndex
            )

            SellingStep.RECOMMENDATIONS -> StepRecommendationsScreen(
                uiState = uiState,
                onSelectOpportunity = { viewModel.selectOpportunityToSell(it) },
                onEditDetails = { viewModel.goToStep(SellingStep.REVIEW) },
                onRetry = { viewModel.startAnalysisAndFindOptions() },
                onBack = { viewModel.goBack() }
            )

            SellingStep.CONFIRM_SELL -> StepConfirmSellScreen(
                uiState = uiState,
                viewModel = viewModel,
                onEditDetails = { viewModel.goToStep(SellingStep.REVIEW) },
                onConfirm = { viewModel.confirmCreateLot() },
                onBack = { viewModel.goBack() }
            )

            SellingStep.LOT_CREATED -> StepLotCreatedScreen(
                uiState = uiState,
                onViewMyLot = {
                    val lotId = uiState.createdLot?.lotId
                    if (lotId != null && onViewLotDetails != null) {
                        onViewLotDetails(lotId)
                    } else {
                        onNavigateToMyLots()
                    }
                },
                onViewOffers = {
                    val lotId = uiState.createdLot?.lotId
                    if (lotId != null && onViewOffers != null) {
                        onViewOffers(lotId)
                    } else {
                        onNavigateToMyLots()
                    }
                },
                onDone = {
                    viewModel.resetFlow()
                    onNavigateToHome()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
