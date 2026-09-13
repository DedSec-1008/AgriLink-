package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AgriRepository
import com.example.data.AppLanguage
import com.example.data.MockAgriRepository
import com.example.data.createLocalizedContext
import com.example.ui.components.AgriTopBar
import com.example.ui.components.FarmerBottomNavigation
import com.example.ui.components.FarmerNavDestination
import com.example.ui.components.FarmerNavigationRail
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ui.screens.AcceptOfferConfirmScreen
import com.example.ui.screens.BuyerProfileScreen
import com.example.ui.screens.BuyersScreen
import com.example.ui.screens.FarmerHomeScreen
import com.example.ui.screens.HelpScreen
import com.example.ui.screens.LotDetailsScreen
import com.example.ui.screens.MyLotsScreen
import com.example.ui.screens.OfferDetailScreen
import com.example.ui.screens.OffersScreen
import com.example.ui.screens.PricesScreen
import com.example.ui.screens.SellScreen
import com.example.ui.screens.SellingViewModel
import com.example.ui.screens.TransactionPreviewScreen
import com.example.ui.screens.TransactionDetailScreen
import com.example.ui.screens.ArrangeTransportScreen
import com.example.ui.screens.ProducePickupScreen
import com.example.ui.screens.DeliveryTrackingScreen
import com.example.ui.screens.PaymentTrackingScreen
import com.example.ui.screens.SaleCompletedScreen
import com.example.ui.screens.YourSalesScreen
import com.example.ui.theme.AgriLinkTheme
import kotlinx.coroutines.launch

sealed interface AppScreen {
    data object MainNav : AppScreen
    data object BuyersDirectory : AppScreen
    data class BuyerProfile(val buyerId: String) : AppScreen
    data class LotDetails(val lotId: String) : AppScreen
    data class Offers(val lotId: String) : AppScreen
    data class OfferDetails(val offerId: String) : AppScreen
    data class AcceptOfferConfirm(val offerId: String) : AppScreen
    data class TransactionPreview(val transactionId: String) : AppScreen
    data class TransactionDetail(val transactionId: String) : AppScreen
    data class ArrangeTransport(val transactionId: String) : AppScreen
    data class ProducePickup(val transactionId: String) : AppScreen
    data class DeliveryTracking(val transactionId: String) : AppScreen
    data class PaymentTracking(val transactionId: String) : AppScreen
    data class SaleCompleted(val transactionId: String) : AppScreen
    data object YourSales : AppScreen
}

val AppScreenSaver = Saver<AppScreen, String>(
    save = { screen ->
        when (screen) {
            is AppScreen.MainNav -> "MainNav"
            is AppScreen.BuyersDirectory -> "BuyersDirectory"
            is AppScreen.BuyerProfile -> "BuyerProfile:${screen.buyerId}"
            is AppScreen.LotDetails -> "LotDetails:${screen.lotId}"
            is AppScreen.Offers -> "Offers:${screen.lotId}"
            is AppScreen.OfferDetails -> "OfferDetails:${screen.offerId}"
            is AppScreen.AcceptOfferConfirm -> "AcceptOfferConfirm:${screen.offerId}"
            is AppScreen.TransactionPreview -> "TransactionPreview:${screen.transactionId}"
            is AppScreen.TransactionDetail -> "TransactionDetail:${screen.transactionId}"
            is AppScreen.ArrangeTransport -> "ArrangeTransport:${screen.transactionId}"
            is AppScreen.ProducePickup -> "ProducePickup:${screen.transactionId}"
            is AppScreen.DeliveryTracking -> "DeliveryTracking:${screen.transactionId}"
            is AppScreen.PaymentTracking -> "PaymentTracking:${screen.transactionId}"
            is AppScreen.SaleCompleted -> "SaleCompleted:${screen.transactionId}"
            is AppScreen.YourSales -> "YourSales"
        }
    },
    restore = { serialized ->
        val parts = serialized.split(":", limit = 2)
        when (parts[0]) {
            "MainNav" -> AppScreen.MainNav
            "BuyersDirectory" -> AppScreen.BuyersDirectory
            "BuyerProfile" -> AppScreen.BuyerProfile(parts.getOrElse(1) { "" })
            "LotDetails" -> AppScreen.LotDetails(parts.getOrElse(1) { "" })
            "Offers" -> AppScreen.Offers(parts.getOrElse(1) { "" })
            "OfferDetails" -> AppScreen.OfferDetails(parts.getOrElse(1) { "" })
            "AcceptOfferConfirm" -> AppScreen.AcceptOfferConfirm(parts.getOrElse(1) { "" })
            "TransactionPreview" -> AppScreen.TransactionPreview(parts.getOrElse(1) { "" })
            "TransactionDetail" -> AppScreen.TransactionDetail(parts.getOrElse(1) { "" })
            "ArrangeTransport" -> AppScreen.ArrangeTransport(parts.getOrElse(1) { "" })
            "ProducePickup" -> AppScreen.ProducePickup(parts.getOrElse(1) { "" })
            "DeliveryTracking" -> AppScreen.DeliveryTracking(parts.getOrElse(1) { "" })
            "PaymentTracking" -> AppScreen.PaymentTracking(parts.getOrElse(1) { "" })
            "SaleCompleted" -> AppScreen.SaleCompleted(parts.getOrElse(1) { "" })
            "YourSales" -> AppScreen.YourSales
            else -> AppScreen.MainNav
        }
    }
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriLinkFarmerApp()
        }
    }
}

