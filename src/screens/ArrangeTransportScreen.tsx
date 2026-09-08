import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { ChevronLeft, Truck, Star, ShieldCheck, MapPin, Check, ArrowRight } from 'lucide-react';
import { TransporterOption } from '../types';

interface ArrangeTransportScreenProps {
  transactionId: string;
}

export const ArrangeTransportScreen: React.FC<ArrangeTransportScreenProps> = ({
  transactionId,
}) => {
  const { t, transactions, transporters, bookTransport, navigateTo } = useAgri();
  const tx = transactions.find((item) => item.id === transactionId);
  const [selectedTransporter, setSelectedTransporter] = useState<TransporterOption>(
    transporters[0]
  );
  const [isBooked, setIsBooked] = useState(false);

  if (!tx) {
    return <div className="p-6 text-center text-sm text-[#4A5546]">Transaction not found.</div>;
  }

  const handleBook = () => {
    bookTransport(transactionId, selectedTransporter);
    setIsBooked(true);
    setTimeout(() => {
      navigateTo({ type: 'produce_pickup', transactionId });
    }, 1200);
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
          {t('title_arrange_transport')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">
          {t('subtitle_arrange_transport')}
        </p>
      </div>

      {/* Route & Distance Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-4 shadow-xs space-y-3">
        <div className="flex items-center justify-between text-xs font-bold text-[#4A5546] uppercase tracking-wider">
          <span>Route</span>
          <span className="text-[#1B5E20]">
            {t('label_distance_val', selectedTransporter.distanceKm)}
          </span>
        </div>

        <div className="space-y-2 text-xs">
          <div className="flex items-start gap-2.5">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-600 mt-1 shrink-0" />
            <div>
              <span className="text-[#4A5546] block">{t('label_pickup_location')}</span>
              <span className="font-bold text-[#141A13]">{t(selectedTransporter.pickupLocationKey)}</span>
            </div>
          </div>

          <div className="flex items-start gap-2.5">
            <div className="w-2.5 h-2.5 rounded-full bg-amber-600 mt-1 shrink-0" />
            <div>
              <span className="text-[#4A5546] block">{t('label_delivery_location')}</span>
              <span className="font-bold text-[#141A13]">{selectedTransporter.deliveryLocation}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Available Transporters List */}
      <div className="space-y-2.5">
        <span className="text-xs font-extrabold text-[#4A5546] uppercase tracking-wider px-1">
          {t('recommended_transport_header')}
        </span>

        {transporters.map((trans) => (
          <button
            key={trans.id}
            id={`transporter-option-${trans.id}`}
            onClick={() => setSelectedTransporter(trans)}
            className={`w-full p-4 rounded-2xl border text-left transition-all ${
              selectedTransporter.id === trans.id
                ? 'border-2 border-[#1B5E20] bg-[#E8F5E9] shadow-xs'
                : 'border-[#D2DCC7] bg-white hover:bg-[#F9FAF6]'
            }`}
          >
            <div className="flex items-start justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="font-bold text-sm text-[#141A13]">
                    {trans.name}
                  </h3>
                  <span className="inline-flex items-center gap-0.5 text-[10px] font-bold text-amber-600">
                    <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
                    {trans.rating}
                  </span>
                </div>
                <p className="text-xs text-[#4A5546] mt-0.5">
                  {t(trans.vehicleTypeKey)} • {t(trans.estimatedPickupTimeKey)}
                </p>
              </div>

              <div className="text-right">
                <span className="font-black text-sm text-[#1B5E20]">
                  ₹{trans.totalCost.toLocaleString()}
                </span>
                <span className="text-[10px] text-[#4A5546] block">
                  (₹{trans.costPerQuintal} / q)
                </span>
              </div>
            </div>
          </button>
        ))}
      </div>

      {/* Confirmation & CTA */}
      <div className="pt-2">
        {isBooked ? (
          <div className="p-3.5 bg-emerald-100 text-emerald-800 rounded-xl text-center font-bold text-sm flex items-center justify-center gap-2">
            <Check className="w-5 h-5 stroke-[3]" />
            <span>{t('transport_arranged_header')}</span>
          </div>
        ) : (
          <button
            id="confirm-book-transport-btn"
            onClick={handleBook}
            className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
          >
            <Truck className="w-4 h-4" />
            <span>{t('btn_book_transport')}</span>
          </button>
        )}
      </div>
    </div>
  );
};
