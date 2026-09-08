import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { AlertCircle, X, CheckCircle, Send } from 'lucide-react';

interface GrievanceModalProps {
  isOpen: boolean;
  onClose: () => void;
  transactionId?: string;
}

export const GrievanceModal: React.FC<GrievanceModalProps> = ({ isOpen, onClose, transactionId }) => {
  const { t, submitGrievance } = useAgri();
  const [selectedType, setSelectedType] = useState<string>('grievance_type_payment');
  const [details, setDetails] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);

  if (!isOpen) return null;

  const issueTypes = [
    'grievance_type_payment',
    'grievance_type_buyer',
    'grievance_type_delivery',
    'grievance_type_transport',
    'grievance_type_quality',
    'grievance_type_other',
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    submitGrievance(transactionId || 'general', selectedType, details);
    setIsSubmitted(true);
    setTimeout(() => {
      setIsSubmitted(false);
      setDetails('');
      onClose();
    }, 2000);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-in fade-in">
      <div
        className="bg-white w-full max-w-md rounded-2xl shadow-2xl border border-[#D2DCC7] overflow-hidden"
        id="grievance-dialog"
      >
        {/* Header */}
        <div className="bg-[#5D4037] text-white px-5 py-3.5 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <AlertCircle className="w-5 h-5 text-amber-300" />
            <h2 className="font-bold text-base">{t('grievance_dialog_title')}</h2>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-full hover:bg-white/10 text-white/80 hover:text-white"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        {isSubmitted ? (
          <div className="p-8 text-center space-y-3">
            <div className="w-14 h-14 bg-emerald-100 rounded-full flex items-center justify-center mx-auto text-emerald-600">
              <CheckCircle className="w-8 h-8" />
            </div>
            <h3 className="text-lg font-bold text-[#141A13]">
              {t('grievance_submitted_snack')}
            </h3>
            <p className="text-xs text-[#4A5546]">
              {t('helpline_label')}: <span className="font-bold">1800-180-1551</span>
            </p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="p-5 space-y-4">
            <div>
              <label className="block text-xs font-bold text-[#4A5546] uppercase tracking-wider mb-2">
                {t('grievance_prompt')}
              </label>
              <div className="grid grid-cols-2 gap-2">
                {issueTypes.map((typeKey) => (
                  <button
                    type="button"
                    key={typeKey}
                    onClick={() => setSelectedType(typeKey)}
                    className={`px-3 py-2 text-xs rounded-xl font-medium border text-left transition-all ${
                      selectedType === typeKey
                        ? 'bg-[#E8F5E9] border-[#1B5E20] text-[#1B5E20] font-bold shadow-xs'
                        : 'bg-[#FBFBF6] border-[#D2DCC7] text-[#2E382B] hover:bg-white'
                    }`}
                  >
                    {t(typeKey)}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-[#4A5546] uppercase tracking-wider mb-1.5">
                {t('rate_feedback_hint')}
              </label>
              <textarea
                value={details}
                onChange={(e) => setDetails(e.target.value)}
                rows={3}
                placeholder="उदा. भावामध्ये तफावत आहे, किंवा पेमेंट यायला उशीर होत आहे..."
                className="w-full text-sm p-3 rounded-xl border border-[#D2DCC7] bg-[#FBFBF6] focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#1B5E20]"
              />
            </div>

            <div className="pt-2 flex items-center gap-3">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 py-2.5 text-sm font-semibold text-[#4A5546] hover:bg-[#F1F4EB] rounded-xl transition-colors"
              >
                {t('btn_go_back')}
              </button>
              <button
                type="submit"
                className="flex-1 py-2.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-1.5 active:scale-98 transition-transform"
              >
                <Send className="w-4 h-4" />
                {t('btn_get_help')}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
