import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { ChevronLeft, Star, ShieldCheck, Check, Phone, MessageSquare } from 'lucide-react';

interface OfferDetailScreenProps {
  offerId: string;
}

export const OfferDetailScreen: React.FC<OfferDetailScreenProps> = ({ offerId }) => {
  const { t, offers, navigateTo, showSnack } = useAgri();
  const [showContacted, setShowContacted] = useState(false);

  const offer = offers.find((o) => o.id === offerId);

  if (!offer) {
    return (
      <div className="p-6 text-center text-sm text-[#4A5546]">Offer not found.</div>
    );
  }

  const netPerQ = Math.round(offer.estimatedNetAmount / offer.quantityQuintals);
  const isPending = offer.status === 'PENDING';

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <button
        onClick={() => navigateTo({ type: 'offers', lotId: offer.lotId })}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('title_offers', '')}</span>
      </button>

      <div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight">
          {t('title_offer_details')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">
          {t('offer_from_buyer', t(offer.buyerNameKey))}
        </p>
      </div>

      {/* Buyer & Rate Overview */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
        <div className="flex items-start justify-between">
          <div>
            <h3 className="text-lg font-black text-[#141A13]">
              {t(offer.buyerNameKey)}
            </h3>
            <div className="flex items-center gap-2 mt-1 text-xs text-[#4A5546]">
              {offer.isVerifiedBuyer && (
                <span className="inline-flex items-center gap-1 text-emerald-700 font-bold">
                  <ShieldCheck className="w-3.5 h-3.5" />
                  {t('verified_buyer_tag')}
                </span>
              )}
              <span className="inline-flex items-center gap-0.5 font-bold text-amber-600">
                <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                {offer.farmerRating} / 5.0
              </span>
            </div>
          </div>

          <span className="px-2.5 py-1 text-xs font-extrabold rounded-md bg-amber-100 text-amber-800">
            {t(
              offer.status === 'PENDING'
                ? 'offer_status_pending'
                : offer.status === 'ACCEPTED'
                ? 'offer_status_accepted'
                : 'offer_status_rejected'
            )}
          </span>
        </div>

        {/* Calculation Table */}
        <div className="p-3.5 rounded-xl bg-[#F9FAF6] border border-[#E0E5D7] space-y-2 text-xs">
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">
              {t('label_quoted_price')} ({offer.quantityQuintals} {t('unit_quintals')} × ₹{offer.pricePerQuintal.toLocaleString()})
            </span>
            <span className="font-bold text-[#141A13]">
              ₹{offer.quotedTotalAmount.toLocaleString()}
            </span>
          </div>

          <div className="flex items-center justify-between text-red-700">
            <span>− {t('detail_transport')}</span>
            <span className="font-bold">
              − ₹{(offer.transportExpensePerQ * offer.quantityQuintals).toLocaleString()}
            </span>
          </div>

          <div className="flex items-center justify-between text-red-700">
            <span>− {t('detail_handling')}</span>
            <span className="font-bold">
              − ₹{(offer.otherExpensePerQ * offer.quantityQuintals).toLocaleString()}
            </span>
          </div>

          <div className="pt-2 border-t border-[#D2DCC7] flex items-baseline justify-between">
            <span className="font-bold text-sm text-[#141A13]">
              {t('label_after_expenses')}
            </span>
            <div className="text-right">
              <span className="text-xl font-black text-[#1B5E20] block">
                ₹{offer.estimatedNetAmount.toLocaleString()}
              </span>
              <span className="text-[11px] text-[#4A5546]">
                ₹{netPerQ.toLocaleString()} / q
              </span>
            </div>
          </div>
        </div>

        {/* Terms */}
        <div className="space-y-3 pt-2 text-xs">
          <div>
            <span className="font-bold text-[#4A5546] uppercase tracking-wider block mb-1">
              {t('section_payment')}
            </span>
            <p className="text-[#141A13] bg-[#F1F4EB] p-2.5 rounded-xl border border-[#D2DCC7]">
              {t(offer.paymentTermsKey)}
            </p>
          </div>

          <div>
            <span className="font-bold text-[#4A5546] uppercase tracking-wider block mb-1">
              {t('label_delivery_requirements')}
            </span>
            <p className="text-[#141A13] bg-[#F1F4EB] p-2.5 rounded-xl border border-[#D2DCC7]">
              {t(offer.deliveryRequirementsKey)}
            </p>
          </div>

          <div>
            <span className="font-bold text-[#4A5546] uppercase tracking-wider block mb-1">
              {t('label_quality_requirements')}
            </span>
            <p className="text-[#141A13] bg-[#F1F4EB] p-2.5 rounded-xl border border-[#D2DCC7]">
              {t(offer.qualityRequirementsKey)}
            </p>
          </div>
        </div>
      </div>

      {/* Action buttons */}
      <div className="space-y-2 pt-1">
        {isPending ? (
          <button
            onClick={() =>
              navigateTo({ type: 'accept_offer_confirm', offerId: offer.id })
            }
            className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
          >
            <Check className="w-5 h-5 stroke-[3]" />
            <span>{t('btn_accept_offer')}</span>
          </button>
        ) : (
          <div className="p-3 bg-emerald-100 text-emerald-800 text-center font-bold text-sm rounded-xl">
            {t('offer_status_accepted')} ✓
          </div>
        )}

        <button
          onClick={() => {
            setShowContacted(true);
            showSnack(`Connected to ${t(offer.buyerNameKey)} Mandi Desk`);
          }}
          className="w-full py-2.5 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] hover:text-[#141A13] font-bold text-xs rounded-xl flex items-center justify-center gap-1.5 hover:bg-[#F1F4EB]"
        >
          <Phone className="w-3.5 h-3.5" />
          <span>{t('btn_contact_buyer')}</span>
        </button>

        {showContacted && (
          <div className="p-3 bg-[#E8F5E9] border border-[#A5D6A7] rounded-xl text-xs text-[#1B5E20] text-center font-semibold">
            📞 Direct Desk: +91 98230 XXXXX (Active during trading hours 8 AM – 6 PM)
          </div>
        )}
      </div>
    </div>
  );
};
