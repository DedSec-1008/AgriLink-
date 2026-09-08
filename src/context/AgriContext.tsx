import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  AgriTransaction,
  AppScreen,
  Buyer,
  GrievanceIssue,
  LanguageCode,
  NavTab,
  Offer,
  ProduceItem,
  ProduceLot,
  TransporterOption,
  TransportBooking,
} from '../types';
import {
  initialBuyers,
  initialLots,
  initialOffers,
  initialProduce,
  initialTransactions,
  initialTransporters,
} from '../data/mockData';
import { formatString, translations } from '../data/translations';

interface AgriContextType {
  language: LanguageCode;
  setLanguage: (lang: LanguageCode) => void;
  t: (key: string, ...args: (string | number)[]) => string;
  screen: AppScreen;
  navigateTo: (screen: AppScreen) => void;
  navigateTab: (tab: NavTab) => void;
  produce: ProduceItem;
  setProduce: React.Dispatch<React.SetStateAction<ProduceItem>>;
  lots: ProduceLot[];
  offers: Offer[];
  transactions: AgriTransaction[];
  buyers: Buyer[];
  transporters: TransporterOption[];
  activeTransaction: AgriTransaction | null;
  createLot: (
    cropNameKey: string,
    iconEmoji: string,
    quantityQuintals: number,
    qualityKey: string,
    location: string,
    readyTiming: string,
    expectedPricePerQ: number,
    estimatedNetPerQ: number,
    buyerNameKey?: string
  ) => string;
  acceptOffer: (offerId: string) => AgriTransaction;
  bookTransport: (transactionId: string, transporter: TransporterOption) => void;
  confirmPickup: (transactionId: string) => void;
  confirmDelivery: (transactionId: string) => void;
  confirmPayment: (transactionId: string) => void;
  flagPaymentDelay: (transactionId: string) => void;
  submitBuyerRating: (transactionId: string, rating: number, feedback: string) => void;
  submitGrievance: (transactionId: string, issueTypeKey: string, details: string) => void;
  snackMessage: string | null;
  showSnack: (msg: string) => void;
}

const AgriContext = createContext<AgriContextType | undefined>(undefined);

const LOCAL_STORAGE_PREFIX = 'agrilink_';

