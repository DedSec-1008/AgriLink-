import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import {
  CheckCircle2,
  Star,
  ArrowRight,
  Download,
  Share2,
  ThumbsUp,
} from 'lucide-react';
import confetti from 'canvas-confetti';

interface SaleCompletedScreenProps {
  transactionId: string;
}

export const SaleCompletedScreen: React.FC<SaleCompletedScreenProps> = ({
  transactionId,
}) => {
  const { t, transactions, submitBuyerRating, navigateTab, showSnack } = useAgri();
  const tx = transactions.find((item) => item.id === transactionId);

  const [rating, setRating] = useState<number>(5);
  const [selectedTag, setSelectedTag] = useState<string>('rate_tag_fast_payment');
  const [reviewText, setReviewText] = useState<string>('');
  const [rated, setRated] = useState<boolean>(false);

  if (!tx) {
    return <div className="p-6 text-center text-sm text-[#4A5546]">Transaction not found.</div>;
  }

  const tags = [
    'rate_tag_fast_payment',
    'rate_tag_fair_grading',
    'rate_tag_polite_staff',
    'rate_tag_accurate_weight',
  ];

  const handleRate = () => {
    submitBuyerRating(tx.id, rating, reviewText || t(selectedTag));
    setRated(true);
    showSnack(t('rating_submitted_snack'));

    try {
      confetti({
        particleCount: 50,
        spread: 60,
        origin: { y: 0.5 },
        colors: ['#1B5E20', '#FFB300', '#4CAF50'],
      });
    } catch {
      // safe fallback
    }
  };

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      {/* Celebration Header */}
      <div className="bg-gradient-to-b from-[#E8F5E9] to-white rounded-3xl border-2 border-[#1B5E20] p-6 text-center shadow-md space-y-4">
        <div className="w-16 h-16 rounded-full bg-[#1B5E20] text-amber-300 flex items-center justify-center mx-auto shadow-md">
          <CheckCircle2 className="w-10 h-10" />
        </div>

        <div>
          <span className="text-[11px] font-extrabold text-[#1B5E20] uppercase tracking-wider block">
            {t('title_sale_completed')}
          </span>
          <h2 className="text-2xl font-black text-[#141A13] mt-1">
            ₹{tx.estimatedNetAmount.toLocaleString()}
          </h2>
          <p className="text-xs text-[#4A5546] mt-0.5">
            {t('msg_sale_completed_payout', t(tx.buyerNameKey))}
          </p>
        </div>

        <div className="p-3 bg-white rounded-xl border border-[#D2DCC7] text-xs text-left space-y-1">
          <div className="flex justify-between">
            <span className="text-[#4A5546]">{t('label_transaction_id')}:</span>
            <span className="font-mono font-bold text-[#141A13]">{tx.id}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-[#4A5546]">{t('review_crop_label')}:</span>
            <span className="font-bold text-[#141A13]">
              {tx.quantityQuintals} {t('unit_quintals')} {t(tx.cropNameKey)}
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-[#4A5546]">{t('payment_status_header')}:</span>
            <span className="font-bold text-emerald-700">
              {t('payment_status_received')} ✓
            </span>
          </div>
        </div>
      </div>

      {/* Buyer Rating Component */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-3">
        <div className="text-center">
          <h3 className="font-extrabold text-sm text-[#141A13]">
            {t('rate_buyer_title', t(tx.buyerNameKey))}
          </h3>
          <p className="text-xs text-[#4A5546] mt-0.5">{t('rate_buyer_subtitle')}</p>
        </div>

        {/* 5-star rating */}
        <div className="flex items-center justify-center gap-2 py-1">
          {[1, 2, 3, 4, 5].map((star) => (
            <button
              key={star}
              disabled={rated}
              onClick={() => setRating(star)}
              className="p-1 hover:scale-110 active:scale-95 transition-transform"
            >
              <Star
                className={`w-8 h-8 ${
                  star <= rating
                    ? 'fill-amber-400 text-amber-400'
                    : 'text-gray-300'
                }`}
              />
            </button>
          ))}
        </div>

        {/* Feedback tags */}
        <div className="flex flex-wrap gap-1.5 justify-center pt-1">
          {tags.map((tagKey) => (
            <button
              key={tagKey}
              disabled={rated}
              onClick={() => setSelectedTag(tagKey)}
              className={`px-3 py-1.5 text-xs rounded-xl font-medium border transition-colors ${
                selectedTag === tagKey
                  ? 'bg-[#E8F5E9] border-[#1B5E20] text-[#1B5E20] font-bold'
                  : 'bg-[#FBFBF6] border-[#D2DCC7] text-[#4A5546]'
              }`}
            >
              {t(tagKey)}
            </button>
          ))}
        </div>

        {!rated ? (
          <button
            id="submit-rating-btn"
            onClick={handleRate}
            className="w-full mt-2 py-3 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-xs active:scale-98 transition-transform"
          >
            {t('btn_submit_rating')}
          </button>
        ) : (
          <div className="p-3 bg-emerald-100 text-emerald-800 text-center font-bold text-xs rounded-xl">
            {t('rating_submitted_snack')} ✓
          </div>
        )}
      </div>

      {/* Done & Back home */}
      <button
        id="back-home-btn"
        onClick={() => navigateTab('home')}
        className="w-full py-3.5 px-4 bg-[#141A13] hover:bg-[#2E382B] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
      >
        <span>{t('btn_back_to_home')}</span>
        <ArrowRight className="w-4 h-4" />
      </button>
    </div>
  );
};
