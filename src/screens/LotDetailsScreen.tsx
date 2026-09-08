import React from 'react';
import { useAgri } from '../context/AgriContext';
import { ChevronLeft, ArrowRight, CheckCircle2, Clock, Tag } from 'lucide-react';

interface LotDetailsScreenProps {
  lotId: string;
}

export const LotDetailsScreen: React.FC<LotDetailsScreenProps> = ({ lotId }) => {
  const { t, lots, offers, transactions, navigateTo, navigateTab } = useAgri();

  const lot = lots.find((l) => l.lotId === lotId);
  const lotOffers = offers.filter((o) => o.lotId === lotId);
  const relatedTx = transactions.find((tx) => tx.lotId === lotId);

  if (!lot) {
    return (
      <div className="p-6 text-center space-y-3">
        <p className="text-sm text-[#4A5546]">Lot not found.</p>
        <button
          onClick={() => navigateTab('my_lots')}
          className="px-4 py-2 bg-[#1B5E20] text-white rounded-xl text-xs font-bold"
        >
          {t('btn_back_to_my_lots')}
        </button>
      </div>
    );
  }

  const isAccepted = lot.statusKey === 'status_offer_accepted';

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      {/* Top back navigation */}
      <button
        onClick={() => navigateTab('my_lots')}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('btn_back_to_my_lots')}</span>
      </button>

      {/* Lot Status Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-xl bg-[#E8F5E9] flex items-center justify-center text-2xl border border-[#C8E6C9]">
              {lot.iconEmoji}
            </div>
            <div>
              <span className="font-mono text-xs font-bold text-[#4A5546]">
                #{lot.lotId}
              </span>
              <h2 className="text-lg font-black text-[#141A13] leading-tight">
                {t(lot.cropNameKey)}
              </h2>
              <p className="text-xs text-[#4A5546] mt-0.5">
                {lot.quantityQuintals} {t('unit_quintals')} • {t(lot.qualityKey)}
              </p>
            </div>
          </div>

          <span
            className={`px-2.5 py-1 text-xs font-extrabold rounded-md flex items-center gap-1 ${
              isAccepted
                ? 'bg-emerald-100 text-emerald-800'
                : 'bg-amber-100 text-amber-800'
            }`}
          >
            {isAccepted ? <CheckCircle2 className="w-3.5 h-3.5" /> : <Clock className="w-3.5 h-3.5" />}
            {t(lot.statusKey)}
          </span>
        </div>

        {/* Specifications Table */}
        <div className="space-y-2.5 pt-3 border-t border-[#F1F4EB] text-xs">
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">{t('review_location_label')}</span>
            <span className="font-bold text-[#141A13]">{t(lot.location)}</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">{t('review_timing_label')}</span>
            <span className="font-bold text-[#141A13]">{t(lot.readyTiming)}</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">{t('label_expected_price')}</span>
            <span className="font-bold text-[#141A13]">₹{lot.expectedPricePerQ.toLocaleString()} / q</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">{t('label_estimated_after_expenses')}</span>
            <span className="font-bold text-[#1B5E20]">₹{lot.estimatedNetPerQ.toLocaleString()} / q</span>
          </div>
        </div>
      </div>

      {/* Offers Section */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Tag className="w-4 h-4 text-[#1B5E20]" />
            <h3 className="font-bold text-sm text-[#141A13] uppercase tracking-wider">
              {t('section_offers')}
            </h3>
          </div>
          <span className="text-xs font-bold text-emerald-800 bg-emerald-100 px-2 py-0.5 rounded-full">
            {lotOffers.length} {t('section_offers')}
          </span>
        </div>

        {lotOffers.length > 0 ? (
          <div className="space-y-2.5 pt-1">
            {lotOffers.map((offer) => (
              <div
                key={offer.id}
                className="p-3 rounded-xl border border-[#D2DCC7] bg-[#FBFBF6] flex items-center justify-between"
              >
                <div>
                  <div className="flex items-center gap-1.5">
                    <span className="font-bold text-sm text-[#141A13]">
                      {t(offer.buyerNameKey)}
                    </span>
                    {offer.isVerifiedBuyer && (
                      <span className="text-[10px] font-bold text-emerald-700 bg-emerald-100 px-1.5 rounded-sm">
                        ✓
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-[#4A5546] mt-0.5">
                    ₹{offer.pricePerQuintal.toLocaleString()} / q • {t('label_after_expenses_short')}:{' '}
                    <strong className="text-[#1B5E20]">
                      ₹{(offer.estimatedNetAmount / offer.quantityQuintals).toLocaleString()}
                    </strong>
                  </p>
                </div>

                <button
                  onClick={() => navigateTo({ type: 'offer_details', offerId: offer.id })}
                  className="px-3 py-1.5 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-xs"
                >
                  {t('btn_view_details')}
                </button>
              </div>
            ))}
          </div>
        ) : (
          <div className="p-4 bg-[#F9FAF6] rounded-xl text-center text-xs text-[#4A5546]">
            {t('empty_offers_desc')}
          </div>
        )}

        {lotOffers.length > 0 && !isAccepted && (
          <button
            onClick={() => navigateTo({ type: 'offers', lotId: lot.lotId })}
            className="w-full mt-2 py-3 bg-amber-400 hover:bg-amber-300 text-[#141A13] font-bold text-xs rounded-xl shadow-xs flex items-center justify-center gap-1.5 active:scale-98 transition-transform"
          >
            <span>{t('btn_view_offers')}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        )}
      </div>

      {/* Linked active transaction card if offer was accepted */}
      {relatedTx && (
        <div className="bg-[#E8F5E9] rounded-2xl border border-[#A5D6A7] p-4 flex items-center justify-between">
          <div>
            <span className="text-[10px] font-extrabold text-[#00390B] uppercase tracking-wider block">
              {t('home_active_sale_title')}
            </span>
            <h4 className="font-bold text-sm text-[#141A13] mt-0.5">
              {relatedTx.id} • {t('tx_status_' + relatedTx.status.toLowerCase())}
            </h4>
          </div>
          <button
            onClick={() =>
              navigateTo({
                type: 'transaction_detail',
                transactionId: relatedTx.id,
              })
            }
            className="px-3 py-1.5 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-xs flex items-center gap-1"
          >
            <span>{t('btn_view_sale')}</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>
      )}
    </div>
  );
};
