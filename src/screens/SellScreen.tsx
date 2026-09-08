import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import { cropOptions, generateRecommendations } from '../data/mockData';
import { CropOption, SellingOpportunity } from '../types';
import {
  Check,
  ChevronRight,
  ChevronLeft,
  ArrowRight,
  Sparkles,
  MapPin,
  Calendar,
  ShieldCheck,
  CheckCircle2,
  Clock,
  RotateCcw,
  Info,
} from 'lucide-react';
import confetti from 'canvas-confetti';

type SellStep =
  | 'crop'
  | 'quantity'
  | 'quality'
  | 'location'
  | 'timing'
  | 'review'
  | 'analysis'
  | 'recommendations'
  | 'confirm'
  | 'success';

export const SellScreen: React.FC = () => {
  const { t, createLot, navigateTo, navigateTab } = useAgri();

  // Wizard state
  const [currentStep, setCurrentStep] = useState<SellStep>('crop');
  const [selectedCrop, setSelectedCrop] = useState<CropOption>(cropOptions[0]);
  const [quantity, setQuantity] = useState<number>(50);
  const [showManualQty, setShowManualQty] = useState<boolean>(false);
  const [quality, setQuality] = useState<'quality_good' | 'quality_average' | 'quality_poor'>('quality_good');
  const [location, setLocation] = useState<string>('loc_nagpur');
  const [timing, setTiming] = useState<string>('timing_ready_now');

  // Recommendation & Selected Opportunity state
  const [recommendations, setRecommendations] = useState<SellingOpportunity[]>([]);
  const [expandedOppId, setExpandedOppId] = useState<string | null>(null);
  const [selectedOpportunity, setSelectedOpportunity] = useState<SellingOpportunity | null>(null);
  const [createdLotId, setCreatedLotId] = useState<string>('');

  // Location choices
  const locations = [
    { key: 'loc_nagpur', name: 'Nagpur, Maharashtra' },
    { key: 'loc_katol', name: 'Katol, Maharashtra' },
    { key: 'loc_amravati', name: 'Amravati, Maharashtra' },
    { key: 'loc_wardha', name: 'Wardha, Maharashtra' },
    { key: 'loc_hingna', name: 'Hingna, Maharashtra' },
  ];

  // Timing choices
  const timingOptions = [
    { key: 'timing_ready_now', titleKey: 'timing_ready_now', descKey: 'timing_ready_now_desc', emoji: '🌾' },
    { key: 'timing_few_days', titleKey: 'timing_few_days', descKey: 'timing_few_days_desc', emoji: '⏳' },
    { key: 'timing_choose_date', titleKey: 'timing_choose_date', descKey: 'timing_choose_date_desc', emoji: '📅' },
  ];

  // Step 6 -> 7: Trigger analysis simulation
  const startAnalysis = () => {
    setCurrentStep('analysis');
    const recs = generateRecommendations(selectedCrop, quantity, quality, location, timing);
    setRecommendations(recs);
    setSelectedOpportunity(recs[0]);

    setTimeout(() => {
      setCurrentStep('recommendations');
    }, 1600);
  };

  // Step 9 -> 10: Confirm and create lot
  const handleConfirmSell = () => {
    if (!selectedOpportunity) return;
    const lotId = createLot(
      selectedCrop.nameKey,
      selectedCrop.emoji,
      quantity,
      quality,
      location,
      timing,
      selectedOpportunity.quotedPricePerQ,
      selectedOpportunity.netRealizationPerQ,
      selectedOpportunity.buyerNameKey
    );
    setCreatedLotId(lotId);
    setCurrentStep('success');

    // Trigger celebratory confetti
    try {
      confetti({
        particleCount: 60,
        spread: 60,
        origin: { y: 0.6 },
        colors: ['#1B5E20', '#A5D6A7', '#FFB300'],
      });
    } catch {
      // Ignore if not supported
    }
  };

  const stepNumber =
    currentStep === 'crop'
      ? 1
      : currentStep === 'quantity'
      ? 2
      : currentStep === 'quality'
      ? 3
      : currentStep === 'location'
      ? 4
      : currentStep === 'timing'
      ? 5
      : 5;

  const isFormStep = ['crop', 'quantity', 'quality', 'location', 'timing'].includes(currentStep);

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      {/* Step progress header for form steps */}
      {isFormStep && (
        <div className="bg-white p-3 rounded-2xl border border-[#D2DCC7] shadow-2xs space-y-2">
          <div className="flex items-center justify-between text-xs font-bold text-[#1B5E20]">
            <span>{t('sell_step_progress', stepNumber)}</span>
            <button
              onClick={() => {
                setCurrentStep('crop');
                setQuantity(50);
              }}
              className="text-[#4A5546] hover:text-[#141A13] flex items-center gap-1 font-medium text-[11px]"
            >
              <RotateCcw className="w-3 h-3" />
              <span>{t('btn_reset_flow')}</span>
            </button>
          </div>
          {/* Progress bar */}
          <div className="w-full bg-[#E8F5E9] h-2 rounded-full overflow-hidden">
            <div
              className="bg-[#1B5E20] h-full transition-all duration-300 rounded-full"
              style={{ width: `${(stepNumber / 5) * 100}%` }}
            />
          </div>
        </div>
      )}

      {/* STEP 1: CROP SELECTION */}
      {currentStep === 'crop' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('step1_title')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('step1_subtitle')}</p>
          </div>

          <div className="space-y-2.5">
            {cropOptions.map((crop) => (
              <button
                key={crop.id}
                id={`crop-option-${crop.id}`}
                onClick={() => setSelectedCrop(crop)}
                className={`w-full p-4 rounded-2xl border text-left flex items-center justify-between transition-all ${
                  selectedCrop.id === crop.id
                    ? 'border-2 border-[#1B5E20] bg-[#E8F5E9] shadow-xs'
                    : 'border-[#D2DCC7] bg-white hover:bg-[#F9FAF6]'
                }`}
              >
                <div className="flex items-center gap-3.5">
                  <div className="w-12 h-12 rounded-xl bg-white border border-[#D2DCC7] flex items-center justify-center text-2xl shadow-2xs">
                    {crop.emoji}
                  </div>
                  <div>
                    <h3 className="font-bold text-base text-[#141A13]">
                      {t(crop.nameKey)}
                    </h3>
                    <p className="text-xs text-[#4A5546] mt-0.5">
                      {t('price_soybean_modal').replace('₹4,850', `₹${crop.typicalPrice.toLocaleString()}`)}
                    </p>
                  </div>
                </div>

                <div
                  className={`w-6 h-6 rounded-full flex items-center justify-center border ${
                    selectedCrop.id === crop.id
                      ? 'bg-[#1B5E20] border-[#1B5E20] text-white'
                      : 'border-[#D2DCC7] bg-white'
                  }`}
                >
                  {selectedCrop.id === crop.id && <Check className="w-3.5 h-3.5 stroke-[3]" />}
                </div>
              </button>
            ))}
          </div>

          <button
            id="crop-continue-btn"
            onClick={() => setCurrentStep('quantity')}
            className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
          >
            <span>{t('btn_continue')}</span>
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* STEP 2: QUANTITY INPUT */}
      {currentStep === 'quantity' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('step2_title')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('step2_subtitle')}</p>
          </div>

          {/* Stepper Card */}
          <div className="bg-white rounded-2xl p-6 border border-[#D2DCC7] text-center shadow-xs space-y-4">
            <span className="text-xs font-bold text-[#4A5546] uppercase tracking-wider">
              {t(selectedCrop.nameKey)}
            </span>

            <div className="flex items-center justify-center gap-4">
              <button
                id="qty-decrease-btn"
                onClick={() => setQuantity((q) => Math.max(5, q - 5))}
                className="w-12 h-12 rounded-2xl bg-[#F1F4EB] hover:bg-[#E8F5E9] text-[#1B5E20] font-black text-2xl flex items-center justify-center border border-[#D2DCC7] active:scale-95 transition-transform"
                aria-label={t('desc_decrease_qty')}
              >
                −
              </button>

              <div className="min-w-28">
                <span className="text-4xl font-black text-[#1B5E20] block">
                  {quantity}
                </span>
                <span className="text-xs font-bold text-[#4A5546]">
                  {t('unit_quintals')}
                </span>
              </div>

              <button
                id="qty-increase-btn"
                onClick={() => setQuantity((q) => q + 5)}
                className="w-12 h-12 rounded-2xl bg-[#F1F4EB] hover:bg-[#E8F5E9] text-[#1B5E20] font-black text-2xl flex items-center justify-center border border-[#D2DCC7] active:scale-95 transition-transform"
                aria-label={t('desc_increase_qty')}
              >
                +
              </button>
            </div>

            {/* Quick quantity chips */}
            <div className="flex items-center justify-center gap-2 pt-2">
              {[10, 25, 50, 100].map((preset) => (
                <button
                  key={preset}
                  onClick={() => setQuantity(preset)}
                  className={`px-3 py-1.5 text-xs font-bold rounded-xl border transition-colors ${
                    quantity === preset
                      ? 'bg-[#1B5E20] text-white border-[#1B5E20]'
                      : 'bg-[#FBFBF6] text-[#2E382B] border-[#D2DCC7] hover:bg-emerald-50'
                  }`}
                >
                  {preset} {t('unit_quintals')}
                </button>
              ))}
            </div>

            {/* Manual input toggle */}
            <div>
              <button
                type="button"
                onClick={() => setShowManualQty(!showManualQty)}
                className="text-xs text-[#1B5E20] font-bold underline"
              >
                {showManualQty ? t('hide_manual_entry') : t('type_exact_quantity')}
              </button>
              {showManualQty && (
                <div className="mt-2 flex items-center justify-center gap-2">
                  <input
                    type="number"
                    min={1}
                    value={quantity}
                    onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))}
                    className="w-24 p-2 text-center text-sm font-bold border border-[#1B5E20] rounded-xl focus:outline-none bg-[#F9FAF6]"
                  />
                  <span className="text-xs font-bold text-[#4A5546]">
                    {t('unit_quintals')}
                  </span>
                </div>
              )}
            </div>
          </div>

          <div className="flex items-center gap-2.5">
            <button
              onClick={() => setCurrentStep('crop')}
              className="py-3 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button
              id="quantity-continue-btn"
              onClick={() => setCurrentStep('quality')}
              className="flex-1 py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <span>{t('btn_continue')}</span>
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* STEP 3: QUALITY SELECTION */}
      {currentStep === 'quality' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('step3_title')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('step3_subtitle')}</p>
          </div>

          <div className="space-y-2.5">
            {(
              [
                { key: 'quality_good', titleKey: 'quality_good', descKey: 'quality_good_desc', badge: '+₹100/q' },
                { key: 'quality_average', titleKey: 'quality_average', descKey: 'quality_average_desc', badge: 'Standard' },
                { key: 'quality_poor', titleKey: 'quality_poor', descKey: 'quality_poor_desc', badge: '-₹220/q' },
              ] as const
            ).map((opt) => (
              <button
                key={opt.key}
                id={`quality-option-${opt.key}`}
                onClick={() => setQuality(opt.key)}
                className={`w-full p-4 rounded-2xl border text-left flex items-center justify-between transition-all ${
                  quality === opt.key
                    ? 'border-2 border-[#1B5E20] bg-[#E8F5E9] shadow-xs'
                    : 'border-[#D2DCC7] bg-white hover:bg-[#F9FAF6]'
                }`}
              >
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="font-bold text-base text-[#141A13]">
                      {t(opt.titleKey)}
                    </h3>
                    <span className="text-[10px] font-extrabold px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-800">
                      {opt.badge}
                    </span>
                  </div>
                  <p className="text-xs text-[#4A5546] mt-0.5">{t(opt.descKey)}</p>
                </div>

                <div
                  className={`w-6 h-6 rounded-full flex items-center justify-center border ${
                    quality === opt.key
                      ? 'bg-[#1B5E20] border-[#1B5E20] text-white'
                      : 'border-[#D2DCC7] bg-white'
                  }`}
                >
                  {quality === opt.key && <Check className="w-3.5 h-3.5 stroke-[3]" />}
                </div>
              </button>
            ))}
          </div>

          {/* Photo helper info */}
          <div className="bg-[#F1F4EB] border border-[#D2DCC7] rounded-xl p-3 flex items-center gap-2 text-xs text-[#2E382B]">
            <span>📷</span>
            <span>{t('quality_photo_helper')}</span>
          </div>

          <div className="flex items-center gap-2.5">
            <button
              onClick={() => setCurrentStep('quantity')}
              className="py-3 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button
              id="quality-continue-btn"
              onClick={() => setCurrentStep('location')}
              className="flex-1 py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <span>{t('btn_continue')}</span>
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* STEP 4: LOCATION INPUT */}
      {currentStep === 'location' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('step4_heading')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('step4_subtitle')}</p>
          </div>

          <div className="space-y-2.5">
            {locations.map((loc) => (
              <button
                key={loc.key}
                id={`location-option-${loc.key}`}
                onClick={() => setLocation(loc.key)}
                className={`w-full p-4 rounded-2xl border text-left flex items-center justify-between transition-all ${
                  location === loc.key
                    ? 'border-2 border-[#1B5E20] bg-[#E8F5E9] shadow-xs'
                    : 'border-[#D2DCC7] bg-white hover:bg-[#F9FAF6]'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-white border border-[#D2DCC7] flex items-center justify-center text-[#1B5E20]">
                    <MapPin className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="font-bold text-sm text-[#141A13]">
                      {t(loc.key)}
                    </h3>
                    {loc.key === 'loc_nagpur' && (
                      <span className="text-[10px] font-bold text-emerald-700 bg-emerald-100 px-1.5 py-0.5 rounded-sm">
                        {t('loc_use_my_location')}
                      </span>
                    )}
                  </div>
                </div>

                <div
                  className={`w-6 h-6 rounded-full flex items-center justify-center border ${
                    location === loc.key
                      ? 'bg-[#1B5E20] border-[#1B5E20] text-white'
                      : 'border-[#D2DCC7] bg-white'
                  }`}
                >
                  {location === loc.key && <Check className="w-3.5 h-3.5 stroke-[3]" />}
                </div>
              </button>
            ))}
          </div>

          <div className="flex items-center gap-2.5">
            <button
              onClick={() => setCurrentStep('quality')}
              className="py-3 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button
              id="location-continue-btn"
              onClick={() => setCurrentStep('timing')}
              className="flex-1 py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <span>{t('btn_continue')}</span>
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* STEP 5: TIMING INPUT */}
      {currentStep === 'timing' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('step5_title')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('step5_subtitle')}</p>
          </div>

          <div className="space-y-2.5">
            {timingOptions.map((opt) => (
              <button
                key={opt.key}
                id={`timing-option-${opt.key}`}
                onClick={() => setTiming(opt.key)}
                className={`w-full p-4 rounded-2xl border text-left flex items-center justify-between transition-all ${
                  timing === opt.key
                    ? 'border-2 border-[#1B5E20] bg-[#E8F5E9] shadow-xs'
                    : 'border-[#D2DCC7] bg-white hover:bg-[#F9FAF6]'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-white border border-[#D2DCC7] flex items-center justify-center text-xl">
                    {opt.emoji}
                  </div>
                  <div>
                    <h3 className="font-bold text-sm text-[#141A13]">
                      {t(opt.titleKey)}
                    </h3>
                    <p className="text-xs text-[#4A5546] mt-0.5">{t(opt.descKey)}</p>
                  </div>
                </div>

                <div
                  className={`w-6 h-6 rounded-full flex items-center justify-center border ${
                    timing === opt.key
                      ? 'bg-[#1B5E20] border-[#1B5E20] text-white'
                      : 'border-[#D2DCC7] bg-white'
                  }`}
                >
                  {timing === opt.key && <Check className="w-3.5 h-3.5 stroke-[3]" />}
                </div>
              </button>
            ))}
          </div>

          <div className="flex items-center gap-2.5">
            <button
              onClick={() => setCurrentStep('location')}
              className="py-3 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button
              id="timing-continue-btn"
              onClick={() => setCurrentStep('review')}
              className="flex-1 py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <span>{t('btn_continue')}</span>
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* STEP 6: REVIEW PRODUCE */}
      {currentStep === 'review' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('review_title')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('review_subtitle')}</p>
          </div>

          <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-[#F1F4EB]">
              <span className="text-xs font-extrabold text-[#4A5546] uppercase tracking-wider">
                {t('review_header_produce')}
              </span>
              <div className="text-2xl">{selectedCrop.emoji}</div>
            </div>

            <div className="space-y-3 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-[#4A5546]">{t('review_crop_label')}</span>
                <span className="font-bold text-[#141A13]">{t(selectedCrop.nameKey)}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-[#4A5546]">{t('review_quantity_label')}</span>
                <span className="font-bold text-[#141A13]">{quantity} {t('unit_quintals')}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-[#4A5546]">{t('review_quality_label')}</span>
                <span className="font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md">
                  {t(quality)}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-[#4A5546]">{t('review_location_label')}</span>
                <span className="font-bold text-[#141A13]">{t(location)}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-[#4A5546]">{t('review_timing_label')}</span>
                <span className="font-bold text-[#141A13]">{t(timing)}</span>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2.5">
            <button
              onClick={() => setCurrentStep('timing')}
              className="py-3 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button
              id="find-best-option-btn"
              onClick={startAnalysis}
              className="flex-1 py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <Sparkles className="w-4 h-4 text-amber-300" />
              <span>{t('btn_find_best_option')}</span>
            </button>
          </div>
        </div>
      )}

      {/* STEP 7: ANALYSIS ANIMATION */}
      {currentStep === 'analysis' && (
        <div className="bg-white rounded-2xl border border-[#D2DCC7] p-6 shadow-sm text-center space-y-6">
          <div className="w-16 h-16 rounded-full bg-[#E8F5E9] text-[#1B5E20] flex items-center justify-center mx-auto border-2 border-[#1B5E20] animate-bounce">
            <Sparkles className="w-8 h-8 text-[#1B5E20]" />
          </div>

          <div>
            <h3 className="text-lg font-black text-[#141A13]">
              {t('analysis_loading_title')}
            </h3>
            <p className="text-xs text-[#4A5546] mt-1">
              {quantity} {t('unit_quintals')} {t(selectedCrop.nameKey)}
            </p>
          </div>

          <div className="space-y-2.5 text-left text-xs font-semibold text-[#2E382B] max-w-xs mx-auto">
            <div className="flex items-center gap-2 text-emerald-700">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>{t('analysis_check_prices')}</span>
            </div>
            <div className="flex items-center gap-2 text-emerald-700">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>{t('analysis_compare_markets')}</span>
            </div>
            <div className="flex items-center gap-2 text-emerald-700">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>{t('analysis_check_demand')}</span>
            </div>
            <div className="flex items-center gap-2 text-emerald-700">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>{t('analysis_estimate_transport')}</span>
            </div>
            <div className="flex items-center gap-2 text-emerald-700">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>{t('analysis_compare_realization')}</span>
            </div>
          </div>
        </div>
      )}

      {/* STEP 8: RECOMMENDATION RESULTS */}
      {currentStep === 'recommendations' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('rec_results_title')}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('rec_results_subtitle')}</p>
          </div>

          <div className="space-y-3.5">
            {recommendations.map((opp) => {
              const isExpanded = expandedOppId === opp.id;
              return (
                <div
                  key={opp.id}
                  id={`rec-card-${opp.id}`}
                  className={`bg-white rounded-2xl border p-4 transition-all ${
                    opp.isTopRecommendation
                      ? 'border-2 border-[#1B5E20] shadow-md bg-gradient-to-b from-white to-[#F9FAF6]'
                      : 'border-[#D2DCC7] shadow-2xs'
                  }`}
                >
                  {/* Top Badge */}
                  <div className="flex items-center justify-between">
                    {opp.isTopRecommendation ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 bg-[#1B5E20] text-amber-300 text-[10px] font-black rounded-md tracking-wider uppercase">
                        {t('badge_top_pick')}
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-[#F1F4EB] text-[#4A5546] text-[10px] font-bold rounded-md">
                        {t('status_good_option')}
                      </span>
                    )}

                    <span className="text-xs text-[#4A5546] font-medium">
                      {opp.distanceKm} km
                    </span>
                  </div>

                  {/* Buyer details */}
                  <div className="mt-2 flex items-start justify-between">
                    <div>
                      <h3 className="font-extrabold text-base text-[#141A13]">
                        {t(opp.buyerNameKey)}
                      </h3>
                      <p className="text-xs text-[#4A5546] mt-0.5">
                        {t(opp.buyerTypeKey)}
                      </p>
                    </div>

                    <div className="text-right">
                      <span className="text-[10px] text-[#4A5546] block font-semibold">
                        {t('label_quoted_price_short')}
                      </span>
                      <span className="text-sm font-bold text-[#141A13]">
                        ₹{opp.quotedPricePerQ.toLocaleString()} / q
                      </span>
                    </div>
                  </div>

                  {/* Net Realization Hero Box */}
                  <div className="mt-2.5 p-3 rounded-xl bg-[#E8F5E9] border border-[#A5D6A7] flex items-baseline justify-between">
                    <div>
                      <span className="text-[10px] font-extrabold text-[#00390B] tracking-wider uppercase block">
                        {t('label_after_expenses_short')}
                      </span>
                      <span className="text-xl font-black text-[#1B5E20]">
                        ₹{opp.netRealizationPerQ.toLocaleString()} / q
                      </span>
                    </div>

                    <div className="text-right">
                      <span className="text-[10px] font-bold text-[#4A5546] block">
                        {t('label_estimated_total_short')}
                      </span>
                      <span className="text-sm font-black text-[#1B5E20]">
                        ₹{opp.estimatedTotalAmount.toLocaleString()}
                      </span>
                    </div>
                  </div>

                  {/* Expandable Breakdown / Why this option */}
                  {isExpanded && (
                    <div className="mt-3 pt-3 border-t border-[#D2DCC7] space-y-2 text-xs text-[#2E382B] animate-in fade-in">
                      <div className="font-bold text-[#141A13]">{t('btn_why_this_option')}</div>
                      <div className="space-y-1">
                        {opp.reasonsKeys.map((rk) => (
                          <div key={rk} className="flex items-center gap-1.5">
                            <span className="text-emerald-700">✓</span>
                            <span>{t(rk)}</span>
                          </div>
                        ))}
                      </div>

                      <div className="pt-2 grid grid-cols-2 gap-2 text-[11px] bg-[#F9FAF6] p-2.5 rounded-lg border border-[#E0E5D7]">
                        <div>
                          <span className="text-[#4A5546] block">{t('detail_transport')}:</span>
                          <span className="font-bold text-[#141A13]">₹{opp.transportExpensePerQ} / q</span>
                        </div>
                        <div>
                          <span className="text-[#4A5546] block">{t('detail_handling')}:</span>
                          <span className="font-bold text-[#141A13]">₹{opp.otherExpensePerQ} / q</span>
                        </div>
                        <div className="col-span-2">
                          <span className="text-[#4A5546] block">{t('detail_payment')}:</span>
                          <span className="font-bold text-emerald-800">{t(opp.paymentReliabilityKey)}</span>
                        </div>
                      </div>
                    </div>
                  )}

                  <div className="mt-3 flex items-center gap-2">
                    <button
                      onClick={() => setExpandedOppId(isExpanded ? null : opp.id)}
                      className="py-2 px-3 text-xs font-bold text-[#1B5E20] hover:bg-[#E8F5E9] rounded-xl transition-colors border border-transparent hover:border-[#A5D6A7]"
                    >
                      {isExpanded ? t('btn_hide_details') : t('btn_why_this_option')}
                    </button>

                    <button
                      id={`select-opp-${opp.id}`}
                      onClick={() => {
                        setSelectedOpportunity(opp);
                        setCurrentStep('confirm');
                      }}
                      className="flex-1 py-2.5 px-3 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl shadow-xs flex items-center justify-center gap-1 active:scale-98 transition-transform"
                    >
                      <span>{t('btn_sell_here')}</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>

          <button
            onClick={() => setCurrentStep('review')}
            className="w-full py-2.5 text-xs font-bold text-[#4A5546] hover:bg-[#F1F4EB] rounded-xl transition-colors"
          >
            ← {t('btn_back')}
          </button>
        </div>
      )}

      {/* STEP 9: CONFIRM SELL */}
      {currentStep === 'confirm' && selectedOpportunity && (
        <div className="space-y-4">
          <div>
            <h2 className="text-xl font-black text-[#141A13] tracking-tight">
              {t('confirm_sell_title', t(selectedOpportunity.buyerNameKey))}
            </h2>
            <p className="text-xs text-[#4A5546] mt-0.5">{t('confirm_sell_prompt')}</p>
          </div>

          {/* Financial Calculation Breakdown */}
          <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-4">
            <span className="text-xs font-extrabold text-[#4A5546] uppercase tracking-wider block">
              {t('breakdown_title')}
            </span>

            <div className="space-y-2.5 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-[#4A5546]">
                  {t('breakdown_gross')} ({quantity} × ₹{selectedOpportunity.quotedPricePerQ.toLocaleString()})
                </span>
                <span className="font-bold text-[#141A13]">
                  ₹{(quantity * selectedOpportunity.quotedPricePerQ).toLocaleString()}
                </span>
              </div>

              <div className="flex items-center justify-between text-red-700">
                <span>− {t('breakdown_transport')}</span>
                <span className="font-bold">
                  − ₹{(quantity * selectedOpportunity.transportExpensePerQ).toLocaleString()}
                </span>
              </div>

              <div className="flex items-center justify-between text-red-700">
                <span>− {t('breakdown_handling')}</span>
                <span className="font-bold">
                  − ₹{(quantity * selectedOpportunity.otherExpensePerQ).toLocaleString()}
                </span>
              </div>

              <div className="pt-3 border-t border-[#D2DCC7] flex items-baseline justify-between">
                <div>
                  <span className="font-black text-base text-[#141A13] block">
                    {t('breakdown_net')}
                  </span>
                  <span className="text-[11px] text-[#4A5546]">
                    ₹{selectedOpportunity.netRealizationPerQ.toLocaleString()} / {t('unit_quintals')}
                  </span>
                </div>
                <span className="text-2xl font-black text-[#1B5E20]">
                  ₹{selectedOpportunity.estimatedTotalAmount.toLocaleString()}
                </span>
              </div>
            </div>
          </div>

          <div className="bg-[#FFF8E1] border border-amber-200 rounded-xl p-3 flex items-start gap-2 text-xs text-amber-900 leading-relaxed">
            <Info className="w-4 h-4 text-amber-700 shrink-0 mt-0.5" />
            <span>{t('label_note_share_details')}</span>
          </div>

          <div className="flex items-center gap-2.5 pt-2">
            <button
              onClick={() => setCurrentStep('recommendations')}
              className="py-3 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              {t('btn_go_back')}
            </button>
            <button
              id="confirm-create-lot-btn"
              onClick={handleConfirmSell}
              className="flex-1 py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <Check className="w-4 h-4 stroke-[3]" />
              <span>{t('btn_create_lot')}</span>
            </button>
          </div>
        </div>
      )}

      {/* STEP 10: LOT CREATED SUCCESS */}
      {currentStep === 'success' && (
        <div className="bg-white rounded-2xl border-2 border-[#1B5E20] p-6 shadow-md text-center space-y-5 animate-in zoom-in-95">
          <div className="w-16 h-16 rounded-full bg-[#E8F5E9] text-[#1B5E20] flex items-center justify-center mx-auto border-2 border-[#1B5E20]">
            <Check className="w-8 h-8 stroke-[3]" />
          </div>

          <div>
            <span className="text-[11px] font-extrabold text-[#1B5E20] uppercase tracking-wider block">
              {t('title_lot_created')}
            </span>
            <h3 className="text-xl font-black text-[#141A13] mt-1">
              {t('success_lot_created')}
            </h3>
            <p className="text-xs text-[#4A5546] mt-1">
              {t('success_lot_msg')}
            </p>
          </div>

          <div className="p-3 bg-[#F1F4EB] rounded-xl font-mono text-sm font-black text-[#1B5E20] border border-[#D2DCC7]">
            {t('success_lot_id', createdLotId)}
          </div>

          <div className="space-y-2 pt-2">
            <button
              id="view-created-lot-btn"
              onClick={() => {
                navigateTo({ type: 'lot_details', lotId: createdLotId });
              }}
              className="w-full py-3.5 px-4 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-sm rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-98 transition-transform"
            >
              <span>{t('btn_view_my_lot')}</span>
              <ArrowRight className="w-4 h-4" />
            </button>

            <button
              id="done-flow-btn"
              onClick={() => {
                navigateTab('my_lots');
              }}
              className="w-full py-2.5 px-4 bg-white border border-[#D2DCC7] text-[#4A5546] font-bold text-sm rounded-xl hover:bg-[#F1F4EB]"
            >
              {t('btn_done')}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
