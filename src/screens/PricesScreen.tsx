import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { initialMarketPrices, cropOptions } from '../data/mockData';
import { TrendingUp, MapPin, ArrowRight } from 'lucide-react';

export const PricesScreen: React.FC = () => {
  const { t, navigateTab } = useAgri();
  const [selectedCropId, setSelectedCropId] = useState<string>('crop_soybean');

  const selectedCrop = cropOptions.find((c) => c.id === selectedCropId) || cropOptions[0];

  // Adjust rates dynamically based on chosen crop
  const prices = initialMarketPrices.map((mp, index) => {
    const base = selectedCrop.typicalPrice;
    const variation = index === 0 ? 0 : index === 1 ? -30 : index === 2 ? -70 : -130;
    return {
      ...mp,
      cropNameKey: selectedCrop.nameKey,
      pricePerQuintal: base + variation,
    };
  });

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight">
          {t('title_prices')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">
          {t('subtitle_prices')}
        </p>
      </div>

      {/* Crop selector chips */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
        {cropOptions.map((crop) => (
          <button
            key={crop.id}
            id={`crop-filter-${crop.id}`}
            onClick={() => setSelectedCropId(crop.id)}
            className={`px-3 py-1.5 rounded-xl text-xs font-bold shrink-0 transition-all flex items-center gap-1.5 ${
              selectedCropId === crop.id
                ? 'bg-[#1B5E20] text-white shadow-xs'
                : 'bg-white border border-[#D2DCC7] text-[#2E382B] hover:bg-emerald-50'
            }`}
          >
            <span>{crop.emoji}</span>
            <span>{t(crop.nameKey)}</span>
          </button>
        ))}
      </div>

      {/* Mandi Rates List */}
      <div className="space-y-3">
        {prices.map((item, index) => (
          <div
            key={item.id}
            id={`mandi-rate-card-${item.id}`}
            className={`bg-white rounded-2xl p-4 border transition-all ${
              index === 0
                ? 'border-2 border-[#1B5E20] shadow-sm'
                : 'border-[#D2DCC7] shadow-2xs'
            }`}
          >
            <div className="flex items-start justify-between">
              <div>
                {index === 0 && (
                  <span className="inline-block px-2 py-0.5 bg-[#E8F5E9] text-[#1B5E20] text-[10px] font-extrabold rounded-md mb-1 uppercase tracking-wide">
                    {t('badge_best_opportunity')}
                  </span>
                )}
                <div className="flex items-center gap-1.5">
                  <MapPin className="w-3.5 h-3.5 text-[#1B5E20]" />
                  <h3 className="font-bold text-base text-[#141A13]">
                    {t(item.marketNameKey)}
                  </h3>
                </div>
                <p className="text-xs text-[#4A5546] mt-0.5">
                  {t(item.cropNameKey)} • {t('produce_quality_good')}
                </p>
              </div>

              <div className="text-right">
                <div className="text-lg font-black text-[#1B5E20]">
                  ₹{item.pricePerQuintal.toLocaleString()}
                </div>
                <span className="text-[10px] text-[#4A5546] font-semibold block">
                  / {t('unit_quintals')}
                </span>
              </div>
            </div>

            <div className="mt-3 pt-2.5 border-t border-[#F1F4EB] flex items-center justify-between">
              <span
                className={`inline-flex items-center gap-1 text-xs font-semibold ${
                  item.isPositiveChange ? 'text-emerald-700' : 'text-[#4A5546]'
                }`}
              >
                <TrendingUp className="w-3.5 h-3.5" />
                {t(item.priceChangeTextKey)}
              </span>

              <button
                onClick={() => navigateTab('sell')}
                className="text-xs font-bold text-[#1B5E20] hover:text-[#2E7D32] flex items-center gap-1 px-2.5 py-1 rounded-lg hover:bg-[#E8F5E9] transition-colors"
              >
                <span>{t('action_sell_produce')}</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Info note */}
      <div className="bg-[#FFF8E1] border border-amber-200 rounded-xl p-3 text-xs text-amber-900 leading-relaxed">
        💡 <strong>टीप / Note:</strong> {t('label_deduction_note')}
      </div>
    </div>
  );
};
