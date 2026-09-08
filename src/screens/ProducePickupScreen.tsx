import React from 'react';
import { useAgri } from '../context/AgriContext';
import { ChevronLeft, Truck, Check, MapPin, ArrowRight, ShieldCheck } from 'lucide-react';

interface ProducePickupScreenProps {
  transactionId: string;
}

export const ProducePickupScreen: React.FC<ProducePickupScreenProps> = ({
  transactionId,
}) => {
  const { t, transactions, confirmPickup, navigateTo } = useAgri();
  const tx = transactions.find((item) => item.id === transactionId);

  if (!tx) {
    return <div className="p-6 text-center text-sm text-[#4A5546]">Transaction not found.</div>;
  }

  const handleConfirm = () => {
    confirmPickup(transactionId);
    navigateTo({ type: 'delivery_tracking', transactionId });
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
          {t('title_produce_pickup')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">
          {t('msg_transporter_ready')}
        </p>
      </div>

      {/* Pickup Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-xl bg-[#E8F5E9] text-[#1B5E20] flex items-center justify-center text-2xl border border-[#A5D6A7]">
            🚚
          </div>
          <div>
            <h3 className="font-bold text-base text-[#141A13]">
              {tx.transporterBooking?.transporterName || t('transporter_shree_agro')}
            </h3>
            <p className="text-xs text-[#4A5546] mt-0.5">
              Vehicle: MH-31-CB-4921 • Driver: Ramesh Patil
            </p>
          </div>
        </div>

        <div className="p-3.5 bg-[#F9FAF6] rounded-xl border border-[#E0E5D7] space-y-2 text-xs">
          <div className="flex justify-between">
            <span className="text-[#4A5546]">{t('review_crop_label')}:</span>
            <span className="font-bold text-[#141A13]">{t(tx.cropNameKey)}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-[#4A5546]">{t('review_quantity_label')}:</span>
            <span className="font-bold text-[#141A13]">{tx.quantityQuintals} {t('unit_quintals')}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-[#4A5546]">{t('label_pickup_location')}:</span>
            <span className="font-bold text-[#141A13]">{t('location_nagpur')}</span>
          </div>
        </div>

        <div className="p-3 bg-[#E8F5E9] rounded-xl text-xs text-[#1B5E20] border border-[#A5D6A7] flex items-center gap-2">
          <ShieldCheck className="w-4 h-4 shrink-0" />
          <span>Driver has verified bag count and seal. Ready for departure.</span>
        </div>
      </div>

      <button
        id="confirm-pickup-btn"
        onClick={handleConfirm}
        className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
      >
        <Check className="w-5 h-5 stroke-[3]" />
        <span>{t('btn_confirm_pickup')}</span>
      </button>
    </div>
  );
};
