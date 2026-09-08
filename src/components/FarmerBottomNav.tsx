import React from 'react';
import { useAgri } from '../context/AgriContext';
import { NavTab } from '../types';
import { Home, TrendingUp, PlusCircle, Layers, HelpCircle } from 'lucide-react';

interface FarmerBottomNavProps {
  activeTab?: NavTab;
  onSelectTab?: (tab: NavTab) => void;
}

export const FarmerBottomNav: React.FC<FarmerBottomNavProps> = ({
  activeTab: propActiveTab,
  onSelectTab: propOnSelectTab,
}) => {
  const { t, offers, screen, navigateTab } = useAgri();

  const activeTab: NavTab =
    propActiveTab ?? (screen.type === 'main_nav' ? screen.tab : 'home');
  const onSelectTab = propOnSelectTab ?? navigateTab;

  // Check pending offers to show notification badge on "My Lots"
  const pendingOffersCount = offers.filter((o) => o.status === 'PENDING').length;

  const tabs: { id: NavTab; labelKey: string; icon: React.ComponentType<{ className?: string }> }[] = [
    { id: 'home', labelKey: 'nav_home', icon: Home },
    { id: 'prices', labelKey: 'nav_prices', icon: TrendingUp },
    { id: 'sell', labelKey: 'nav_sell', icon: PlusCircle },
    { id: 'my_lots', labelKey: 'nav_my_lots', icon: Layers },
    { id: 'help', labelKey: 'nav_help', icon: HelpCircle },
  ];

  return (
    <nav
      className="fixed bottom-0 left-0 right-0 z-30 bg-white/95 backdrop-blur-md border-t border-[#D2DCC7] shadow-lg max-w-md mx-auto"
      id="farmer-bottom-nav"
    >
      <div className="flex items-center justify-around py-1.5 px-2">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          const isSellTab = tab.id === 'sell';

          if (isSellTab) {
            return (
              <button
                key={tab.id}
                id={`bottom-nav-${tab.id}`}
                onClick={() => onSelectTab(tab.id)}
                className="flex flex-col items-center justify-center -mt-5 group focus:outline-none"
                aria-label={t(tab.labelKey)}
              >
                <div
                  className={`w-13 h-13 rounded-full flex items-center justify-center shadow-lg transition-transform active:scale-95 ${
                    isActive
                      ? 'bg-gradient-to-tr from-[#1B5E20] to-[#2E7D32] text-white ring-4 ring-[#E8F5E9]'
                      : 'bg-[#1B5E20] text-white hover:bg-[#2E7D32]'
                  }`}
                >
                  <Icon className="w-7 h-7" />
                </div>
                <span
                  className={`text-[11px] mt-1 font-bold ${
                    isActive ? 'text-[#1B5E20]' : 'text-[#4A5546]'
                  }`}
                >
                  {t(tab.labelKey)}
                </span>
              </button>
            );
          }

          return (
            <button
              key={tab.id}
              id={`bottom-nav-${tab.id}`}
              onClick={() => onSelectTab(tab.id)}
              className={`flex-1 flex flex-col items-center justify-center py-1 relative transition-colors ${
                isActive ? 'text-[#1B5E20]' : 'text-[#4A5546] hover:text-[#141A13]'
              }`}
            >
              <div className="relative">
                <Icon className={`w-5 h-5 ${isActive ? 'stroke-[2.5]' : 'stroke-[1.8]'}`} />
                {tab.id === 'my_lots' && pendingOffersCount > 0 && (
                  <span className="absolute -top-1 -right-2 bg-amber-500 text-white text-[10px] font-extrabold w-4 h-4 rounded-full flex items-center justify-center shadow-xs">
                    {pendingOffersCount}
                  </span>
                )}
              </div>
              <span
                className={`text-[11px] mt-0.5 tracking-tight ${
                  isActive ? 'font-bold text-[#1B5E20]' : 'font-medium'
                }`}
              >
                {t(tab.labelKey)}
              </span>
              {isActive && (
                <div className="w-1.5 h-1.5 rounded-full bg-[#1B5E20] mt-0.5" />
              )}
            </button>
          );
        })}
      </div>
    </nav>
  );
};
