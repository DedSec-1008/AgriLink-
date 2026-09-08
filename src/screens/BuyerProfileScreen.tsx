import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { initialBuyers } from '../data/mockData';
import {
  ChevronLeft,
  Star,
  ShieldCheck,
  MapPin,
  Phone,
  CheckCircle2,
  Clock,
  ArrowRight,
  Sparkles,
} from 'lucide-react';

interface BuyerProfileScreenProps {
  buyerId: string;
}

export const BuyerProfileScreen: React.FC<BuyerProfileScreenProps> = ({ buyerId }) => {
  const { t, navigateTo, navigateTab, showSnack } = useAgri();
  const [showPhone, setShowPhone] = useState(false);

  const buyer = initialBuyers.find((b) => b.id === buyerId) || initialBuyers[0];

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <button
        onClick={() => navigateTo({ type: 'buyers_directory' })}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('action_browse_buyers')}</span>
      </button>

      {/* Buyer Header Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
        <div className="flex items-start justify-between">
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-xl font-black text-[#141A13]">
                {t(buyer.nameKey)}
              </h2>
              {buyer.isVerified && (
                <ShieldCheck className="w-5 h-5 text-emerald-600" />
              )}
            </div>
            <p className="text-xs text-[#4A5546] mt-0.5">{buyer.address}</p>
          </div>

          <div className="text-right">
            <div className="flex items-center gap-1 text-amber-600 justify-end">
              <Star className="w-4 h-4 fill-amber-400 text-amber-400" />
              <span className="font-black text-sm">{buyer.rating}</span>
            </div>
            <span className="text-[10px] text-[#4A5546] block">
              {buyer.completedTransactionsCount} trades
            </span>
          </div>
        </div>

        {/* Verification badges */}
        <div className="flex flex-wrap gap-2 pt-1 text-xs">
          <span className="inline-flex items-center gap-1 px-2.5 py-1 bg-emerald-50 text-emerald-800 rounded-lg font-bold border border-emerald-200">
            <CheckCircle2 className="w-3.5 h-3.5" />
            {t('verified_buyer_tag')}
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-1 bg-blue-50 text-blue-800 rounded-lg font-bold border border-blue-200">
            APMC Mandi Reg: #NGP-774
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-1 bg-purple-50 text-purple-800 rounded-lg font-bold border border-purple-200">
            GST & Mandi Cess Compliant
          </span>
        </div>

        {/* Performance metrics */}
        <div className="grid grid-cols-2 gap-2 pt-1">
          <div className="p-3 bg-[#F9FAF6] rounded-xl border border-[#E0E5D7] text-xs">
            <span className="text-[#4A5546] block">{t('buyer_profile_payment_speed')}:</span>
            <span className="font-bold text-[#141A13] block mt-0.5">
              ⚡ {t(buyer.paymentTurnaroundKey || 'payment_within_2_days')}
            </span>
          </div>

          <div className="p-3 bg-[#F9FAF6] rounded-xl border border-[#E0E5D7] text-xs">
            <span className="text-[#4A5546] block">Payment Reliability:</span>
            <span className="font-bold text-emerald-800 block mt-0.5">
              100% On-Time Record
            </span>
          </div>
        </div>
      </div>

      {/* Currently Purchasing Crops */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-3">
        <h3 className="font-bold text-xs text-[#4A5546] uppercase tracking-wider">
          Currently Purchasing
        </h3>

        <div className="grid grid-cols-2 gap-2">
          {(buyer.acceptedCropsKeys || ['crop_soybean', 'crop_wheat']).map((ck) => (
            <div
              key={ck}
              className="p-3 rounded-xl bg-[#E8F5E9] border border-[#A5D6A7] flex items-center justify-between"
            >
              <span className="font-bold text-xs text-[#1B5E20]">{t(ck)}</span>
              <span className="text-[10px] font-bold text-emerald-700 bg-white/80 px-1.5 py-0.5 rounded">
                High Demand
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* Recent Farmer Reviews */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-3">
        <h3 className="font-bold text-xs text-[#4A5546] uppercase tracking-wider">
          Recent Farmer Feedback
        </h3>

        <div className="space-y-2.5 text-xs">
          <div className="p-3 bg-[#FBFBF6] rounded-xl border border-[#D2DCC7] space-y-1">
            <div className="flex items-center justify-between">
              <span className="font-bold text-[#141A13]">Ganesh Deshmukh (Katol)</span>
              <div className="flex text-amber-400">★★★★★</div>
            </div>
            <p className="text-[#4A5546]">
              "Very fair weighment and payment was credited directly within 2 hours of unloading."
            </p>
          </div>

          <div className="p-3 bg-[#FBFBF6] rounded-xl border border-[#D2DCC7] space-y-1">
            <div className="flex items-center justify-between">
              <span className="font-bold text-[#141A13]">Sunil Wankhede (Hingna)</span>
              <div className="flex text-amber-400">★★★★★</div>
            </div>
            <p className="text-[#4A5546]">
              "Quick unloading, no unnecessary deduction for moisture. Transparent deal."
            </p>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="space-y-2 pt-1">
        <button
          id="sell-to-this-buyer-btn"
          onClick={() => navigateTab('sell')}
          className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
        >
          <Sparkles className="w-4 h-4 text-amber-300" />
          <span>{t('btn_sell_here')} ({t(buyer.nameKey)})</span>
        </button>

        <button
          onClick={() => {
            setShowPhone(true);
            showSnack(t('helpline_label') + ': 1800-180-1551');
          }}
          className="w-full py-2.5 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] hover:text-[#141A13] font-bold text-xs rounded-xl flex items-center justify-center gap-1.5 hover:bg-[#F1F4EB]"
        >
          <Phone className="w-3.5 h-3.5" />
          <span>{t('btn_contact_buyer')}</span>
        </button>

        {showPhone && (
          <div className="p-3 bg-[#E8F5E9] border border-[#A5D6A7] rounded-xl text-xs text-[#1B5E20] text-center font-bold">
            📞 Trading Desk: +91 712 254 9901 / Toll-Free: 1800-180-1551
          </div>
        )}
      </div>
    </div>
  );
};
