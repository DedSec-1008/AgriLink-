import React from 'react';
import { useAgri } from '../context/AgriContext';
import { MapPin, Phone, HelpCircle, ArrowLeft } from 'lucide-react';
import { LanguageCode } from '../types';

interface AgriTopBarProps {
  showBack?: boolean;
  onBack?: () => void;
  title?: string;
}

export const AgriTopBar: React.FC<AgriTopBarProps> = ({ showBack, onBack, title }) => {
  const { language, setLanguage, t, navigateTab } = useAgri();

  const languages: { code: LanguageCode; label: string }[] = [
    { code: 'mr', label: 'मराठी' },
    { code: 'hi', label: 'हिन्दी' },
    { code: 'en', label: 'EN' },
  ];

  return (
    <header className="sticky top-0 z-40 bg-[#1B5E20] text-white shadow-md">
      {/* Top micro banner with toll-free helpline */}
      <div className="bg-[#144718] px-4 py-1.5 flex items-center justify-between text-xs text-emerald-100 border-b border-emerald-800/40">
        <a
          href="tel:18001801551"
          className="flex items-center gap-1.5 hover:text-white transition-colors"
          id="toll-free-link"
        >
          <Phone className="w-3.5 h-3.5 text-amber-300" />
          <span className="font-medium text-amber-200">1800-180-1551</span>
          <span className="opacity-75 hidden sm:inline">• 6 AM – 10 PM</span>
        </a>

        {/* Language selector chips */}
        <div className="flex items-center gap-1">
          {languages.map((item) => (
            <button
              key={item.code}
              id={`lang-btn-${item.code}`}
              onClick={() => setLanguage(item.code)}
              className={`px-2 py-0.5 text-xs rounded-full font-medium transition-all ${
                language === item.code
                  ? 'bg-amber-400 text-[#141A13] font-bold shadow-xs'
                  : 'bg-emerald-900/60 text-emerald-100 hover:bg-emerald-800/80'
              }`}
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>

      {/* Main App Bar */}
      <div className="px-4 py-3 flex items-center justify-between gap-3">
        <div className="flex items-center gap-2.5 min-w-0">
          {showBack && onBack ? (
            <button
              id="topbar-back-button"
              onClick={onBack}
              className="p-1.5 -ml-1.5 rounded-full hover:bg-emerald-700/50 active:bg-emerald-800 transition-colors text-white"
              aria-label={t('btn_back')}
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
          ) : (
            <div className="w-9 h-9 rounded-xl bg-white/15 flex items-center justify-center text-xl shrink-0 shadow-inner">
              🌱
            </div>
          )}

          <div className="min-w-0">
            {title ? (
              <h1 className="text-lg font-bold truncate leading-tight tracking-tight text-white">
                {title}
              </h1>
            ) : (
              <>
                <div className="flex items-center gap-1.5 text-emerald-100 text-xs font-medium">
                  <MapPin className="w-3 h-3 text-amber-300 shrink-0" />
                  <span className="truncate">{t('location_nagpur')}</span>
                </div>
                <h1 className="text-base sm:text-lg font-bold text-white tracking-tight leading-tight truncate">
                  {t('greeting_farmer')}
                </h1>
              </>
            )}
          </div>
        </div>

        {/* Right action button */}
        <div className="flex items-center gap-1.5 shrink-0">
          <button
            id="topbar-help-button"
            onClick={() => navigateTab('help')}
            className="p-2 rounded-xl bg-white/10 hover:bg-white/20 active:bg-white/25 text-emerald-100 hover:text-white transition-colors flex items-center gap-1"
            title={t('title_help')}
          >
            <HelpCircle className="w-5 h-5 text-amber-300" />
            <span className="text-xs font-semibold hidden md:inline">{t('nav_help')}</span>
          </button>
        </div>
      </div>
    </header>
  );
};
