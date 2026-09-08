import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import {
  ChevronLeft,
  Coins,
  CheckCircle2,
  Clock,
  ArrowRight,
  AlertCircle,
  Building,
  FileCheck,
} from 'lucide-react';
import { GrievanceModal } from '../components/GrievanceModal';

interface PaymentTrackingScreenProps {
  transactionId: string;
}

export const PaymentTrackingScreen: React.FC<PaymentTrackingScreenProps> = ({
  transactionId,
}) => {
  const { t, transactions, confirmPayment, navigateTo } = useAgri();
  const [showGrievance, setShowGrievance] = useState(false);

  const tx = transactions.find((item) => item.id === transactionId);

  if (!tx) {
    return <div className="p-6 text-center text-sm text-[#4A5546]">Transaction not found.</div>;
  }

  const isPaid = tx.status === 'PAYMENT_RECEIVED' || tx.status === 'COMPLETED';

  const handleConfirm = () => {
    confirmPayment(transactionId);
    navigateTo({ type: 'sale_completed', transactionId });
  };

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <button
        onClick={() => navigateTo({ type: 'transaction_detail', transactionId })}
        className="flex items-center gap-1 text-xs font-bold text-[#1B5E20] hover:underline"
      >
        <ChevronLeft className="w-4 h-4" />
        <span>{t('title_transaction_detail')}</span>
      </button>

      <div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight">
          {t('title_payment')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">{t('subtitle_payment')}</p>
      </div>

      {/* Payment Status Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Coins className="w-5 h-5 text-[#1B5E20]" />
            <span className="font-extrabold text-sm text-[#141A13]">
              {isPaid ? t('payment_received_badge') : t('payment_processing_badge')}
            </span>
          </div>
          <span
            className={`px-2.5 py-0.5 text-xs font-extrabold rounded-md flex items-center gap-1 ${
              isPaid
                ? 'bg-emerald-100 text-emerald-800'
                : 'bg-amber-100 text-amber-800'
            }`}
          >
            {isPaid ? <CheckCircle2 className="w-3.5 h-3.5" /> : <Clock className="w-3.5 h-3.5" />}
            {isPaid ? t('payment_status_received') : t('payment_status_pending')}
          </span>
        </div>

        {/* Amount Hero */}
        <div className="p-4 rounded-xl bg-[#E8F5E9] border border-[#A5D6A7] text-center space-y-1">
          <span className="text-xs font-extrabold text-[#00390B] uppercase tracking-wider block">
            {t('payment_net_received')}
          </span>
          <div className="text-3xl font-black text-[#1B5E20]">
            ₹{tx.estimatedNetAmount.toLocaleString()}
          </div>
          <span className="text-[11px] text-[#2E7D32] font-semibold block">
            Direct Bank Transfer (IMPS / NEFT)
          </span>
        </div>

        {/* Account Details */}
        <div className="p-3.5 bg-[#F9FAF6] rounded-xl border border-[#E0E5D7] space-y-2 text-xs">
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">{t('payment_account_credited')}:</span>
            <span className="font-bold text-[#141A13]">State Bank of India (••• 4289)</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">{t('payment_utr_label')}:</span>
            <span className="font-mono font-bold text-[#141A13]">SBI93821094821</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-[#4A5546]">{t('payment_transfer_time')}:</span>
            <span className="font-semibold text-[#141A13]">Today, 02:45 PM</span>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="space-y-2 pt-1">
        {!isPaid ? (
          <button
            id="confirm-payment-btn"
            onClick={handleConfirm}
            className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
          >
            <CheckCircle2 className="w-5 h-5" />
            <span>{t('btn_confirm_payment_received')}</span>
          </button>
        ) : (
          <button
            onClick={() => navigateTo({ type: 'sale_completed', transactionId })}
            className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
          >
            <span>{t('action_view_completed')}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        )}

        <button
          onClick={() => setShowGrievance(true)}
          className="w-full py-2.5 px-4 bg-white border border-[#D2DCC7] text-[#5D4037] hover:text-[#3E2723] font-bold text-xs rounded-xl flex items-center justify-center gap-1.5 hover:bg-[#EFEBE9]"
        >
          <AlertCircle className="w-3.5 h-3.5" />
          <span>{t('btn_report_payment_delay')}</span>
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