@Composable
fun AgriLinkFarmerApp(
    customRepository: AgriRepository? = null,
    appViewModel: com.example.ui.AgriAppViewModel = viewModel(
        factory = com.example.ui.AgriAppViewModel.Factory(LocalContext.current)
    )
) {
    var selectedLanguage by rememberSaveable { mutableStateOf(AppLanguage.ENGLISH) }
    var currentDestination by rememberSaveable { mutableStateOf(FarmerNavDestination.HOME) }
    var currentScreen by rememberSaveable(stateSaver = AppScreenSaver) { mutableStateOf<AppScreen>(AppScreen.MainNav) }

    val repository: AgriRepository = customRepository ?: appViewModel.repository
    val sellingViewModel: SellingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SellingViewModel(repository) as T
            }
        }
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val baseContext = LocalContext.current
    val localizedContext = remember(selectedLanguage, baseContext) {
        baseContext.createLocalizedContext(selectedLanguage.code)
    }
    val registryOwner = LocalActivityResultRegistryOwner.current
        ?: (baseContext as? ActivityResultRegistryOwner)

    val compositionLocals = remember(localizedContext, registryOwner) {
        if (registryOwner != null) {
            arrayOf(
                LocalContext provides localizedContext,
                LocalActivityResultRegistryOwner provides registryOwner
            )
        } else {
            arrayOf(LocalContext provides localizedContext)
        }
    }

    CompositionLocalProvider(*compositionLocals) {
        AgriLinkTheme {
            val appScreenContent: @Composable () -> Unit = {
                when (val screen = currentScreen) {
                        is AppScreen.MainNav -> {
                            when (currentDestination) {
                                FarmerNavDestination.HOME -> FarmerHomeScreen(
                                    repository = repository,
                                    onNavigateToSell = { currentDestination = FarmerNavDestination.SELL },
                                    onNavigateToPrices = { currentDestination = FarmerNavDestination.PRICES },
                                    onNavigateToMyLots = { currentDestination = FarmerNavDestination.MY_LOTS },
                                    onNavigateToHelp = { currentDestination = FarmerNavDestination.HELP },
                                    onNavigateToBuyers = { currentScreen = AppScreen.BuyersDirectory },
                                    onNavigateToActiveSale = { txId -> currentScreen = AppScreen.TransactionDetail(txId) }
                                )
                                FarmerNavDestination.PRICES -> PricesScreen(
                                    repository = repository,
                                    onNavigateToSell = { currentDestination = FarmerNavDestination.SELL }
                                )
                                FarmerNavDestination.SELL -> SellScreen(
                                    repository = repository,
                                    viewModel = sellingViewModel,
                                    onNavigateToMyLots = { currentDestination = FarmerNavDestination.MY_LOTS },
                                    onNavigateToHome = { currentDestination = FarmerNavDestination.HOME },
                                    onViewLotDetails = { lotId -> currentScreen = AppScreen.LotDetails(lotId) },
                                    onViewOffers = { lotId -> currentScreen = AppScreen.Offers(lotId) }
                                )
                                FarmerNavDestination.MY_LOTS -> MyLotsScreen(
                                    repository = repository,
                                    onNavigateToSell = { currentDestination = FarmerNavDestination.SELL },
                                    snackbarHostState = snackbarHostState,
                                    onViewLotDetails = { lotId -> currentScreen = AppScreen.LotDetails(lotId) }
                                )
                                FarmerNavDestination.HELP -> HelpScreen(
                                    repository = repository,
                                    snackbarHostState = snackbarHostState
                                )
                            }
                        }
                        is AppScreen.BuyersDirectory -> {
                            BackHandler { currentScreen = AppScreen.MainNav }
                            BuyersScreen(
                                repository = repository,
                                onViewBuyer = { buyerId -> currentScreen = AppScreen.BuyerProfile(buyerId) },
                                onSellToBuyer = { buyerId ->
                                    sellingViewModel.startSellingToBuyer(buyerId)
                                    currentDestination = FarmerNavDestination.SELL
                                    currentScreen = AppScreen.MainNav
                                }
                            )
                        }
                        is AppScreen.BuyerProfile -> {
                            BackHandler { currentScreen = AppScreen.BuyersDirectory }
                            BuyerProfileScreen(
                                buyerId = screen.buyerId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.BuyersDirectory },
                                onSellToBuyer = {
                                    sellingViewModel.startSellingToBuyer(screen.buyerId)
                                    currentDestination = FarmerNavDestination.SELL
                                    currentScreen = AppScreen.MainNav
                                }
                            )
                        }
                        is AppScreen.LotDetails -> {
                            BackHandler {
                                currentScreen = AppScreen.MainNav
                                currentDestination = FarmerNavDestination.MY_LOTS
                            }
                            LotDetailsScreen(
                                lotId = screen.lotId,
                                repository = repository,
                                onBack = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.MY_LOTS
                                },
                                onViewOffers = { lotId -> currentScreen = AppScreen.Offers(lotId) },
                                onViewActiveSale = { txId -> currentScreen = AppScreen.TransactionDetail(txId) },
                                onFindBuyers = { currentScreen = AppScreen.BuyersDirectory }
                            )
                        }
                        is AppScreen.Offers -> {
                            BackHandler { currentScreen = AppScreen.LotDetails(screen.lotId) }
                            OffersScreen(
                                lotId = screen.lotId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.LotDetails(screen.lotId) },
                                onViewOfferDetails = { offerId -> currentScreen = AppScreen.OfferDetails(offerId) },
                                onAcceptOfferClicked = { offerId -> currentScreen = AppScreen.AcceptOfferConfirm(offerId) },
                                onFindBuyers = { currentScreen = AppScreen.BuyersDirectory }
                            )
                        }
                        is AppScreen.OfferDetails -> {
                            val offer = repository.getOfferById(screen.offerId)
                            BackHandler {
                                currentScreen = if (offer != null) AppScreen.Offers(offer.lotId) else AppScreen.MainNav
                            }
                            OfferDetailScreen(
                                offerId = screen.offerId,
                                repository = repository,
                                onBack = {
                                    currentScreen = if (offer != null) AppScreen.Offers(offer.lotId) else AppScreen.MainNav
                                },
                                onAcceptOffer = { offerId -> currentScreen = AppScreen.AcceptOfferConfirm(offerId) },
                                onDeclineOffer = { offerId ->
                                    scope.launch {
                                        repository.rejectOffer(offerId)
                                        snackbarHostState.showSnackbar("Offer declined.")
                                        if (offer != null) {
                                            currentScreen = AppScreen.Offers(offer.lotId)
                                        }
                                    }
                                },
                                onContactBuyer = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Connecting to buyer via verified AgriLink agent...")
                                    }
                                }
                            )
                        }
                        is AppScreen.AcceptOfferConfirm -> {
                            BackHandler { currentScreen = AppScreen.OfferDetails(screen.offerId) }
                            AcceptOfferConfirmScreen(
                                offerId = screen.offerId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.OfferDetails(screen.offerId) },
                                onConfirmAccept = {
                                    scope.launch {
                                        val result = repository.acceptOffer(screen.offerId)
                                        val transaction = result.getOrNull()
                                        if (transaction != null) {
                                            currentScreen = AppScreen.TransactionDetail(transaction.id)
                                        } else {
                                            snackbarHostState.showSnackbar("Offer accepted.")
                                            currentScreen = AppScreen.MainNav
                                            currentDestination = FarmerNavDestination.MY_LOTS
                                        }
                                    }
                                }
                            )
                        }
                        is AppScreen.TransactionPreview -> {
                            BackHandler {
                                currentScreen = AppScreen.MainNav
                                currentDestination = FarmerNavDestination.MY_LOTS
                            }
                            TransactionPreviewScreen(
                                transactionId = screen.transactionId,
                                repository = repository,
                                onBack = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.MY_LOTS
                                },
                                onArrangeTransport = { currentScreen = AppScreen.ArrangeTransport(screen.transactionId) },
                                onGoToMyLots = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.MY_LOTS
                                },
                                onGoToHome = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.HOME
                                }
                            )
                        }
                        is AppScreen.TransactionDetail -> {
                            BackHandler {
                                currentScreen = AppScreen.MainNav
                                currentDestination = FarmerNavDestination.MY_LOTS
                            }
                            TransactionDetailScreen(
                                transactionId = screen.transactionId,
                                repository = repository,
                                onBack = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.MY_LOTS
                                },
                                onNavigateToLogistics = { txId -> currentScreen = AppScreen.ArrangeTransport(txId) },
                                onNavigateToPickup = { txId -> currentScreen = AppScreen.ProducePickup(txId) },
                                onNavigateToDelivery = { txId -> currentScreen = AppScreen.DeliveryTracking(txId) },
                                onNavigateToPayment = { txId -> currentScreen = AppScreen.PaymentTracking(txId) },
                                onNavigateToCompleted = { txId -> currentScreen = AppScreen.SaleCompleted(txId) },
                                onNavigateToYourSales = { currentScreen = AppScreen.YourSales },
                                onGoToMyLots = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.MY_LOTS
                                },
                                onGoToHome = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.HOME
                                },
                                snackbarHostState = snackbarHostState
                            )
                        }
                        is AppScreen.ArrangeTransport -> {
                            BackHandler { currentScreen = AppScreen.TransactionDetail(screen.transactionId) }
                            ArrangeTransportScreen(
                                transactionId = screen.transactionId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.TransactionDetail(screen.transactionId) },
                                onTransportArranged = { currentScreen = AppScreen.TransactionDetail(screen.transactionId) }
                            )
                        }
                        is AppScreen.ProducePickup -> {
                            BackHandler { currentScreen = AppScreen.TransactionDetail(screen.transactionId) }
                            ProducePickupScreen(
                                transactionId = screen.transactionId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.TransactionDetail(screen.transactionId) },
                                onPickupConfirmed = { currentScreen = AppScreen.DeliveryTracking(screen.transactionId) }
                            )
                        }
                        is AppScreen.DeliveryTracking -> {
                            BackHandler { currentScreen = AppScreen.TransactionDetail(screen.transactionId) }
                            DeliveryTrackingScreen(
                                transactionId = screen.transactionId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.TransactionDetail(screen.transactionId) },
                                onTrackPayment = { currentScreen = AppScreen.PaymentTracking(screen.transactionId) }
                            )
                        }
                        is AppScreen.PaymentTracking -> {
                            BackHandler { currentScreen = AppScreen.TransactionDetail(screen.transactionId) }
                            PaymentTrackingScreen(
                                transactionId = screen.transactionId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.TransactionDetail(screen.transactionId) },
                                onSaleCompleted = { currentScreen = AppScreen.SaleCompleted(screen.transactionId) },
                                snackbarHostState = snackbarHostState
                            )
                        }
                        is AppScreen.SaleCompleted -> {
                            BackHandler { currentScreen = AppScreen.TransactionDetail(screen.transactionId) }
                            SaleCompletedScreen(
                                transactionId = screen.transactionId,
                                repository = repository,
                                onBack = { currentScreen = AppScreen.TransactionDetail(screen.transactionId) },
                                onViewTransaction = { currentScreen = AppScreen.TransactionDetail(screen.transactionId) },
                                onGoToHome = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.HOME
                                },
                                onGoToMyLots = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.MY_LOTS
                                },
                                snackbarHostState = snackbarHostState
                            )
                        }
                        is AppScreen.YourSales -> {
                            BackHandler {
                                currentScreen = AppScreen.MainNav
                                currentDestination = FarmerNavDestination.HOME
                            }
                            YourSalesScreen(
                                repository = repository,
                                onBack = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.HOME
                                },
                                onViewTransaction = { txId -> currentScreen = AppScreen.TransactionDetail(txId) },
                                onNavigateToSell = {
                                    currentScreen = AppScreen.MainNav
                                    currentDestination = FarmerNavDestination.SELL
                                }
                            )
                        }
                    }
                }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    AgriTopBar(
                        currentLanguage = selectedLanguage,
                        onLanguageSelected = { selectedLanguage = it },
                        onHelpClicked = {
                            currentScreen = AppScreen.MainNav
                            currentDestination = FarmerNavDestination.HELP
                        },
                        isCompact = isLandscape
                    )
                },
                bottomBar = {
                    FarmerBottomNavigation(
                        currentDestination = currentDestination,
                        onNavigate = {
                            currentDestination = it
                            currentScreen = AppScreen.MainNav
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    appScreenContent()
                }
            }
        }
    }
}
