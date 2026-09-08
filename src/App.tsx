import React from 'react';
import { AgriProvider, useAgri } from './context/AgriContext';
import { AgriTopBar } from './components/AgriTopBar';
import { FarmerBottomNav } from './components/FarmerBottomNav';

// Screens
import { FarmerHomeScreen } from './screens/FarmerHomeScreen';
import { PricesScreen } from './screens/PricesScreen';
import { SellScreen } from './screens/SellScreen';
import { MyLotsScreen } from './screens/MyLotsScreen';
import { HelpScreen } from './screens/HelpScreen';
import { LotDetailsScreen } from './screens/LotDetailsScreen';
import { OffersScreen } from './screens/OffersScreen';
import { OfferDetailScreen } from './screens/OfferDetailScreen';
import { AcceptOfferConfirmScreen } from './screens/AcceptOfferConfirmScreen';
import { TransactionDetailScreen } from './screens/TransactionDetailScreen';
import { ArrangeTransportScreen } from './screens/ArrangeTransportScreen';
import { ProducePickupScreen } from './screens/ProducePickupScreen';
import { DeliveryTrackingScreen } from './screens/DeliveryTrackingScreen';
import { PaymentTrackingScreen } from './screens/PaymentTrackingScreen';
import { SaleCompletedScreen } from './screens/SaleCompletedScreen';
import { BuyersDirectoryScreen } from './screens/BuyersDirectoryScreen';
import { BuyerProfileScreen } from './screens/BuyerProfileScreen';

const MainLayout: React.FC = () => {
  const { screen, snackMessage } = useAgri();

  const renderContent = () => {
    if (screen.type === 'main_nav') {
      switch (screen.tab) {
        case 'home':
          return <FarmerHomeScreen />;
        case 'prices':
          return <PricesScreen />;
        case 'sell':
          return <SellScreen />;
        case 'my_lots':
          return <MyLotsScreen />;
        case 'help':
          return <HelpScreen />;
        default:
          return <FarmerHomeScreen />;
      }
    }

    switch (screen.type) {
      case 'lot_details':
        return <LotDetailsScreen lotId={screen.lotId} />;
      case 'offers':
        return <OffersScreen lotId={screen.lotId} />;
      case 'offer_details':
        return <OfferDetailScreen offerId={screen.offerId} />;
      case 'accept_offer_confirm':
        return <AcceptOfferConfirmScreen offerId={screen.offerId} />;
      case 'transaction_detail':
        return <TransactionDetailScreen transactionId={screen.transactionId} />;
      case 'arrange_transport':
        return <ArrangeTransportScreen transactionId={screen.transactionId} />;
      case 'produce_pickup':
        return <ProducePickupScreen transactionId={screen.transactionId} />;
      case 'delivery_tracking':
        return <DeliveryTrackingScreen transactionId={screen.transactionId} />;
      case 'payment_tracking':
        return <PaymentTrackingScreen transactionId={screen.transactionId} />;
      case 'sale_completed':
        return <SaleCompletedScreen transactionId={screen.transactionId} />;
      case 'buyers_directory':
        return <BuyersDirectoryScreen />;
      case 'buyer_profile':
        return <BuyerProfileScreen buyerId={screen.buyerId} />;
      case 'your_sales':
        return <FarmerHomeScreen />;
      default:
        return <FarmerHomeScreen />;
    }
  };

  return (
    <div className="min-h-screen bg-[#F4F6F0] flex justify-center selection:bg-[#E8F5E9] selection:text-[#1B5E20]">
      <div
        id="app-container"
        className="w-full max-w-md min-h-screen bg-[#FBFBF6] shadow-xl flex flex-col relative border-x border-[#D2DCC7]"
      >
        <AgriTopBar />

        <main className="flex-1 overflow-y-auto">
          {renderContent()}
        </main>

        <FarmerBottomNav />

        {/* Global Toast / Snackbar notification */}
        {snackMessage && (
          <div
            id="app-snackbar"
            className="fixed bottom-20 left-1/2 -translate-x-1/2 z-50 bg-[#141A13] text-white text-xs font-semibold px-4 py-2.5 rounded-full shadow-lg border border-white/20 animate-in fade-in slide-in-from-bottom-2 flex items-center gap-2"
          >
            <span className="w-2 h-2 rounded-full bg-emerald-400" />
            <span>{snackMessage}</span>
          </div>
        )}
      </div>
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <AgriProvider>
      <MainLayout />
    </AgriProvider>
  );
};

export default App;
