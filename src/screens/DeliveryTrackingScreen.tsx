import React from 'react';
import { useAgri } from '../context/AgriContext';
import { ChevronLeft, Truck, CheckCircle2, ArrowRight, MapPin } from 'lucide-react';

interface DeliveryTrackingScreenProps {
  transactionId: string;
}

export const DeliveryTrackingScreen: React.FC<DeliveryTrackingScreenProps> = ({
  transactionId,
}) => {
  const { t, transactions, confirmDelivery, navigateTo } = useAgri();
  const tx = transactions.find((item) => item.id === transactionId);

  if (!tx) {
    return <div className="p-6 text-center text-sm text-[#4A5546]">Transaction not found.</div>;
  }

  const isDelivered = tx.status === 'DELIVERED' || tx.status === 'PAYMENT_INITIATED' || tx.status === 'COMPLETED';

  const handleConfirm = () => {
    confirmDelivery(transactionId);
    navigateTo({ type: 'payment_tracking', transactionId });
  };

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <button
        onClick={() => navigateTo({ type: 'transaction_detail', transactionId })}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('title_transaction_detail')}</span>
      </button>

      <div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight">
          {isDelivered ? t('title_produce_delivered') : t('title_produce_in_transit')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">
          {isDelivered ? t('delivered_today') : t('expected_delivery_today')}
        </p>
      </div>

      {/* In Transit Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
        <div className="flex items-center justify-between">
          <span className="text-xs font-extrabold text-[#1B5E20] uppercase tracking-wider">
            {isDelivered ? '✓ Unloaded & Weighed' : t('status_in_transit')}
          </span>
          <span className="text-xs font-semibold text-[#4A5546]">
            {t(tx.buyerNameKey)}
          </span>
        </div>

        {/* Live Route status meter */}
        <div className="space-y-3 pt-2">
          <div className="w-full bg-[#E8F5E9] h-2.5 rounded-full overflow-hidden">
            <div
              className={`bg-[#1B5E20] h-full transition-all duration-500 rounded-full ${
                isDelivered ? 'w-full' : 'w-3/4 animate-pulse'
              }`}
            />
          </div>

          <div className="flex items-center justify-between text-xs text-[#4A5546]">
            <span>Nagpur Farmgate</span>
            <span className="font-bold text-[#141A13]">
              {isDelivered ? 'Arrival Confirmed' : 'Approaching Mandi'}
            </span>
            <span>Buyer Center</span>
          </div>
        </div>

        <div className="p-3 bg-[#F9FAF6] rounded-xl border border-[#E0E5D7] space-y-1.5 text-xs">
          <div className="flex justify-between">
            <span className="text-[#4A5546]">Driver:</span>
            <span className="font-bold text-[#141A13]">Ramesh Patil (+91 98221 XXXXX)</span>
          </div>
          <div className="flex justify-between">
            <span className="text-[#4A5546]">Transporter:</span>
            <span className="font-bold text-[#141A13]">
              {tx.transporterBooking?.transporterName || t('transporter_shree_agro')}
            </span>
          </div>
        </div>
      </div>

      {!isDelivered ? (
        <button
          id="confirm-delivery-btn"
          onClick={handleConfirm}
          className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
        >
          <CheckCircle2 className="w-5 h-5" />
          <span>{t('btn_confirm_delivery')}</span>
        </button>
      ) : (
        <button
          onClick={() => navigateTo({ type: 'payment_tracking', transactionId })}
          className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
        >
          <span>{t('action_track_payment')}</span>
          <ArrowRight className="w-4 h-4" />
        </button>
      )}
    </div>
  );
};
