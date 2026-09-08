import React from 'react';
import { useAgri } from '../context/AgriContext';
import { Layers, ArrowRight, PlusCircle, CheckCircle2, Clock } from 'lucide-react';

export const MyLotsScreen: React.FC = () => {
  const { t, lots, offers, navigateTo, navigateTab } = useAgri();

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-black text-[#141A13] tracking-tight">
            {t('title_my_lots')}
          </h2>
          <p className="text-xs text-[#4A5546] mt-0.5">{t('subtitle_my_lots')}</p>
        </div>
        <button
          onClick={() => navigateTab('sell')}
          className="px-3 py-1.5 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-xs flex items-center gap-1 shrink-0"
        >
          <PlusCircle className="w-3.5 h-3.5" />
          <span>{t('btn_new_lot')}</span>
        </button>
      </div>

      {lots.length === 0 ? (
        <div className="bg-white rounded-2xl border border-[#D2DCC7] p-8 text-center space-y-4 shadow-2xs">
          <div className="w-14 h-14 rounded-full bg-[#E8F5E9] text-[#1B5E20] flex items-center justify-center mx-auto">
            <Layers className="w-7 h-7" />
          </div>
          <div>
            <h3 className="text-base font-bold text-[#141A13]">
              {t('empty_lots_title')}
            </h3>
            <p className="text-xs text-[#4A5546] mt-1 max-w-xs mx-auto">
              {t('empty_lots_desc')}
            </p>
          </div>
          <button
            onClick={() => navigateTab('sell')}
            className="py-2.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-md inline-flex items-center gap-1.5"
          >
            <PlusCircle className="w-4 h-4" />
            <span>{t('btn_create_first_lot')}</span>
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {lots.map((lot) => {
            const lotOffers = offers.filter((o) => o.lotId === lot.lotId);
            const isAccepted = lot.statusKey === 'status_offer_accepted';

            return (
              <div
                key={lot.lotId}
                id={`lot-card-${lot.lotId}`}
                className="bg-white rounded-2xl border border-[#D2DCC7] p-4 shadow-2xs hover:border-[#1B5E20] transition-all"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-11 h-11 rounded-xl bg-[#E8F5E9] flex items-center justify-center text-xl border border-[#C8E6C9]">
                      {lot.iconEmoji}
                    </div>
                    <div>
                      <div className="flex items-center gap-1.5">
                        <span className="font-mono text-xs font-bold text-[#4A5546]">
                          #{lot.lotId}
                        </span>
                        <span className="text-xs text-[#D2DCC7]">•</span>
                        <h3 className="font-bold text-base text-[#141A13]">
                          {t(lot.cropNameKey)}
                        </h3>
                      </div>
                      <p className="text-xs text-[#4A5546] mt-0.5">
                        {lot.quantityQuintals} {t('unit_quintals')} • {t(lot.qualityKey)}
                      </p>
                    </div>
                  </div>

                  <span
                    className={`px-2 py-0.5 text-[10px] font-extrabold rounded-md flex items-center gap-1 ${
                      isAccepted
                        ? 'bg-emerald-100 text-emerald-800'
                        : 'bg-amber-100 text-amber-800'
                    }`}
                  >
                    {isAccepted ? <CheckCircle2 className="w-3 h-3" /> : <Clock className="w-3 h-3" />}
                    {t(lot.statusKey)}
                  </span>
                </div>

                {/* Pricing summary */}
                <div className="mt-3 p-2.5 rounded-xl bg-[#F9FAF6] border border-[#E0E5D7] flex items-baseline justify-between text-xs">
                  <div>
                    <span className="text-[10px] text-[#4A5546] block font-semibold">
                      {t('label_expected_price')}
                    </span>
                    <span className="font-bold text-[#141A13]">
                      ₹{lot.expectedPricePerQ.toLocaleString()} / q
                    </span>
                  </div>

                  <div className="text-right">
                    <span className="text-[10px] text-[#4A5546] block font-semibold">
                      {t('label_estimated_after_expenses')}
                    </span>
                    <span className="font-bold text-[#1B5E20]">
                      ₹{lot.estimatedNetPerQ.toLocaleString()} / q
                    </span>
                  </div>
                </div>

                {/* Bottom actions & offers counter */}
                <div className="mt-3 pt-2.5 border-t border-[#F1F4EB] flex items-center justify-between">
                  <div className="text-xs font-semibold text-[#1B5E20]">
                    {lotOffers.length > 0 ? (
                      <span className="font-bold text-amber-700 bg-amber-50 px-2 py-0.5 rounded-md border border-amber-200">
                        {t('offers_received_count', lotOffers.length)}
                      </span>
                    ) : (
                      <span className="text-[#4A5546]">{t('status_waiting_offers')}</span>
                    )}
                  </div>

                  <div className="flex items-center gap-2">
                    {lotOffers.length > 0 && !isAccepted && (
                      <button
                        onClick={() => navigateTo({ type: 'offers', lotId: lot.lotId })}
                        className="px-3 py-1.5 bg-amber-400 hover:bg-amber-300 text-[#141A13] font-bold text-xs rounded-xl shadow-2xs"
                      >
                        {t('btn_view_offers')}
                      </button>
                    )}
                    <button
                      onClick={() => navigateTo({ type: 'lot_details', lotId: lot.lotId })}
                      className="px-2.5 py-1.5 text-xs font-bold text-[#1B5E20] hover:bg-[#E8F5E9] rounded-xl transition-colors flex items-center gap-1"
                    >
                      <span>{t('btn_view_lot')}</span>
                      <ArrowRight className="w-3 h-3" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
