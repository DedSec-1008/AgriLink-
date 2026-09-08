export type LanguageCode = 'en' | 'hi' | 'mr';

export interface ProduceItem {
  cropNameKey: string;
  iconEmoji: string;
  quantityQuintals: number;
  qualityKey: string;
}

export interface MarketPriceInfo {
  id: string;
  cropNameKey: string;
  marketNameKey: string;
  pricePerQuintal: number;
  priceChangeTextKey: string;
  isPositiveChange: boolean;
}

export interface SellingOpportunity {
  id: string;
  buyerNameKey: string;
  buyerTypeKey: string;
  cropNameKey: string;
  quantityQuintals: number;
  quotedPricePerQ: number;
  transportExpensePerQ: number;
  otherExpensePerQ: number;
  handlingCostPerQ: number;
  storageCostPerQ: number;
  transactionCostPerQ: number;
  netRealizationPerQ: number;
  estimatedTotalAmount: number;
  statusTextKey: string;
  recommendationStrength: 'STRONG' | 'GOOD' | 'MODERATE';
  isTopRecommendation: boolean;
  distanceKm: number;
  paymentReliabilityKey: string;
  reasonsKeys: string[];
  cautionsKeys?: string[];
  quantityMatch: boolean;
  qualityMatch: boolean;
  isVerifiedBuyer: boolean;
}

export interface ProduceLot {
  lotId: string;
  cropNameKey: string;
  iconEmoji: string;
  quantityQuintals: number;
  qualityKey: string;
  statusKey: string;
  dateCreated: string;
  buyerNameKey?: string | null;
  location: string;
  readyTiming: string;
  expectedPricePerQ: number;
  estimatedNetPerQ: number;
}

export interface Buyer {
  id: string;
  nameKey: string;
  locationKey: string;
  isVerified: boolean;
  commodityKey: string;
  currentDemandMinQ: number;
  currentDemandMaxQ: number;
  requiredQualityKey: string;
  quotedPricePerQ: number;
  paymentDaysDescriptionKey: string;
  onTimePaymentPct: number;
  completedTransactions: number;
  farmerRating: number;
  neededTimelineKey: string;
  disputeRatePct: number;
  averagePaymentDays: number;
  paymentTermsKey: string;
  deliveryTermsKey: string;
  qualityRequirementsKey: string;
  address?: string;
  distanceKm?: number;
  rating?: number;
  completedTransactionsCount?: number;
  paymentTurnaroundKey?: string;
  acceptedCropsKeys?: string[];
}

export type OfferStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED' | 'CANCELLED';

export interface Offer {
  id: string;
  lotId: string;
  buyerId: string;
  buyerNameKey: string;
  isVerifiedBuyer: boolean;
  farmerRating: number;
  pricePerQuintal: number;
  quantityQuintals: number;
  quotedTotalAmount: number;
  transportExpensePerQ: number;
  otherExpensePerQ: number;
  estimatedNetAmount: number;
  paymentTermsKey: string;
  deliveryRequirementsKey: string;
  qualityRequirementsKey: string;
  status: OfferStatus;
  createdAt: string;
  expiresAt: string;
}

export type TransactionStatus =
  | 'OFFER_ACCEPTED'
  | 'LOGISTICS_BOOKED'
  | 'DISPATCHED'
  | 'DELIVERED'
  | 'PAYMENT_INITIATED'
  | 'PAYMENT_RECEIVED'
  | 'COMPLETED'
  | 'PAYMENT_DELAYED'
  | 'CANCELLED'
  | 'DISPUTED';

export type PaymentStatus = 'PENDING' | 'INITIATED' | 'RECEIVED' | 'DELAYED';

export interface TransporterOption {
  id: string;
  name: string;
  vehicleTypeKey: string;
  isVerified: boolean;
  rating: number;
  estimatedPickupTimeKey: string;
  distanceKm: number;
  totalCost: number;
  costPerQuintal: number;
  pickupLocationKey: string;
  deliveryLocation: string;
}

export interface TransportBooking {
  bookingId: string;
  transactionId: string;
  transporterName: string;
  vehicleTypeKey: string;
  pickupLocation: string;
  deliveryLocation: string;
  pickupTime: string;
  distanceKm: number;
  totalCost: number;
  costPerQ: number;
  isConfirmed: boolean;
}

export interface AgriTransaction {
  id: string;
  lotId: string;
  offerId: string;
  buyerId: string;
  buyerNameKey: string;
  cropNameKey: string;
  iconEmoji: string;
  quantityQuintals: number;
  agreedPricePerQ: number;
  grossProduceValue: number;
  transportDeduction: number;
  otherDeductions: number;
  estimatedNetAmount: number;
  status: TransactionStatus;
  paymentStatus: PaymentStatus;
  transporterBooking?: TransportBooking | null;
  createdAt: string;
  buyerRating?: number | null;
  buyerFeedback?: string | null;
}

export interface GrievanceIssue {
  id: string;
  transactionId: string;
  issueTypeKey: string;
  details: string;
  createdAt: string;
}

export interface CropOption {
  id: string;
  nameKey: string;
  emoji: string;
  typicalPrice: number;
}

export interface HelpCategory {
  id: string;
  titleKey: string;
  iconEmoji: string;
}

export type NavTab = 'home' | 'prices' | 'sell' | 'my_lots' | 'help';

export type AppScreen =
  | { type: 'main_nav'; tab: NavTab }
  | { type: 'buyers_directory' }
  | { type: 'buyer_profile'; buyerId: string }
  | { type: 'lot_details'; lotId: string }
  | { type: 'offers'; lotId: string }
  | { type: 'offer_details'; offerId: string }
  | { type: 'accept_offer_confirm'; offerId: string }
  | { type: 'transaction_detail'; transactionId: string }
  | { type: 'arrange_transport'; transactionId: string }
  | { type: 'produce_pickup'; transactionId: string }
  | { type: 'delivery_tracking'; transactionId: string }
  | { type: 'payment_tracking'; transactionId: string }
  | { type: 'sale_completed'; transactionId: string }
  | { type: 'your_sales' };
