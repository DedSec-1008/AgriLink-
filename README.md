# AgriLink (Web)

An agricultural market-linkage & price discovery platform ported from Android to React, TypeScript, and Tailwind CSS.

## Features Preserved & Implemented
- **Home & Dashboard**: Produce summary, active transaction lifecycle banner, and quick-action navigation.
- **Selling Flow**: Multi-step lot creation wizard (Crop selection, Quality grading, Quantity & Storage timing, Net realization calculator, Buyer matching).
- **Mandi Market Prices**: Real-time mandi rates across regional agricultural markets with positive/negative trend indicators and search filter.
- **My Lots & Competitive Offers**: Lot listings, status badges, incoming buyer offers with transparent net realization breakdown (deducting transport & handling).
- **6-Stage Transaction Lifecycle**:
  1. Offer Accepted
  2. Logistics Booking (Vehicle selection, transporter rating, cost per quintal)
  3. Produce Pickup & Dispatch confirmation
  4. Delivery Tracking with timeline checkpoints
  5. Payment Tracking & Delay Flagging
  6. Sale Completion & Buyer Rating
- **Buyers Directory & Profiles**: Verified badges, payment turnaround records, accepted commodities, and historical reliability.
- **Support & Grievances**: Category-based help desk, emergency helpline, and formal issue escalation modal.
- **Multi-Language Localization**: Full dynamic localization in English (`en`), Hindi (`hi`), and Marathi (`mr`).

## Tech Stack
- **Framework**: React 18, Vite, TypeScript
- **Styling**: Tailwind CSS
- **Icons**: Lucide React
- **Animations & Effects**: Motion & Canvas Confetti
- **State Management**: React Context (`AgriContext`) with LocalStorage persistence

## Running Locally
```bash
npm install
npm run dev
```
The application will start on `http://localhost:3000`.
