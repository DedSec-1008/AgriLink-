import React from 'react';
import { useAgri } from '../context/AgriContext';
import { ChevronLeft, Star, ShieldCheck, ArrowRight, Check, X } from 'lucide-react';

interface OffersScreenProps {
  lotId: string;
}

export const OffersScreen: React.FC<OffersScreenProps> = ({ lotId }) => {
  const { t, offers, lots, navigateTo } = useAgri();

  const lot = lots.find((l) => l.lotId === lotId);
  const lotOffers = offers.filter((o) => o.lotId === lotId);

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <button
        onClick={() => navigateTo({ type: 'lot_details', lotId })}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('title_lot_details')}</span>
      </button>

      <div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight">
          {t('title_offers', lot ? t(lot.cropNameKey) : 'Produce')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">{t('subtitle_offers')}</p>
      </div>

      {lotOffers.length === 0 ? (
        <div className="bg-white rounded-2xl border border-[#D2DCC7] p-8 text-center space-y-2">
          <h3 className="font-bold text-sm text-[#141A13]">{t('empty_offers_title')}</h3>
          <p className="text-xs text-[#4A5546]">{t('empty_offers_desc')}</p>
        </div>
      ) : (
        <div className="space-y-3.5">
          {lotOffers.map((offer) => {
            const netPerQ = Math.round(offer.estimatedNetAmount / offer.quantityQuintals);
            const isAccepted = offer.status === 'ACCEPTED';
            const isPending = offer.status === 'PENDING';

            return (
              <div
                key={offer.id}
                id={`offer-card-${offer.id}`}
                className={`bg-white rounded-2xl border p-4 transition-all ${
                  isAccepted
                    ? 'border-2 border-emerald-600 bg-emerald-50/20'
                    : 'border-[#D2DCC7] shadow-2xs hover:border-[#1B5E20]'
                }`}
              >
                {/* Header */}
                <div className="flex items-center justify-between">
                  <span className="text-[10px] font-extrabold text-[#4A5546] uppercase tracking-wider">
                    {t('offer_from_buyer', t(offer.buyerNameKey))}
                  </span>
                  <span className="text-[10px] font-semibold text-amber-700 bg-amber-50 px-2 py-0.5 rounded-full border border-amber-200">
                    {t('offer_valid_24h')}
                  </span>
                </div>

                <div className="mt-2 flex items-start justify-between">
                  <div>
                    <h3 className="font-bold text-base text-[#141A13]">
                      {t(offer.buyerNameKey)}
                    </h3>
                    <div className="flex items-center gap-2 mt-0.5 text-xs text-[#4A5546]">
                      {offer.isVerifiedBuyer && (
                        <span className="inline-flex items-center gap-1 text-emerald-700 font-bold">
                          <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
                          {t('verified_buyer_tag')}
                        </span>
                      )}
                      <span className="inline-flex items-center gap-0.5 font-bold text-amber-600">
                        <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
                        {offer.farmerRating}
                      </span>
                    </div>
                  </div>

                  <div className="text-right">
                    <span className="text-[10px] text-[#4A5546] block font-medium">
                      {t('label_quoted_price')}
                    </span>
                    <span className="text-sm font-bold text-[#141A13]">
                      ₹{offer.pricePerQuintal.toLocaleString()} / q
                    </span>
                  </div>
                </div>

                {/* Net in pocket hero */}
                <div className="mt-3 p-3 rounded-xl bg-[#E8F5E9] border border-[#A5D6A7] flex items-baseline justify-between">
                  <div>
                    <span className="text-[10px] font-extrabold text-[#00390B] tracking-wider uppercase block">
                      {t('label_net_realization_hero')}
                    </span>
                    <span className="text-xl font-black text-[#1B5E20]">
                      ₹{netPerQ.toLocaleString()} / q
                    </span>
                  </div>

                  <div className="text-right">
                    <span className="text-[10px] text-[#4A5546] font-semibold block">
                      {t('label_estimated_total')}
                    </span>
                    <span className="text-sm font-black text-[#1B5E20]">
                      ₹{offer.estimatedNetAmount.toLocaleString()}
                    </span>
                  </div>
                </div>

                {/* Actions */}
                <div className="mt-3 pt-2.5 border-t border-[#F1F4EB] flex items-center gap-2">
                  <button
                    onClick={() => navigateTo({ type: 'offer_details', offerId: offer.id })}
                    className="py-2.5 px-3 text-xs font-bold text-[#1B5E20] hover:bg-[#E8F5E9] rounded-xl transition-colors"
                  >
                    {t('btn_view_details')}
                  </button>

                  {isPending && (
                    <button
                      id={`accept-offer-btn-${offer.id}`}
                      onClick={() =>
                        navigateTo({ type: 'accept_offer_confirm', offerId: offer.id })
                      }
                      className="flex-1 py-2.5 px-3 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-xs flex items-center justify-center gap-1 active:scale-98 transition-transform"
                    >
                      <Check className="w-4 h-4 stroke-[3]" />
                      <span>{t('btn_accept_offer')}</span>
                    </button>
                  )}

                  {isAccepted && (
                    <span className="flex-1 py-2 text-center text-xs font-bold text-emerald-800 bg-emerald-100 rounded-xl">
                      {t('offer_status_accepted')} ✓
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
