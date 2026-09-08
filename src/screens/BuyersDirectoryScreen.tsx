import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { initialBuyers } from '../data/mockData';
import { Star, ShieldCheck, MapPin, ArrowRight, ChevronLeft, Search } from 'lucide-react';

export const BuyersDirectoryScreen: React.FC = () => {
  const { t, navigateTo, navigateTab } = useAgri();
  const [searchTerm, setSearchTerm] = useState('');
  const [verifiedOnly, setVerifiedOnly] = useState(false);

  const filteredBuyers = initialBuyers.filter((b) => {
    const nameMatch = t(b.nameKey).toLowerCase().includes(searchTerm.toLowerCase());
    const locMatch = (b.address || '').toLowerCase().includes(searchTerm.toLowerCase());
    const passSearch = nameMatch || locMatch;
    const passVerified = verifiedOnly ? b.isVerified : true;
    return passSearch && passVerified;
  });

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <button
        onClick={() => navigateTab('home')}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('btn_back_to_home')}</span>
      </button>

      <div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight">
          {t('action_browse_buyers')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">{t('subtitle_buyers')}</p>
      </div>

      {/* Search & Filter Bar */}
      <div className="space-y-2">
        <div className="relative">
          <Search className="w-4 h-4 text-[#4A5546] absolute left-3 top-3" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search buyers by name or mandi..."
            className="w-full pl-9 pr-3 py-2.5 text-xs bg-white border border-[#D2DCC7] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#1B5E20]"
          />
        </div>

        <div className="flex items-center justify-between px-1">
          <label className="flex items-center gap-2 text-xs font-semibold text-[#2E382B] cursor-pointer">
            <input
              type="checkbox"
              checked={verifiedOnly}
              onChange={(e) => setVerifiedOnly(e.target.checked)}
              className="rounded text-[#1B5E20] focus:ring-[#1B5E20]"
            />
            <span>{t('verified_buyer_tag')} only</span>
          </label>
          <span className="text-xs text-[#4A5546]">
            {filteredBuyers.length} buyers found
          </span>
        </div>
      </div>

      {/* Buyers List */}
      <div className="space-y-3">
        {filteredBuyers.map((buyer) => (
          <div
            key={buyer.id}
            id={`buyer-card-${buyer.id}`}
            onClick={() => navigateTo({ type: 'buyer_profile', buyerId: buyer.id })}
            className="bg-white rounded-2xl border border-[#D2DCC7] p-4 shadow-2xs hover:border-[#1B5E20] cursor-pointer transition-all space-y-3"
          >
            <div className="flex items-start justify-between">
              <div>
                <div className="flex items-center gap-1.5">
                  <h3 className="font-extrabold text-base text-[#141A13]">
                    {t(buyer.nameKey)}
                  </h3>
                  {buyer.isVerified && (
                    <ShieldCheck className="w-4 h-4 text-emerald-600 shrink-0" />
                  )}
                </div>
                <div className="flex items-center gap-2 mt-0.5 text-xs text-[#4A5546]">
                  <span className="inline-flex items-center gap-0.5 font-bold text-amber-600">
                    <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                    {buyer.rating}
                  </span>
                  <span>•</span>
                  <span>{buyer.completedTransactionsCount} trades completed</span>
                </div>
              </div>

              <span className="text-xs font-semibold text-[#4A5546]">
                {buyer.distanceKm} km
              </span>
            </div>

            <div className="flex items-center gap-1 text-xs text-[#4A5546]">
              <MapPin className="w-3.5 h-3.5 text-[#1B5E20] shrink-0" />
              <span className="truncate">{buyer.address}</span>
            </div>

            <div className="pt-2 border-t border-[#F1F4EB] flex items-center justify-between">
              <span className="text-[11px] font-bold text-emerald-800 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200">
                ⚡ {t(buyer.paymentTurnaroundKey || 'payment_within_2_days')}
              </span>

              <span className="text-xs font-bold text-[#1B5E20] flex items-center gap-0.5">
                <span>View Profile</span>
                <ArrowRight className="w-3 h-3" />
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