export const AgriProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [language, setLanguageState] = useState<LanguageCode>(() => {
    const saved = localStorage.getItem(LOCAL_STORAGE_PREFIX + 'lang');
    return (saved as LanguageCode) || 'en';
  });

  const [screen, setScreen] = useState<AppScreen>({ type: 'main_nav', tab: 'home' });

  const [produce, setProduce] = useState<ProduceItem>(initialProduce);

  const [lots, setLots] = useState<ProduceLot[]>(() => {
    const saved = localStorage.getItem(LOCAL_STORAGE_PREFIX + 'lots');
    return saved ? JSON.parse(saved) : initialLots;
  });

  const [offers, setOffers] = useState<Offer[]>(() => {
    const saved = localStorage.getItem(LOCAL_STORAGE_PREFIX + 'offers');
    return saved ? JSON.parse(saved) : initialOffers;
  });

  const [transactions, setTransactions] = useState<AgriTransaction[]>(() => {
    const saved = localStorage.getItem(LOCAL_STORAGE_PREFIX + 'transactions');
    return saved ? JSON.parse(saved) : initialTransactions;
  });

  const [buyers] = useState<Buyer[]>(initialBuyers);
  const [transporters] = useState<TransporterOption[]>(initialTransporters);
  const [snackMessage, setSnackMessage] = useState<string | null>(null);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_PREFIX + 'lang', language);
  }, [language]);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_PREFIX + 'lots', JSON.stringify(lots));
  }, [lots]);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_PREFIX + 'offers', JSON.stringify(offers));
  }, [offers]);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_PREFIX + 'transactions', JSON.stringify(transactions));
  }, [transactions]);

  const setLanguage = (lang: LanguageCode) => {
    setLanguageState(lang);
  };

  const t = (key: string, ...args: (string | number)[]): string => {
    const dict = translations[language] || translations.en;
    const template = dict[key] || translations.en[key] || key;
    if (args.length > 0) {
      return formatString(template, ...args);
    }
    return template;
  };

  const showSnack = (msg: string) => {
    setSnackMessage(msg);
    setTimeout(() => {
      setSnackMessage((current) => (current === msg ? null : current));
    }, 4000);
  };

  const navigateTo = (newScreen: AppScreen) => {
    setScreen(newScreen);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const navigateTab = (tab: NavTab) => {
    navigateTo({ type: 'main_nav', tab });
  };

  // Find active ongoing transaction (not completed / cancelled)
  const activeTransaction =
    transactions.find(
      (tx) => tx.status !== 'COMPLETED' && tx.status !== 'CANCELLED'
    ) || null;

  const createLot = (
    cropNameKey: string,
    iconEmoji: string,
    quantityQuintals: number,
    qualityKey: string,
    location: string,
    readyTiming: string,
    expectedPricePerQ: number,
    estimatedNetPerQ: number,
    buyerNameKey?: string
  ): string => {
    const newLotId = `AG-${Math.floor(1000 + Math.random() * 9000)}`;
    const newLot: ProduceLot = {
      lotId: newLotId,
      cropNameKey,
      iconEmoji,
      quantityQuintals,
      qualityKey,
      statusKey: 'lot_status_waiting',
      dateCreated: new Date().toISOString().split('T')[0],
      buyerNameKey: buyerNameKey || null,
      location,
      readyTiming,
      expectedPricePerQ,
      estimatedNetPerQ,
    };

    setLots((prev) => [newLot, ...prev]);

    // Automatically generate 2 competitive buyer offers for this new lot!
    const matchingBuyer = buyers.find((b) => b.nameKey === buyerNameKey) || buyers[0];
    const secondBuyer = buyers.find((b) => b.id !== matchingBuyer.id) || buyers[1];

    const offer1: Offer = {
      id: `offer-${Date.now()}-1`,
      lotId: newLotId,
      buyerId: matchingBuyer.id,
      buyerNameKey: matchingBuyer.nameKey,
      isVerifiedBuyer: matchingBuyer.isVerified,
      farmerRating: matchingBuyer.farmerRating,
      pricePerQuintal: expectedPricePerQ,
      quantityQuintals,
      quotedTotalAmount: expectedPricePerQ * quantityQuintals,
      transportExpensePerQ: 90,
      otherExpensePerQ: 60,
      estimatedNetAmount: (expectedPricePerQ - 150) * quantityQuintals,
      paymentTermsKey: matchingBuyer.paymentTermsKey,
      deliveryRequirementsKey: matchingBuyer.deliveryTermsKey,
      qualityRequirementsKey: matchingBuyer.qualityRequirementsKey,
      status: 'PENDING',
      createdAt: 'Just now',
      expiresAt: 'In 24 hours',
    };

    const offer2Price = expectedPricePerQ - 60;
    const offer2: Offer = {
      id: `offer-${Date.now()}-2`,
      lotId: newLotId,
      buyerId: secondBuyer.id,
      buyerNameKey: secondBuyer.nameKey,
      isVerifiedBuyer: secondBuyer.isVerified,
      farmerRating: secondBuyer.farmerRating,
      pricePerQuintal: offer2Price,
      quantityQuintals,
      quotedTotalAmount: offer2Price * quantityQuintals,
      transportExpensePerQ: 75,
      otherExpensePerQ: 55,
      estimatedNetAmount: (offer2Price - 130) * quantityQuintals,
      paymentTermsKey: secondBuyer.paymentTermsKey,
      deliveryRequirementsKey: secondBuyer.deliveryTermsKey,
      qualityRequirementsKey: secondBuyer.qualityRequirementsKey,
      status: 'PENDING',
      createdAt: 'Just now',
      expiresAt: 'In 24 hours',
    };

    setOffers((prev) => [offer1, offer2, ...prev]);
    return newLotId;
  };

  const acceptOffer = (offerId: string): AgriTransaction => {
    const offer = offers.find((o) => o.id === offerId);
    if (!offer) {
      throw new Error(`Offer not found: ${offerId}`);
    }

    // Mark offer as accepted and other offers for this lot as rejected
    setOffers((prev) =>
      prev.map((o) => {
        if (o.id === offerId) return { ...o, status: 'ACCEPTED' };
        if (o.lotId === offer.lotId && o.status === 'PENDING') return { ...o, status: 'REJECTED' };
        return o;
      })
    );

    // Update lot status
    setLots((prev) =>
      prev.map((l) =>
        l.lotId === offer.lotId
          ? { ...l, statusKey: 'status_offer_accepted', buyerNameKey: offer.buyerNameKey }
          : l
      )
    );

    const lot = lots.find((l) => l.lotId === offer.lotId);
    const newTxId = `TX-${Math.floor(1000 + Math.random() * 9000)}`;

    const newTx: AgriTransaction = {
      id: newTxId,
      lotId: offer.lotId,
      offerId: offer.id,
      buyerId: offer.buyerId,
      buyerNameKey: offer.buyerNameKey,
      cropNameKey: lot ? lot.cropNameKey : 'crop_soybean',
      iconEmoji: lot ? lot.iconEmoji : '🌱',
      quantityQuintals: offer.quantityQuintals,
      agreedPricePerQ: offer.pricePerQuintal,
      grossProduceValue: offer.quotedTotalAmount,
      transportDeduction: offer.transportExpensePerQ * offer.quantityQuintals,
      otherDeductions: offer.otherExpensePerQ * offer.quantityQuintals,
      estimatedNetAmount: offer.estimatedNetAmount,
      status: 'OFFER_ACCEPTED',
      paymentStatus: 'PENDING',
      createdAt: new Date().toLocaleDateString(),
    };

    setTransactions((prev) => [newTx, ...prev]);
    return newTx;
  };

  const bookTransport = (transactionId: string, transporter: TransporterOption) => {
    const booking: TransportBooking = {
      bookingId: `BK-${Math.floor(1000 + Math.random() * 9000)}`,
      transactionId,
      transporterName: transporter.name,
      vehicleTypeKey: transporter.vehicleTypeKey,
      pickupLocation: t(transporter.pickupLocationKey),
      deliveryLocation: transporter.deliveryLocation,
      pickupTime: t(transporter.estimatedPickupTimeKey),
      distanceKm: transporter.distanceKm,
      totalCost: transporter.totalCost,
      costPerQ: transporter.costPerQuintal,
      isConfirmed: true,
    };

    setTransactions((prev) =>
      prev.map((tx) =>
        tx.id === transactionId
          ? {
              ...tx,
              status: 'LOGISTICS_BOOKED',
              transporterBooking: booking,
            }
          : tx
      )
    );
    showSnack(t('transport_arranged_header'));
  };

  const confirmPickup = (transactionId: string) => {
    setTransactions((prev) =>
      prev.map((tx) =>
        tx.id === transactionId
          ? {
              ...tx,
              status: 'DISPATCHED',
            }
          : tx
      )
    );
    showSnack(t('pickup_success_snack'));
  };

  const confirmDelivery = (transactionId: string) => {
    setTransactions((prev) =>
      prev.map((tx) =>
        tx.id === transactionId
          ? {
              ...tx,
              status: 'DELIVERED',
              paymentStatus: 'INITIATED',
            }
          : tx
      )
    );
    showSnack(`${t('title_produce_delivered')} - ${t('status_in_transit')}`);
  };

  const confirmPayment = (transactionId: string) => {
    setTransactions((prev) =>
      prev.map((tx) =>
        tx.id === transactionId
          ? {
              ...tx,
              status: 'COMPLETED',
              paymentStatus: 'RECEIVED',
            }
          : tx
      )
    );
    showSnack(t('payment_received_header'));
  };

  const flagPaymentDelay = (transactionId: string) => {
    setTransactions((prev) =>
      prev.map((tx) =>
        tx.id === transactionId
          ? {
              ...tx,
              status: 'PAYMENT_DELAYED',
              paymentStatus: 'DELAYED',
            }
          : tx
      )
    );
    showSnack(t('payment_delayed_header'));
  };

  const submitBuyerRating = (transactionId: string, rating: number, feedback: string) => {
    setTransactions((prev) =>
      prev.map((tx) =>
        tx.id === transactionId
          ? {
              ...tx,
              buyerRating: rating,
              buyerFeedback: feedback,
            }
          : tx
      )
    );
    showSnack(t('rating_submitted_msg'));
  };

  const submitGrievance = (_transactionId: string, _issueTypeKey: string, _details: string) => {
    showSnack(t('grievance_submitted_snack'));
  };

  return (
    <AgriContext.Provider
      value={{
        language,
        setLanguage,
        t,
        screen,
        navigateTo,
        navigateTab,
        produce,
        setProduce,
        lots,
        offers,
        transactions,
        buyers,
        transporters,
        activeTransaction,
        createLot,
        acceptOffer,
        bookTransport,
        confirmPickup,
        confirmDelivery,
        confirmPayment,
        flagPaymentDelay,
        submitBuyerRating,
        submitGrievance,
        snackMessage,
        showSnack,
      }}
    >
      {children}
    </AgriContext.Provider>
  );
};

export const useAgri = () => {
  const context = useContext(AgriContext);
  if (!context) {
    throw new Error('useAgri must be used within an AgriProvider');
  }
  return context;
};
