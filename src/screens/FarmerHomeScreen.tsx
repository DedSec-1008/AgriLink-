import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import {
  TrendingUp,
  ShieldCheck,
  ArrowRight,
  Sparkles,
  ShoppingBag,
  Layers,
  HelpCircle,
  Truck,
  Users,
  CheckCircle2,
} from 'lucide-react';
import { GrievanceModal } from '../components/GrievanceModal';

export const FarmerHomeScreen: React.FC = () => {
  const { t, navigateTab, navigateTo, produce, activeTransaction } = useAgri();
  const [showGrievance, setShowGrievance] = useState(false);

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      {/* Active Sale Banner if farmer has an in-progress transaction */}
      {activeTransaction && (
        <div
          id="home-active-sale-card"
          className="bg-gradient-to-r from-emerald-800 to-[#1B5E20] text-white p-4 rounded-2xl shadow-md border border-emerald-600/40 relative overflow-hidden"
        >
          <div className="flex items-start justify-between gap-3">
            <div>
              <div className="flex items-center gap-1.5 text-amber-300 text-xs font-bold uppercase tracking-wider">
                <Truck className="w-3.5 h-3.5 animate-pulse" />
                <span>{t('home_active_sale_title')}</span>
              </div>
              <h3 className="font-extrabold text-base mt-0.5">
                {t(activeTransaction.buyerNameKey)} • {t(activeTransaction.cropNameKey)}
              </h3>
              <p className="text-xs text-emerald-100 mt-0.5">
                {t('tx_status_' + activeTransaction.status.toLowerCase())}
              </p>
            </div>
            <button
              onClick={() =>
                navigateTo({
                  type: 'transaction_detail',
                  transactionId: activeTransaction.id,
                })
              }
              className="px-3 py-1.5 bg-amber-400 hover:bg-amber-300 text-[#141A13] font-bold text-xs rounded-xl shadow-xs shrink-0 flex items-center gap-1 active:scale-95 transition-transform"
            >
              <span>{t('btn_view_sale')}</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      )}

      {/* Produce Summary Card */}
      <section
        id="produce-summary-card"
        className="bg-white rounded-2xl p-4 border border-[#D2DCC7] shadow-xs"
      >
        <div className="flex items-center justify-between">
          <span className="text-[11px] font-extrabold text-[#4A5546] tracking-wider uppercase">
            {t('section_your_produce')}
          </span>
          <button
            onClick={() => navigateTab('sell')}
            className="text-xs font-bold text-[#1B5E20] hover:underline"
          >
            {t('review_edit')}
          </button>
        </div>

        <div className="mt-2.5 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-2xl bg-[#E8F5E9] flex items-center justify-center text-2xl shrink-0 border border-[#C8E6C9]">
              {produce.iconEmoji}
            </div>
            <div>
              <h2 className="text-lg font-bold text-[#141A13] leading-tight">
                {t(produce.cropNameKey)}
              </h2>
              <div className="flex items-center gap-2 mt-0.5 text-xs text-[#4A5546] font-medium">
                <span className="font-semibold text-[#141A13]">
                  {produce.quantityQuintals} {t('unit_quintals')}
                </span>
                <span>•</span>
                <span className="inline-flex items-center gap-1 text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md font-semibold">
                  <CheckCircle2 className="w-3 h-3" />
                  {t(produce.qualityKey)}
                </span>
              </div>
            </div>
          </div>

          <button
            onClick={() => navigateTab('sell')}
            className="px-3 py-1.5 bg-[#E8F5E9] hover:bg-[#C8E6C9] text-[#1B5E20] font-bold text-xs rounded-xl transition-colors border border-[#A5D6A7]"
          >
            + {t('btn_new_lot')}
          </button>
        </div>
      </section>

      {/* Today's Market Price Card */}
      <section
        id="todays-price-card"
        className="bg-white rounded-2xl p-4 border border-[#D2DCC7] shadow-xs"
      >
        <div className="flex items-center justify-between">
          <span className="text-[11px] font-extrabold text-[#4A5546] tracking-wider uppercase">
            {t('section_todays_price')}
          </span>
          <button
            onClick={() => navigateTab('prices')}
            className="text-xs font-bold text-[#1B5E20] flex items-center gap-0.5 hover:underline"
          >
            <span>{t('title_prices')}</span>
            <ArrowRight className="w-3 h-3" />
          </button>
        </div>

        <div className="mt-2 flex items-baseline justify-between">
          <div>
            <div className="text-2xl font-black text-[#141A13] tracking-tight">
              {t('price_soybean_modal')}
            </div>
            <div className="text-xs font-semibold text-[#4A5546] mt-0.5">
              {t('market_nagpur')}
            </div>
          </div>
          <div className="text-right">
            <span className="inline-flex items-center gap-1 px-2.5 py-1 bg-emerald-100 text-emerald-800 text-xs font-bold rounded-lg border border-emerald-200">
              <TrendingUp className="w-3.5 h-3.5" />
              {t('price_change_yesterday')}
            </span>
          </div>
        </div>
      </section>

      {/* Best Selling Opportunity Card (Hero) */}
      <section
        id="best-opportunity-card"
        className="bg-gradient-to-b from-white to-[#F9FAF6] rounded-2xl border-2 border-[#1B5E20] shadow-md p-4 space-y-3 relative overflow-hidden"
      >
        {/* Top badge */}
        <div className="flex items-center justify-between">
          <span className="inline-flex items-center gap-1 px-2.5 py-1 bg-[#1B5E20] text-amber-300 text-[11px] font-extrabold rounded-lg tracking-wide uppercase">
            <Sparkles className="w-3 h-3 text-amber-300" />
            {t('badge_best_opportunity')}
          </span>
          <span className="inline-flex items-center gap-1 text-[11px] font-bold text-emerald-800 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
            {t('verified_buyer_tag')}
          </span>
        </div>

        {/* Buyer Title */}
        <div className="flex items-center justify-between pt-1">
          <div>
            <h3 className="text-lg font-black text-[#141A13]">
              {t('buyer_abc_foods')}
            </h3>
            <p className="text-xs text-[#4A5546] font-medium">
              {t('buyer_type_verified_direct')} • 28 km
            </p>
          </div>
          <div className="text-right">
            <span className="text-xs text-[#4A5546] block font-medium">
              {t('label_quoted_price')}
            </span>
            <span className="text-base font-bold text-[#141A13]">
              {t('quoted_price_val')}
            </span>
          </div>
        </div>

        {/* Net realization Highlight Box (The core value proposition for farmers) */}
        <div className="bg-[#E8F5E9] rounded-xl p-3 border border-[#A5D6A7] shadow-inner space-y-1">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-extrabold text-[#00390B] tracking-wider uppercase">
              {t('label_net_realization_hero')}
            </span>
            <span className="text-[10px] text-[#2E7D32] font-semibold bg-white/80 px-2 py-0.5 rounded-md">
              {t('label_deduction_note')}
            </span>
          </div>
          <div className="flex items-baseline justify-between pt-0.5">
            <span className="text-2xl font-black text-[#1B5E20]">
              {t('net_price_val')}
            </span>
            <div className="text-right">
              <span className="text-[10px] text-[#4A5546] block font-semibold">
                {t('label_estimated_total')}
              </span>
              <span className="text-base font-extrabold text-[#1B5E20]">
                {t('total_payout_val')}
              </span>
            </div>
          </div>
        </div>

        {/* Reasons list */}
        <div className="grid grid-cols-2 gap-1.5 text-xs text-[#2E382B] pt-1">
          <div className="flex items-center gap-1.5 font-medium">
            <span className="text-emerald-700">✓</span>
            <span>{t('reason_highest_payout')}</span>
          </div>
          <div className="flex items-center gap-1.5 font-medium">
            <span className="text-emerald-700">✓</span>
            <span>{t('reason_verified_buyer')}</span>
          </div>
          <div className="flex items-center gap-1.5 font-medium">
            <span className="text-emerald-700">✓</span>
            <span>{t('reason_payment_history')}</span>
          </div>
          <div className="flex items-center gap-1.5 font-medium">
            <span className="text-emerald-700">✓</span>
            <span>{t('reason_low_transport')}</span>
          </div>
        </div>

        {/* Action buttons */}
        <div className="grid grid-cols-2 gap-2.5 pt-2">
          <button
            id="find-best-place-btn"
            onClick={() => navigateTab('sell')}
            className="py-2.5 px-3 bg-white hover:bg-[#F1F4EB] text-[#1B5E20] font-bold text-xs rounded-xl border border-[#1B5E20] transition-colors shadow-2xs text-center"
          >
            {t('btn_find_best_place')}
          </button>
          <button
            id="sell-here-btn"
            onClick={() => {
              // Direct sell flow to ABC Foods
              navigateTo({ type: 'buyer_profile', buyerId: 'buyer-abc' });
            }}
            className="py-2.5 px-3 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold text-xs rounded-xl transition-all shadow-md active:scale-98 text-center flex items-center justify-center gap-1"
          >
            <span>{t('btn_sell_here')}</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>
      </section>

      {/* Quick Actions Grid */}
      <section id="quick-actions-section" className="space-y-2">
        <span className="text-[11px] font-extrabold text-[#4A5546] tracking-wider uppercase px-1">
          {t('section_quick_actions')}
        </span>

        <div className="grid grid-cols-2 gap-2.5">
          <button
            id="quick-action-prices"
            onClick={() => navigateTab('prices')}
            className="bg-white p-3.5 rounded-xl border border-[#D2DCC7] hover:border-[#1B5E20] hover:bg-emerald-50/40 text-left transition-all group flex items-start gap-3 shadow-2xs"
          >
            <div className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 text-amber-700 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <TrendingUp className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-[#141A13]">
                {t('action_check_prices')}
              </h4>
              <p className="text-[11px] text-[#4A5546] leading-tight mt-0.5">
                {t('subtitle_prices')}
              </p>
            </div>
          </button>

          <button
            id="quick-action-sell"
            onClick={() => navigateTab('sell')}
            className="bg-white p-3.5 rounded-xl border border-[#D2DCC7] hover:border-[#1B5E20] hover:bg-emerald-50/40 text-left transition-all group flex items-start gap-3 shadow-2xs"
          >
            <div className="w-10 h-10 rounded-xl bg-emerald-50 border border-emerald-200 text-[#1B5E20] flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <ShoppingBag className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-[#141A13]">
                {t('action_sell_produce')}
              </h4>
              <p className="text-[11px] text-[#4A5546] leading-tight mt-0.5">
                {t('subtitle_where_to_sell')}
              </p>
            </div>
          </button>

          <button
            id="quick-action-buyers"
            onClick={() => navigateTo({ type: 'buyers_directory' })}
            className="bg-white p-3.5 rounded-xl border border-[#D2DCC7] hover:border-[#1B5E20] hover:bg-emerald-50/40 text-left transition-all group flex items-start gap-3 shadow-2xs"
          >
            <div className="w-10 h-10 rounded-xl bg-blue-50 border border-blue-200 text-blue-700 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <Users className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-[#141A13]">
                {t('action_browse_buyers')}
              </h4>
              <p className="text-[11px] text-[#4A5546] leading-tight mt-0.5">
                {t('subtitle_buyers')}
              </p>
            </div>
          </button>

          <button
            id="quick-action-lots"
            onClick={() => navigateTab('my_lots')}
            className="bg-white p-3.5 rounded-xl border border-[#D2DCC7] hover:border-[#1B5E20] hover:bg-emerald-50/40 text-left transition-all group flex items-start gap-3 shadow-2xs"
          >
            <div className="w-10 h-10 rounded-xl bg-purple-50 border border-purple-200 text-purple-700 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <Layers className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-[#141A13]">
                {t('action_my_lots')}
              </h4>
              <p className="text-[11px] text-[#4A5546] leading-tight mt-0.5">
                {t('subtitle_my_lots')}
              </p>
            </div>
          </button>
        </div>
      </section>

      {/* Having a problem / Support Ticket footer */}
      <div className="bg-[#EFEBE9] rounded-xl p-3 flex items-center justify-between border border-[#D7CCC8]">
        <div className="flex items-center gap-2">
          <HelpCircle className="w-4 h-4 text-[#5D4037]" />
          <span className="text-xs font-semibold text-[#5D4037]">
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
      />
    </div>
  );
};
