import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import {
  ChevronLeft,
  CheckCircle2,
  Clock,
  Truck,
  ArrowRight,
  ShieldCheck,
  AlertCircle,
  Coins,
  Package,
} from 'lucide-react';
import { GrievanceModal } from '../components/GrievanceModal';

interface TransactionDetailScreenProps {
  transactionId: string;
}

export const TransactionDetailScreen: React.FC<TransactionDetailScreenProps> = ({
  transactionId,
}) => {
  const { t, transactions, navigateTo, navigateTab } = useAgri();
  const [showGrievance, setShowGrievance] = useState(false);

  const tx = transactions.find((item) => item.id === transactionId);

  if (!tx) {
    return (
      <div className="p-6 text-center space-y-3">
        <p className="text-sm text-[#4A5546]">Transaction not found.</p>
        <button
          onClick={() => navigateTab('home')}
          className="px-4 py-2 bg-[#1B5E20] text-white rounded-xl text-xs font-bold"
        >
          {t('btn_back_to_home')}
        </button>
      </div>
    );
  }

  // 6 transaction stages
  const steps = [
    { key: 'OFFER_ACCEPTED', labelKey: 'tx_step_offer_accepted' },
    { key: 'LOGISTICS_BOOKED', labelKey: 'tx_step_arrange_transport' },
    { key: 'DISPATCHED', labelKey: 'tx_step_produce_pickup' },
    { key: 'DELIVERED', labelKey: 'tx_step_delivered' },
    { key: 'PAYMENT_RECEIVED', labelKey: 'tx_step_payment_received' },
    { key: 'COMPLETED', labelKey: 'tx_step_sale_completed' },
  ];

  const currentStepIndex =
    tx.status === 'OFFER_ACCEPTED'
      ? 0
      : tx.status === 'LOGISTICS_BOOKED'
      ? 1
      : tx.status === 'DISPATCHED'
      ? 2
      : tx.status === 'DELIVERED' || tx.status === 'PAYMENT_INITIATED'
      ? 3
      : tx.status === 'PAYMENT_RECEIVED'
      ? 4
      : 5;

  // Next action mapping
  let nextActionLabel = t('action_arrange_transport');
  let handleNextAction = () =>
    navigateTo({ type: 'arrange_transport', transactionId: tx.id });

  if (tx.status === 'LOGISTICS_BOOKED') {
    nextActionLabel = t('action_confirm_pickup');
    handleNextAction = () =>
      navigateTo({ type: 'produce_pickup', transactionId: tx.id });
  } else if (tx.status === 'DISPATCHED') {
    nextActionLabel = t('action_track_delivery');
    handleNextAction = () =>
      navigateTo({ type: 'delivery_tracking', transactionId: tx.id });
  } else if (tx.status === 'DELIVERED' || tx.status === 'PAYMENT_INITIATED' || tx.status === 'PAYMENT_DELAYED') {
    nextActionLabel = t('action_track_payment');
    handleNextAction = () =>
      navigateTo({ type: 'payment_tracking', transactionId: tx.id });
  } else if (tx.status === 'PAYMENT_RECEIVED' || tx.status === 'COMPLETED') {
    nextActionLabel = t('action_view_completed');
    handleNextAction = () =>
      navigateTo({ type: 'sale_completed', transactionId: tx.id });
  }

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
        <div className="flex items-center justify-between">
          <span className="text-[10px] font-extrabold text-[#1B5E20] uppercase tracking-wider">
            {t('title_your_sale')}
          </span>
          <span className="font-mono text-xs font-bold text-[#4A5546]">
            {tx.id}
          </span>
        </div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight mt-0.5">
          {t(tx.buyerNameKey)}
        </h2>
        <p className="text-xs text-[#4A5546]">
          {tx.quantityQuintals} {t('unit_quintals')} {t(tx.cropNameKey)} • ₹{tx.agreedPricePerQ.toLocaleString()} / q
        </p>
      </div>

      {/* Progress Timeline Stepper */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-3">
        <span className="text-xs font-extrabold text-[#4A5546] uppercase tracking-wider block">
          {t('label_what_has_happened')}
        </span>

        <div className="relative pl-6 space-y-4 pt-1">
          {/* Vertical indicator line */}
          <div className="absolute left-2.5 top-2 bottom-2 w-0.5 bg-[#D2DCC7]" />

          {steps.map((s, idx) => {
            const isCompleted = idx < currentStepIndex;
            const isCurrent = idx === currentStepIndex;

            return (
              <div key={s.key} className="relative flex items-center gap-3 text-xs">
                <div
                  className={`absolute -left-6 w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold z-10 transition-colors ${
                    isCompleted
                      ? 'bg-emerald-600 text-white'
                      : isCurrent
                      ? 'bg-[#1B5E20] text-amber-300 ring-4 ring-emerald-100'
                      : 'bg-white border-2 border-[#D2DCC7] text-[#4A5546]'
                  }`}
                >
                  {isCompleted ? '✓' : idx + 1}
                </div>

                <div className="flex-1">
                  <span
                    className={`block ${
                      isCurrent
                        ? 'font-extrabold text-[#1B5E20] text-sm'
                        : isCompleted
                        ? 'font-semibold text-[#141A13]'
                        : 'text-[#4A5546]'
                    }`}
                  >
                    {t(s.labelKey)}
                  </span>
                  {isCurrent && (
                    <span className="text-[10px] text-amber-700 font-bold bg-amber-50 px-2 py-0.5 rounded-sm inline-block mt-0.5">
                      {t('next_action_header')}
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* What to do next CTA Box */}
      <div className="bg-[#E8F5E9] rounded-2xl border border-[#A5D6A7] p-4 space-y-2.5 shadow-xs">
        <div className="flex items-center gap-2">
          <Truck className="w-4 h-4 text-[#1B5E20]" />
          <span className="text-xs font-extrabold text-[#00390B] uppercase tracking-wider">
            {t('label_what_next')}
          </span>
        </div>

        <p className="text-xs text-[#1B5E20] font-medium leading-relaxed">
          {tx.status === 'OFFER_ACCEPTED' && t('subtitle_arrange_transport')}
          {tx.status === 'LOGISTICS_BOOKED' && t('msg_transporter_ready')}
          {tx.status === 'DISPATCHED' && t('expected_delivery_today')}
          {(tx.status === 'DELIVERED' || tx.status === 'PAYMENT_INITIATED') && t('subtitle_payment')}
          {tx.status === 'PAYMENT_DELAYED' && t('payment_delayed_msg')}
          {tx.status === 'COMPLETED' && t('title_sale_completed')}
        </p>

        <button
          id="tx-next-action-btn"
          onClick={handleNextAction}
          className="w-full py-3 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-xs flex items-center justify-center gap-1.5 active:scale-98 transition-transform"
        >
          <span>{nextActionLabel}</span>
          <ArrowRight className="w-4 h-4" />
        </button>
      </div>

      {/* Financial Transparency Summary */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-4 shadow-xs space-y-2 text-xs">
        <span className="font-extrabold text-[#4A5546] uppercase tracking-wider block">
          {t('payment_breakdown_title')}
        </span>

        <div className="space-y-1.5 pt-1">
          <div className="flex justify-between">
            <span className="text-[#4A5546]">
              {t('label_produce_value', tx.quantityQuintals, tx.agreedPricePerQ.toLocaleString())}
            </span>
            <span className="font-bold text-[#141A13]">
              ₹{tx.grossProduceValue.toLocaleString()}
            </span>
          </div>

          <div className="flex justify-between text-red-700">
            <span>− {t('label_deduction_transport')}</span>
            <span className="font-bold">
              − ₹{tx.transportDeduction.toLocaleString()}
            </span>
          </div>

          <div className="flex justify-between text-red-700">
            <span>− {t('label_deduction_other')}</span>
            <span className="font-bold">
              − ₹{tx.otherDeductions.toLocaleString()}
            </span>
          </div>

          <div className="pt-2 border-t border-[#D2DCC7] flex justify-between items-baseline">
            <span className="font-bold text-sm text-[#141A13]">
              {t('label_net_in_pocket')}
            </span>
            <span className="text-lg font-black text-[#1B5E20]">
              ₹{tx.estimatedNetAmount.toLocaleString()}
            </span>
          </div>
        </div>
      </div>

      {/* Problem / Grievance Footer */}
      <div className="flex items-center justify-between p-3 bg-[#EFEBE9] rounded-xl border border-[#D7CCC8]">
        <div className="flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-[#5D4037]" />
          <span className="text-xs font-bold text-[#5D4037]">
            {t('grievance_prompt')}
          </span>
        </div>
        <button
          onClick={() => setShowGrievance(true)}
          className="px-3 py-1 bg-[#5D4037] hover:bg-[#4E342E] text-white text-xs font-bold rounded-lg transition-colors"
        >
          {t('btn_get_help')}
        </button>
      </div>

      <GrievanceModal
        isOpen={showGrievance}
        onClose={() => setShowGrievance(false)}
        transactionId={tx.id}
      />
    </div>
  );
};
