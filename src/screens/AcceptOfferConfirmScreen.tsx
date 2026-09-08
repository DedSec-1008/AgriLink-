import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { ChevronLeft, CheckCircle2, ArrowRight, Truck, ShieldCheck, Check } from 'lucide-react';
import confetti from 'canvas-confetti';
import { AgriTransaction } from '../types';

interface AcceptOfferConfirmScreenProps {
  offerId: string;
}

export const AcceptOfferConfirmScreen: React.FC<AcceptOfferConfirmScreenProps> = ({
  offerId,
}) => {
  const { t, offers, acceptOffer, navigateTo, navigateTab } = useAgri();
  const [createdTx, setCreatedTx] = useState<AgriTransaction | null>(null);

  const offer = offers.find((o) => o.id === offerId);

  if (!offer) {
    return <div className="p-6 text-center text-sm text-[#4A5546]">Offer not found.</div>;
  }

  const handleConfirm = () => {
    const tx = acceptOffer(offerId);
    setCreatedTx(tx);

    try {
      confetti({
        particleCount: 50,
        spread: 50,
        origin: { y: 0.6 },
        colors: ['#1B5E20', '#A5D6A7', '#FFB300'],
      });
    } catch {
      // safe fallback
    }
  };

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <button
        onClick={() => navigateTo({ type: 'offers', lotId: offer.lotId })}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('btn_back')}</span>
      </button>

      {/* If already accepted */}
      {createdTx ? (
        <div className="bg-white rounded-2xl border-2 border-emerald-600 p-6 shadow-md text-center space-y-5 animate-in zoom-in-95">
          <div className="w-16 h-16 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center mx-auto">
            <CheckCircle2 className="w-10 h-10" />
          </div>

          <div>
            <h2 className="text-xl font-black text-[#141A13]">
              {t('tx_accepted_header')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-1">
              {t('tx_accepted_msg', t(offer.buyerNameKey))}
            </p>
          </div>

          <div className="p-3.5 bg-[#F1F4EB] rounded-xl text-left border border-[#D2DCC7] space-y-1 text-xs">
            <div className="flex justify-between">
              <span className="text-[#4A5546]">{t('label_transaction_id')}:</span>
              <span className="font-mono font-bold text-[#141A13]">{createdTx.id}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-[#4A5546]">{t('label_agreed_price')}:</span>
              <span className="font-bold text-[#141A13]">₹{createdTx.agreedPricePerQ.toLocaleString()} / q</span>
            </div>
            <div className="flex justify-between">
              <span className="text-[#4A5546]">{t('label_net_in_pocket')}:</span>
              <span className="font-black text-[#1B5E20]">₹{createdTx.estimatedNetAmount.toLocaleString()}</span>
            </div>
          </div>

          <div className="p-3 bg-[#E8F5E9] rounded-xl border border-[#A5D6A7] text-xs text-[#1B5E20] font-bold flex items-center justify-center gap-2">
            <Truck className="w-4 h-4" />
            <span>{t('next_step_label')} {t('next_step_arrange_transport')}</span>
          </div>

          <div className="space-y-2 pt-2">
            <button
              id="arrange-transport-btn"
              onClick={() =>
                navigateTo({
                  type: 'arrange_transport',
                  transactionId: createdTx.id,
                })
              }
              className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <span>{t('btn_arrange_transport')}</span>
              <ArrowRight className="w-4 h-4" />
            </button>

            <button
              onClick={() => navigateTab('my_lots')}
              className="w-full py-2.5 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-xs rounded-xl hover:bg-[#F1F4EB]"
            >
              {t('btn_back_to_my_lots')}
            </button>
          </div>
        </div>
      ) : (
        /* Confirmation prompt */
        <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('title_accept_confirm')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">
              {t('accept_confirm_sub', t(offer.buyerNameKey))}
            </p>
          </div>

          <div className="p-4 bg-[#F9FAF6] rounded-xl border border-[#E0E5D7] space-y-3 text-xs">
            <div className="flex items-center justify-between pb-2 border-b border-[#E0E5D7]">
              <div>
                <span className="font-extrabold text-sm text-[#141A13]">
                  {t(offer.buyerNameKey)}
                </span>
                <p className="text-[#4A5546]">{offer.quantityQuintals} {t('unit_quintals')}</p>
              </div>
              <span className="inline-flex items-center gap-1 text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md font-bold">
                <ShieldCheck className="w-3.5 h-3.5" />
                {t('verified_buyer_tag')}
              </span>
            </div>

            <div className="flex items-center justify-between">
              <span className="text-[#4A5546]">{t('label_agreed_price')}</span>
              <span className="font-bold text-[#141A13]">₹{offer.pricePerQuintal.toLocaleString()} / q</span>
            </div>

            <div className="flex items-center justify-between">
              <span className="text-[#4A5546]">{t('label_after_expenses')}</span>
              <span className="text-base font-black text-[#1B5E20]">
                ₹{offer.estimatedNetAmount.toLocaleString()}
              </span>
            </div>
          </div>

          <div className="p-3 bg-[#E8F5E9] rounded-xl border border-[#A5D6A7] text-xs text-[#1B5E20] leading-relaxed">
            🌿 <strong>{t('next_step_label')}</strong> {t('subtitle_arrange_transport')}
          </div>

          <div className="pt-2 flex items-center gap-2.5">
            <button
              onClick={() => navigateTo({ type: 'offers', lotId: offer.lotId })}
              className="py-3 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              {t('btn_go_back')}
            </button>
            <button
              id="confirm-accept-offer-btn"
              onClick={handleConfirm}
              className="flex-1 py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <Check className="w-4 h-4 stroke-[3]" />
              <span>{t('btn_confirm_accept')}</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
