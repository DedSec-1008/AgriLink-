import React, { useState } from 'react';
import { useAgri } from '../context/AgriContext';
import {
  Phone,
  AlertCircle,
  ChevronDown,
  ChevronUp,
  ShieldCheck,
  HelpCircle,
  BookOpen,
  Send,
} from 'lucide-react';
import { GrievanceModal } from '../components/GrievanceModal';

export const HelpScreen: React.FC = () => {
  const { t, language, setLanguage } = useAgri();
  const [showGrievance, setShowGrievance] = useState(false);
  const [openFaq, setOpenFaq] = useState<number | null>(0);

  const faqs = [
    {
      q: 'FAQ: "Net in Pocket" (हातात येणारी रक्कम) म्हणजे काय?',
      a: 'AgriLink तुम्हाला निव्वळ भाव (Gross Quoted Price) मधून वाहतूक खर्च, हमाली, तोलाई व इतर वजावटी आधीच वजा करून प्रत्यक्ष तुमच्या बँक खात्यात किती रक्कम जमा होईल हे स्पष्टपणे दाखवते. यामुळे कोणतीही गुप्त वजावट (hidden cut) होत नाही.',
    },
    {
      q: 'FAQ: खरेदीदार (Buyer) कसे पडताळले जातात?',
      a: 'AgriLink वर केवळ APMC अधिकृत, परवानाधारक आणि ज्यांचा वेळेवर पेमेंट करण्याचा १००% ट्रॅक रेकॉर्ड आहे असेच खरेदीदार "Verified Buyer" म्हणून सूचीबद्ध केले जातात.',
    },
    {
      q: 'FAQ: वाहतूक (Transport) कशी व्यवस्थापित केली जाते?',
      a: 'ऑफर स्वीकारल्यानंतर तुम्ही थेट AgriLink वरून विश्वसनीय स्थानिक ट्रान्सपोर्टर बुक करू शकता. वाहन थेट तुमच्या शेतात किंवा गोदामात येऊन माल उचलते व सुरक्षितपणे खरेदीदारापर्यंत पोहोचवते.',
    },
    {
      q: 'FAQ: पेमेंट मिळण्यास उशीर झाल्यास काय करावे?',
      a: 'AgriLink च्या "तक्रार नोंदवा" (Register Grievance) बटनावर क्लिक करून तक्रार दाखल करा किंवा खाली दिलेल्या किसान कॉल सेंटरच्या १८००-१८०-१५५१ नंबरवर थेट संपर्क साधा. आमची टीम त्वरित मध्यस्थी करते.',
    },
  ];

  return (
    <div className="space-y-4 pb-24 px-4 pt-3 max-w-md mx-auto animate-in fade-in duration-200">
      <div>
        <h2 className="text-xl font-black text-[#141A13] tracking-tight">
          {t('tab_help')}
        </h2>
        <p className="text-xs text-[#4A5546] mt-0.5">
          {t('kisan_call_center_label')}
        </p>
      </div>

      {/* Helplines Box */}
      <div className="bg-gradient-to-r from-[#1B5E20] to-emerald-800 text-white rounded-2xl p-5 shadow-md space-y-4">
        <div className="flex items-center gap-2 text-amber-300 text-xs font-bold uppercase tracking-wider">
          <Phone className="w-4 h-4 animate-pulse" />
          <span>{t('helpline_label')}</span>
        </div>

        <div className="space-y-2">
          <div className="bg-white/10 backdrop-blur-xs p-3 rounded-xl border border-white/20 flex items-center justify-between">
            <div>
              <span className="text-[11px] text-emerald-100 block">
                {t('kisan_call_center_label')} (Toll-Free)
              </span>
              <span className="text-lg font-black text-white">1800-180-1551</span>
            </div>
            <a
              href="tel:18001801551"
              className="px-3 py-1.5 bg-amber-400 hover:bg-amber-300 text-[#141A13] font-bold text-xs rounded-lg shadow-xs"
            >
              Call Now
            </a>
          </div>

          <div className="bg-white/10 backdrop-blur-xs p-3 rounded-xl border border-white/20 flex items-center justify-between">
            <div>
              <span className="text-[11px] text-emerald-100 block">
                AgriLink Vidarbha Desk
              </span>
              <span className="text-sm font-bold text-white">+91 712 254 9901</span>
            </div>
            <a
              href="tel:+917122549901"
              className="px-3 py-1.5 bg-white text-[#1B5E20] font-bold text-xs rounded-lg"
            >
              Call Desk
            </a>
          </div>
        </div>
      </div>

      {/* Grievance Action Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-5 shadow-xs space-y-3">
        <div className="flex items-center gap-2">
          <AlertCircle className="w-5 h-5 text-[#5D4037]" />
          <h3 className="font-extrabold text-sm text-[#141A13]">
            {t('grievance_dialog_title')}
          </h3>
        </div>
        <p className="text-xs text-[#4A5546] leading-relaxed">
          {t('grievance_prompt')}
        </p>

        <button
          onClick={() => setShowGrievance(true)}
          className="w-full py-3 px-4 bg-[#5D4037] hover:bg-[#4E342E] text-white font-bold text-xs rounded-xl shadow-xs flex items-center justify-center gap-2 active:scale-98 transition-transform"
        >
          <Send className="w-3.5 h-3.5" />
          <span>{t('btn_get_help')}</span>
        </button>
      </div>

      {/* Language Switcher Card */}
      <div className="bg-white rounded-2xl border border-[#D2DCC7] p-4 shadow-xs flex items-center justify-between">
        <div>
          <span className="text-xs font-bold text-[#141A13] block">
            {t('app_language')}
          </span>
          <span className="text-[11px] text-[#4A5546]">
            {language === 'mr' ? 'मराठी निवडलेली आहे' : language === 'hi' ? 'हिंदी चुनी गई है' : 'English Selected'}
          </span>
        </div>

        <div className="flex items-center gap-1.5">
          {(
            [
              { code: 'mr', label: 'मराठी' },
              { code: 'hi', label: 'हिंदी' },
              { code: 'en', label: 'EN' },
            ] as const
          ).map((l) => (
            <button
              key={l.code}
              onClick={() => setLanguage(l.code)}
              className={`px-2.5 py-1 text-xs rounded-lg font-bold transition-all ${
                language === l.code
                  ? 'bg-[#1B5E20] text-white shadow-xs'
                  : 'bg-[#F1F4EB] text-[#2E382B] hover:bg-[#E8F5E9]'
              }`}
            >
              {l.label}
            </button>
          ))}
        </div>
      </div>

      {/* FAQ Section */}
      <div className="space-y-2.5">
        <span className="text-xs font-extrabold text-[#4A5546] uppercase tracking-wider px-1">
          वारंवार विचारले जाणारे प्रश्न (FAQs)
        </span>

        {faqs.map((faq, index) => {
          const isOpen = openFaq === index;
          return (
            <div
              key={index}
              className="bg-white rounded-2xl border border-[#D2DCC7] overflow-hidden shadow-2xs transition-all"
            >
              <button
                onClick={() => setOpenFaq(isOpen ? null : index)}
                className="w-full p-3.5 text-left flex items-center justify-between font-bold text-xs text-[#141A13] hover:bg-[#F9FAF6]"
              >
                <span>{faq.q}</span>
                {isOpen ? (
                  <ChevronUp className="w-4 h-4 text-[#1B5E20] shrink-0" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-[#4A5546] shrink-0" />
                )}
              </button>

              {isOpen && (
                <div className="p-3.5 pt-0 text-xs text-[#4A5546] leading-relaxed border-t border-[#F1F4EB]">
                  {faq.a}
                </div>
              )}
            </div>
          );
        })}
      </div>

      <GrievanceModal
        isOpen={showGrievance}
        onClose={() => setShowGrievance(false)}
      />
    </div>
  );
};
