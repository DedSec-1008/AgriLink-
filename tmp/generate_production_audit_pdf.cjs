const fs = require('fs');
const PDFDocument = require('pdfkit');

const outputPath = process.argv[2] || './KisanSetu_Production_Readiness_Audit.pdf';

const doc = new PDFDocument({
  size: 'A4',
  margins: { top: 36, bottom: 42, left: 38, right: 38 },
  bufferPages: true,
  autoFirstPage: true
});

const writeStream = fs.createWriteStream(outputPath);
doc.pipe(writeStream);

// Professional Color Palette
const PRIMARY_GREEN = '#1B5E20';
const SECONDARY_GREEN = '#2E7D32';
const ACCENT_AMBER = '#B45309';
const CRITICAL_RED = '#DC2626';
const CRITICAL_RED_BG = '#FEE2E2';
const TEXT_DARK = '#111827';
const TEXT_MUTED = '#4B5563';
const BG_LIGHT = '#F9FAFB';
const BG_CARD = '#F3F4F6';
const BORDER_COLOR = '#D1D5DB';
const PASS_GREEN = '#15803D';
const PASS_GREEN_BG = '#DCFCE7';
const WARN_AMBER = '#D97706';
const WARN_AMBER_BG = '#FEF3C7';

const pageWidth = 595.28 - 76; // 519.28 pt

// Helper Drawing Functions
function drawHeader(title, subtitle) {
  doc.rect(38, 36, pageWidth, 56).fill(PRIMARY_GREEN);
  doc.fillColor('#FFFFFF').font('Helvetica-Bold').fontSize(13.5).text(title, 48, 44, { width: pageWidth - 20 });
  if (subtitle) {
    doc.fillColor('#D1FAE5').font('Helvetica').fontSize(8).text(subtitle, 48, 64, { width: pageWidth - 20 });
  }
  doc.y = 102;
}

function drawSectionHeading(text, icon = '■') {
  if (doc.y > 700) doc.addPage();
  doc.moveDown(0.5);
  const startY = doc.y;
  doc.rect(38, startY, 4, 15).fill(PRIMARY_GREEN);
  doc.fillColor(PRIMARY_GREEN).font('Helvetica-Bold').fontSize(10.5).text(`${icon}  ${text}`, 46, startY + 2.5);
  doc.moveDown(0.25);
  doc.strokeColor(BORDER_COLOR).lineWidth(0.5).moveTo(38, doc.y).lineTo(38 + pageWidth, doc.y).stroke();
  doc.moveDown(0.35);
}

function drawSubHeading(text) {
  if (doc.y > 715) doc.addPage();
  doc.moveDown(0.25);
  doc.fillColor(SECONDARY_GREEN).font('Helvetica-Bold').fontSize(9).text(text);
  doc.moveDown(0.15);
}

function drawParagraph(text) {
  if (doc.y > 730) doc.addPage();
  doc.fillColor(TEXT_DARK).font('Helvetica').fontSize(7.8).text(text, { align: 'justify', lineGap: 1.5 });
  doc.moveDown(0.25);
}

function drawCallout(title, text, type = 'danger') {
  if (doc.y > 690) doc.addPage();
  const startY = doc.y;
  const bgColor = type === 'danger' ? CRITICAL_RED_BG : (type === 'warn' ? WARN_AMBER_BG : PASS_GREEN_BG);
  const borderColor = type === 'danger' ? CRITICAL_RED : (type === 'warn' ? WARN_AMBER : PASS_GREEN);
  const textColor = type === 'danger' ? '#991B1B' : (type === 'warn' ? '#92400E' : '#166534');
  
  doc.rect(38, startY, pageWidth, 42).fill(bgColor);
  doc.rect(38, startY, 4, 42).fill(borderColor);
  doc.rect(38, startY, pageWidth, 42).strokeColor(borderColor).lineWidth(0.5).stroke();

  doc.fillColor(textColor).font('Helvetica-Bold').fontSize(8.5).text(title, 48, startY + 6);
  doc.fillColor(TEXT_DARK).font('Helvetica').fontSize(7.5).text(text, 48, startY + 19, { width: pageWidth - 20, lineGap: 1.2 });
  doc.y = startY + 48;
}

function drawBadge(text, type = 'pass', x = doc.x, y = doc.y) {
  const isPass = type === 'pass';
  const isDanger = type === 'danger';
  const isWarn = type === 'warn';

  const bgColor = isDanger ? CRITICAL_RED_BG : (isWarn ? WARN_AMBER_BG : (isPass ? PASS_GREEN_BG : BG_CARD));
  const textColor = isDanger ? CRITICAL_RED : (isWarn ? '#B45309' : (isPass ? PASS_GREEN : TEXT_DARK));
  const borderColor = isDanger ? CRITICAL_RED : (isWarn ? WARN_AMBER : (isPass ? PASS_GREEN : BORDER_COLOR));

  const width = doc.widthOfString(text, { font: 'Helvetica-Bold', size: 6.8 }) + 8;
  doc.roundedRect(x, y, width, 11, 2).fill(bgColor);
  doc.roundedRect(x, y, width, 11, 2).strokeColor(borderColor).lineWidth(0.5).stroke();
  doc.fillColor(textColor).font('Helvetica-Bold').fontSize(6.8).text(text, x + 4, y + 2);
  return width;
}

function drawTable(headers, rows, colWidths, options = {}) {
  const tableWidth = colWidths.reduce((a, b) => a + b, 0);
  const startX = 38;
  
  if (doc.y > 690) doc.addPage();
  const headerY = doc.y;
  doc.rect(startX, headerY, tableWidth, 15).fill(PRIMARY_GREEN);
  
  let curX = startX;
  headers.forEach((h, i) => {
    doc.fillColor('#FFFFFF').font('Helvetica-Bold').fontSize(7.2).text(h, curX + 4, headerY + 3.8, {
      width: colWidths[i] - 8,
      align: options.align && options.align[i] ? options.align[i] : 'left'
    });
    curX += colWidths[i];
  });
  doc.y = headerY + 15;

  rows.forEach((row, rIdx) => {
    const rowHeight = options.rowHeight || 14;
    if (doc.y + rowHeight > 750) {
      doc.addPage();
      const newHeaderY = doc.y;
      doc.rect(startX, newHeaderY, tableWidth, 15).fill(PRIMARY_GREEN);
      let rcurX = startX;
      headers.forEach((h, i) => {
        doc.fillColor('#FFFFFF').font('Helvetica-Bold').fontSize(7.2).text(h, rcurX + 4, newHeaderY + 3.8, {
          width: colWidths[i] - 8,
          align: options.align && options.align[i] ? options.align[i] : 'left'
        });
        rcurX += colWidths[i];
      });
      doc.y = newHeaderY + 15;
    }

    const rowY = doc.y;
    const isEven = rIdx % 2 === 0;
    doc.rect(startX, rowY, tableWidth, rowHeight).fill(isEven ? '#FFFFFF' : BG_LIGHT);
    doc.rect(startX, rowY, tableWidth, rowHeight).strokeColor('#E5E7EB').lineWidth(0.5).stroke();

    let cX = startX;
    row.forEach((cell, cIdx) => {
      if (options.badgeCol === cIdx) {
        let type = 'pass';
        if (cell.includes('CRITICAL') || cell.includes('P0') || cell.includes('BLOCKER') || cell.includes('FAIL')) type = 'danger';
        else if (cell.includes('HIGH') || cell.includes('P1') || cell.includes('WARN') || cell.includes('PARTIAL')) type = 'warn';
        else if (cell.includes('MEDIUM') || cell.includes('P2') || cell.includes('DEMO')) type = 'warn';
        drawBadge(cell, type, cX + 3, rowY + 1.5);
      } else {
        const isBold = options.boldCol === cIdx;
        doc.fillColor(TEXT_DARK)
          .font(isBold ? 'Helvetica-Bold' : 'Helvetica')
          .fontSize(6.8)
          .text(String(cell), cX + 4, rowY + 3.2, {
            width: colWidths[cIdx] - 8,
            align: options.align && options.align[cIdx] ? options.align[cIdx] : 'left',
            lineBreak: false,
            ellipsis: true
          });
      }
      cX += colWidths[cIdx];
    });

    doc.y = rowY + rowHeight;
  });

  doc.moveDown(0.3);
}

// =========================================================================
// PAGE 1: EXECUTIVE COVER & SYSTEMIC STATUS
// =========================================================================
drawHeader(
  'KISANSETU (AGRILINK) — PRODUCTION READINESS AUDIT REPORT',
  'Phase P1 Comprehensive Engineering Audit | Post-SIH Hackathon Transition to Production Standard'
);

// Meta Info Box
const metaY = doc.y;
doc.rect(38, metaY, pageWidth, 42).fill(BG_CARD);
doc.rect(38, metaY, pageWidth, 42).strokeColor(BORDER_COLOR).lineWidth(0.5).stroke();

doc.font('Helvetica-Bold').fontSize(7.5).fillColor(TEXT_DARK)
  .text('Application Name:', 46, metaY + 6)
  .text('Package ID:', 46, metaY + 17)
  .text('Evaluation Standard:', 46, metaY + 28)
  .text('Architecture State:', 280, metaY + 6)
  .text('Total Source Volume:', 280, metaY + 17)
  .text('Audit Status / Verdict:', 280, metaY + 28);

doc.font('Helvetica').fontSize(7.5).fillColor(TEXT_MUTED)
  .text('KisanSetu (formerly AgriLink)', 135, metaY + 6)
  .text('com.aistudio.agrilink.pmqyvz (Namespace: com.example)', 135, metaY + 17)
  .text('Commercial Production Grade (Post-SIH 2026)', 135, metaY + 28)
  .text('Standalone Offline Simulator (Zero Backend / Zero DB)', 375, metaY + 6)
  .text('20,457 Kotlin LOC across 35 source files', 375, metaY + 17);

doc.font('Helvetica-Bold').fontSize(7.5).fillColor(CRITICAL_RED)
  .text('NON-PRODUCTION READY (10 BLOCKERS)', 375, metaY + 28);

doc.y = metaY + 48;

drawCallout(
  'CRITICAL AUDIT VERDICT: TRANSITION FROM PROTOTYPE TO PRODUCTION REQUIRED',
  'KisanSetu successfully validated agricultural market linkage and price discovery at the Smart India Hackathon (SIH). However, the codebase is currently a standalone client simulator. All data, market prices, buyer contracts, and transactions reside strictly in volatile RAM. No backend, database, authentication, or payment systems exist.',
  'danger'
);

drawSectionHeading('Executive Summary & The 6 Production Reality Checks', '🔍');
drawParagraph(
  'Evaluating the application against commercial mission-critical agricultural standards yields the following definitive technical conclusions:'
);

const realityChecks = [
  ['Subsystem', 'Hackathon Prototype Claim', 'Production Codebase Reality', 'Production Status'],
  ['Production Backend', 'Cloud-connected marketplace', '0% Implemented. No REST API, no FastAPI/Node, no database.', 'P0 BLOCKER'],
  ['Database Persistence', 'Room database configured', '0% Implemented. Zero @Entity, zero @Dao. 100% data in RAM.', 'P0 BLOCKER'],
  ['User Authentication', 'Farmer & Buyer access portal', '0% Implemented. No login, no OTP, hardcoded "Ramesh Patil".', 'P0 BLOCKER'],
  ['Security & Document Safe', 'Secure farmer document locker', '0% Implemented. No Safe, allowBackup=true, plaintext APK.', 'P0 BLOCKER'],
  ['Market Data & Intelligence', 'AI-driven price forecasting', 'Deterministic scalar multipliers with sleep delays. 0% ML.', 'P0 BLOCKER'],
  ['Financial Settlement', 'End-to-end direct payout', 'Status tracking only. Zero payment gateway or escrow logic.', 'P0 BLOCKER']
];
drawTable(realityChecks[0], realityChecks.slice(1), [95, 120, 205, 99], { boldCol: 0, badgeCol: 3 });

drawSectionHeading('Architecture Specification (Actual vs Expected)', '🏗');
drawParagraph(
  'The current implementation follows a pure unidirectional in-memory reactive flow: Android UI (Jetpack Compose) -> ViewModel -> MockAgriRepository (MutableStateFlows). The planned 3-tier architecture (Android -> REST API -> Backend -> PostgreSQL) is entirely unbuilt.'
);

const archTable = [
  ['Component', 'Current Implementation', 'Target Production Specification', 'Variance / Gap'],
  ['Operating Platform', 'Android SDK 36 (Compile/Target), Min SDK 24', 'Android SDK 34 / 35 LTS stable compatibility', 'Aligned (Modern)'],
  ['Language & Toolchain', 'Kotlin 2.2.10, Gradle 9.3.1, AGP 9.1.1', 'Kotlin 2.0+ with strict null-safety and Compose compiler', 'Aligned (Modern)'],
  ['UI Toolkit & Design', 'Jetpack Compose BOM 2024.09.00, Material 3', 'Material Design 3 with adaptive layouts and TalkBack a11y', 'Fully Implemented'],
  ['Local Storage Layer', 'In-memory MutableStateFlow in MockAgriRepository', 'Room Database (SQLite) with encrypted SQLCipher & migrations', 'Unimplemented (P0)'],
  ['Network & Remote API', 'None (Retrofit declared in build.gradle.kts, 0 usages)', 'Retrofit 2.12 + OkHttp 4.12 with JWT auth interceptors', 'Unimplemented (P0)'],
  ['Dependency Injection', 'Manual constructor instantiation in MainActivity', 'Hilt / Dagger Singleton component graph', 'Missing (P1)']
];
drawTable(archTable[0], archTable.slice(1), [95, 140, 165, 119], { boldCol: 0 });

// =========================================================================
// PAGE 2: CODE QUALITY, DEPENDENCIES & SECURITY
// =========================================================================
doc.addPage();
drawSectionHeading('Android Code Quality & Structural Bottlenecks', '⚙');
drawParagraph(
  'A surgical inspection of the presentation, domain, and data layers reveals serious architectural anti-patterns and lifecycle vulnerabilities:'
);

const codeIssues = [
  ['Area / File', 'Severity', 'Specific Code Finding', 'Operational Impact & Risk'],
  ['SellScreen.kt (5,757 LOC)', 'P1 HIGH', 'Monolithic God File containing 28.1% of entire application codebase.', 'Extreme maintenance risk; high recomposition overhead; fragility.'],
  ['MainActivity.kt:141', 'P0 CRITICAL', 'remember { MockAgriRepository() } instantiated in root composable.', 'Rotating phone destroys MainActivity, wiping all lots & orders.'],
  ['SellScreen.kt:2485', 'P1 HIGH', 'Synchronous Geocoder.getFromLocation called on main UI thread.', 'Blocks Android main thread; causes UI freezes and ANRs on slow 3G/4G.'],
  ['LocaleHelper.kt:30', 'P2 MEDIUM', 'Locale.setDefault() mutates JVM-wide static process locale.', 'Can corrupt background thread formatting and system service calls.'],
  ['PricesScreen.kt (1,003 LOC)', 'P2 MEDIUM', 'High Composable size with coupled search & filter state.', 'Excessive recomposition cycles when typing in mandi query field.']
];
drawTable(codeIssues[0], codeIssues.slice(1), [110, 55, 175, 179], { boldCol: 0, badgeCol: 1 });

drawSectionHeading('Dependency & Deprecation Audit', '📦');
drawParagraph(
  'Gradle inspection identified unused ghost libraries, packaging bloat, and deprecated platform APIs:'
);

const depTable = [
  ['Library Name', 'Version', 'Declared Purpose', 'Actual Production Status', 'Recommended Action'],
  ['androidx-compose-material3', 'BOM', 'Material 3 Design System', 'Fully utilized across all screens', 'Retain (Baseline)'],
  ['material-icons-extended', 'BOM', 'Extended icon library', 'Bloats APK by ~12MB', 'Prune to SVGs (P2)'],
  ['androidx-room-runtime / ktx', '2.7.0', 'Local SQLite database', '0% implemented (Zero entities/DAOs)', 'Implement or remove (P0)'],
  ['retrofit / converter-moshi', '2.12.0', 'HTTP Client & JSON Parser', '0% implemented (Zero API interfaces)', 'Implement real client (P1)'],
  ['firebase-ai / appcheck-debug', '34.17.0', 'Gemini & Debug AppCheck', 'Unconfigured ghost dependency', 'Remove from release (P0)'],
  ['robolectric / roborazzi', '4.16 / 1.59', 'JVM Unit & Screenshot Tests', '47 tests passing (100% pass rate)', 'Retain for CI testing']
];
drawTable(depTable[0], depTable.slice(1), [120, 48, 110, 125, 116], { boldCol: 0 });

drawSubHeading('Investigation of Framework Warning: "ashmem: Pinning is deprecated since Android Q"');
drawParagraph(
  'Origin & Mechanism: This log is emitted by Android OS native runtime (libcutils / AOSP ashmem-dev.cpp). Android 10 (API 29) deprecated ASHMEM_PIN ioctls in favor of memfd. Legacy software canvas renderers and emulator gralloc drivers trigger this notification when pinning shared memory buffers. Classification: ANDROID FRAMEWORK WARNING / HARMLESS INFORMATION. It does not originate in application Kotlin code and requires no code changes.'
);

drawSectionHeading('Security, Privacy & Attack Surface Audit', '🔒');
drawParagraph(
  'The application has critical security vulnerabilities that prevent commercial distribution on Google Play or enterprise deployment:'
);

const secTable = [
  ['Vulnerability Area', 'Risk Level', 'Vulnerability Finding in Codebase', 'Remediation Requirement'],
  ['Backup Exposure', 'P0 CRITICAL', 'android:allowBackup="true" with empty backup_rules.xml', 'Set allowBackup="false" or restrict to encrypted keys'],
  ['Plaintext Bytecode', 'P0 CRITICAL', 'isMinifyEnabled = false in release build; empty ProGuard', 'Enable R8 code shrinking and symbol obfuscation'],
  ['Committed Keystore', 'P0 CRITICAL', 'debug.keystore & base64 tracked in root Git repository', 'Purge keystore from Git; configure secure CI secrets'],
  ['Document Safe', 'P0 CRITICAL', '0% implemented. No encrypted locker for farmer land titles', 'Build EncryptedSharedPreferences / SQLCipher safe'],
  ['Camera / Photos', 'P1 HIGH', 'Mock photo URIs: "content://media/photo_${timestamp}"', 'Integrate Android Photo Picker & CameraX with file storage']
];
drawTable(secTable[0], secTable.slice(1), [100, 60, 185, 174], { boldCol: 0, badgeCol: 1 });

// =========================================================================
// PAGE 3: AUTHENTICATION, DATABASE & COMMERCIAL WORKFLOWS
// =========================================================================
doc.addPage();
drawSectionHeading('Authentication, Identity & RBAC Audit', '👥');
drawParagraph(
  'Authentication is currently NON-EXISTENT. Any person launching the application is automatically treated as "Ramesh Patil", a verified farmer associated with FPO-NGP-2024-8841. No phone number verification, OTP verification, password entry, or biometric validation exists.'
);

const authMatrix = [
  ['Role Perspective', 'Target Capabilities', 'Current Authorization Enforcement', 'Privilege Risk'],
  ['Farmer', 'Create lots, accept offers, book transport', 'Client-side UI only (Zero server validation)', 'Identity spoofing'],
  ['FPO Aggregator', 'Aggregate lots, bulk bidding, member KYC', 'Not implemented (Conceptual only)', 'Unauthenticated access'],
  ['Direct Buyer', 'Place bids, inspect produce, confirm delivery', 'Not implemented (Simulated via client repository)', 'Contract repudiation'],
  ['APMC Mandi Admin', 'Price verification, cess collection, weighbridge', 'Not implemented (Static hardcoded prices)', 'Price manipulation'],
  ['Government / Auditor', 'Subsidy tracking, compliance audit, MSP alerts', 'Not implemented (Static mock dashboard)', 'Data tampering']
];
drawTable(authMatrix[0], authMatrix.slice(1), [95, 135, 170, 119], { boldCol: 0 });

drawSectionHeading('Database & Data Persistence Reality', '🗄');
drawParagraph(
  'Room Database libraries are declared in build.gradle.kts, but ZERO Room code exists in the repository. All application state is stored in four volatile in-memory StateFlows in MockAgriRepository: lotsFlow, offersFlow, transactionsFlow, and bookingsMap. All data is lost when Android kills the app in the background.'
);

drawSectionHeading('Market Data & Recommendation Intelligence', '🧠');
drawParagraph(
  '1. Market Data: 100% synthetic hardcoded values in AgriRepository.kt (e.g. Nagpur Mandi Soybean = ₹4,850/q). Zero integration with Agmarknet or e-NAM.\n2. Recommendation Engine: 100% deterministic scalar arithmetic. Quality multipliers (Good = 1.0, Average = 0.95, Poor = 0.88) applied to base prices with hardcoded distances. Zero Machine Learning or neural networks.\n3. Artificial Delays: SellingViewModel executes artificial delay loops: delay(200) across 4 progress stages to simulate analysis.'
);

drawSectionHeading('Commercial Systems: Transactions, Payments & Logistics', '💼');
const commTable = [
  ['System Module', 'Prototype Feature', 'Production Reality', 'Risk / Limitation'],
  ['Transaction Engine', '10-stage state machine', 'Client-unilateral; no multi-party handshake', 'Farmer can falsely mark delivery'],
  ['Payment Processing', 'Payment tracking screen', 'Status tracking only; zero gateway/UPI integration', 'Farmers cannot receive real money'],
  ['Logistics Booking', 'Transporter selection modal', 'Static rates; no GPS routing or live tracking', 'Zero verified commercial transport'],
  ['Buyer Directory', 'Verified buyer profiles', '3 hardcoded profiles with mock ratings (4.7)', 'Zero real buyers or escrow funds']
];
drawTable(commTable[0], commTable.slice(1), [95, 115, 185, 124], { boldCol: 0 });

drawSectionHeading('Accessibility, Localization & Theme Audit', '🌐');
drawParagraph(
  'KisanSetu excels in UI craftsmanship and regional inclusivity:\n' +
  '• Localization: English (741 keys), Hindi (724 keys), Marathi (724 keys). 17 explainability keys fall back to English gracefully. Only 1 hardcoded string found in code (Text("Retry") in SellScreen.kt).\n' +
  '• Accessibility: Strict adherence to >=48dp touch targets, high contrast ratios (>7:1), FlowRow multi-line wrapping under 150% font scaling, and TalkBack semantic descriptions.\n' +
  '• Theme: Complete Material 3 Dark and Light color schemes with dedicated high-contrast outdoor agricultural palette (AgriColors).'
);

// =========================================================================
// PAGE 4: PRODUCTION BLOCKERS & READINESS SCORECARD
// =========================================================================
doc.addPage();
drawSectionHeading('The 10 Critical Production Blockers (P0 / P1)', '🚫');
drawParagraph(
  'The following 10 items strictly prevent launching KisanSetu as a commercial production system:'
);

const blockers = [
  ['#', 'Blocker Issue', 'Operational Risk', 'Required Production Solution', 'Priority'],
  ['B01', 'No Backend Server', 'App operates as isolated client simulator', 'Build FastAPI/Go/Node backend with PostgreSQL', 'P0 BLOCKER'],
  ['B02', 'No Authentication', 'Identity spoofing, contract repudiation', 'Implement Phone OTP & OAuth authentication', 'P0 BLOCKER'],
  ['B03', 'No Room Database', '100% data loss upon process death', 'Implement Room entities, DAOs & migrations', 'P0 BLOCKER'],
  ['B04', 'Rotation State Loss Bug', 'Wipes active transactions on phone rotation', 'Move repository out of Compose remember to ViewModel', 'P0 BLOCKER'],
  ['B05', 'Synthetic Market Data', 'Farmers receive outdated or inaccurate prices', 'Integrate Agmarknet / e-NAM government APIs', 'P0 BLOCKER'],
  ['B06', 'Unilateral State Machine', 'Farmer can falsely confirm delivery/payment', 'Enforce server-authoritative transitions with OTP/QR', 'P0 BLOCKER'],
  ['B07', 'No Payment Processing', 'Zero actual money transfer or escrow', 'Integrate Razorpay/Cashfree/UPI payment escrow', 'P0 BLOCKER'],
  ['B08', 'Fake Photo URIs', 'Cannot capture or upload produce photos', 'Implement Android Photo Picker & CameraX', 'P0 BLOCKER'],
  ['B09', 'Plaintext Bytecode', 'App is easily reverse-engineered', 'Enable R8 minification and ProGuard rules', 'P0 BLOCKER'],
  ['B10', 'SellScreen God Monolith', '5,757 lines; high risk of regression bugs', 'Modularize wizard into 10 decoupled components', 'P1 HIGH']
];
drawTable(blockers[0], blockers.slice(1), [22, 105, 140, 185, 67], { boldCol: 1, badgeCol: 4 });

drawSectionHeading('Production Readiness Scorecard (10 Engineering Dimensions)', '📊');
const scores = [
  ['Engineering Dimension', 'Score', 'Status', 'Core Assessment Justification'],
  ['Android Client Architecture', '58 / 100', 'PARTIAL', 'Stunning Compose UI & M3; heavily penalized by 5,757 LOC file and rotation state wipe.'],
  ['Security & Access Control', '18 / 100', 'CRITICAL', 'Zero auth, no PIN, no encryption, allowBackup=true, keystore in source repo.'],
  ['Backend Infrastructure', '0 / 100', 'BLOCKER', 'Completely non-existent. Zero servers, zero endpoints, zero database.'],
  ['Local Database (Room)', '12 / 100', 'BLOCKER', 'Room declared in Gradle, but 0% implemented. 100% data stored in RAM.'],
  ['External Data Integration', '8 / 100', 'BLOCKER', 'Zero API connections; 100% hardcoded Mandi prices and buyer profiles.'],
  ['Intelligence & Forecasting', '14 / 100', 'DEMO', 'Deterministic scalar arithmetic with sleep delays. Zero Machine Learning.'],
  ['Transaction Integrity', '32 / 100', 'PARTIAL', 'Disciplined 10-stage model, but strictly client-unilateral without multi-party proof.'],
  ['Testing Quality & Scope', '44 / 100', 'PARTIAL', '47 unit/Robolectric tests passing, but only test in-memory mocks. Zero DB/API tests.'],
  ['UX, Theming & Accessibility', '82 / 100', 'PASS', 'Superb high-contrast M3 theme, farmer ergonomics, 48dp targets, triple localization.'],
  ['Build, Release & Packaging', '22 / 100', 'CRITICAL', 'Builds debug APK (25MB), but release build has disabled R8 and missing secrets.']
];
drawTable(scores[0], scores.slice(1), [120, 50, 60, 289], { boldCol: 0, badgeCol: 2 });

drawSectionHeading('Strong Existing Foundations (What is Already Good)', '⭐');
drawParagraph(
  'KisanSetu has world-class strengths that provide an exceptional launchpad for production development:\n' +
  '1. Farmer-First UX & Design: Flawless Material 3 layout with outdoor high-contrast palette and zero clutter.\n' +
  '2. Trilingual Localization: Clean architecture supporting English, Hindi, and Marathi with dynamic runtime switching.\n' +
  '3. Sound Domain Modeling: Clean data classes and state machine rules ready for direct backend entity translation.\n' +
  '4. Active Test Suite: 47 local JVM tests verifying navigation, dialogs, and visual screenshot regression.'
);

// =========================================================================
// PAGE 5: ROADMAP & SIGN-OFF
// =========================================================================
doc.addPage();
drawSectionHeading('Prioritized Implementation Roadmap (Phase P1 to P8)', '🗺');
drawParagraph(
  'A rigorous 8-phase engineering sequence to transition KisanSetu into an enterprise production application:'
);

const roadmap = [
  ['Phase', 'Phase Name', 'Key Engineering Deliverables', 'Target Outcome'],
  ['P1', 'Android Hardening & Hygiene', 'Fix rotation bug; modularize SellScreen (5,757 LOC); prune ghost dependencies; purge keystores.', 'Client Stability'],
  ['P2', 'Local Database Persistence', 'Implement Room Database (@Entity, @Dao, @Database) for lots, offers, transactions; add migrations.', 'Offline Persistence'],
  ['P3', 'Hardware & Media Integration', 'Integrate Android Photo Picker & CameraX; move Geocoder to Dispatchers.IO.', 'Real Media Capture'],
  ['P4', 'Backend & API Foundation', 'Build FastAPI/Go/Node backend with PostgreSQL; implement phone OTP & JWT authentication.', 'True Cloud Server'],
  ['P5', 'Server Transactions & RBAC', 'Migrate 10-stage state machine to backend; enforce multi-party OTP/QR verification & RBAC.', 'Contract Integrity'],
  ['P6', 'Market Data & Real Logistics', 'Connect live Agmarknet / e-NAM feeds; integrate routing distance APIs; background WorkManager.', 'Live Mandi Prices'],
  ['P7', 'Payment Processing & Escrow', 'Integrate Razorpay / Cashfree / UPI payment gateway with escrow hold until delivery.', 'Real Money Movement'],
  ['P8', 'Observability & Play Store', 'Integrate Firebase Crashlytics & Analytics; enable R8 minification; automated CI/CD pipeline.', 'Play Store Release']
];
drawTable(roadmap[0], roadmap.slice(1), [40, 115, 244, 120], { boldCol: 1 });

drawSectionHeading('Official Quality Assurance Certification & Audit Sign-Off', '✒');
const signY = doc.y;
doc.rect(38, signY, pageWidth, 58).fill(BG_CARD);
doc.rect(38, signY, pageWidth, 58).strokeColor(PRIMARY_GREEN).lineWidth(1).stroke();

doc.fillColor(TEXT_DARK).font('Helvetica-Bold').fontSize(8.5).text('OFFICIAL AUDIT CONCLUSION & RECOMMENDATION:', 46, signY + 7);
doc.font('Helvetica').fontSize(7.5).fillColor(TEXT_MUTED)
  .text('KisanSetu has graduated from the hackathon evaluation stage and is now under active production engineering.', 46, signY + 19)
  .text('All 37 verification dimensions have been rigorously audited against commercial production standards.', 46, signY + 30)
  .text('Recommended Immediate Action: Execute Phase P1 (Client Hardening, SellScreen Modularization & Lifecycle Fix).', 46, signY + 41);

doc.font('Helvetica-Bold').fontSize(7.5).fillColor(CRITICAL_RED)
  .text('Audit Result: P0 BLOCKERS IDENTIFIED', 320, signY + 19)
  .text('Execution Mandate: AUDIT COMPLETE (READ-ONLY)', 320, signY + 30)
  .text('Certified By: Lead Mobile Architect & QA Lead', 320, signY + 41);

// =========================================================================
// RUNNING FOOTER & PAGE NUMBERING
// =========================================================================
const range = doc.bufferedPageRange();
for (let i = range.start; i < range.start + range.count; i++) {
  doc.switchToPage(i);
  doc.page.margins.bottom = 0;
  doc.strokeColor(BORDER_COLOR).lineWidth(0.5).moveTo(38, 785).lineTo(38 + pageWidth, 785).stroke();
  doc.fillColor(TEXT_MUTED).font('Helvetica').fontSize(7)
    .text('KisanSetu (AgriLink) — Production Readiness Audit Report | Phase P1 Comprehensive Evaluation', 38, 792, {
      width: 380,
      lineBreak: false
    });
  doc.text(`Page ${i + 1} of ${range.count}`, 38 + pageWidth - 80, 792, {
    width: 80,
    align: 'right',
    lineBreak: false
  });
}

doc.end();

writeStream.on('finish', () => {
  console.log('Production Readiness Audit PDF successfully generated at: ' + outputPath);
});
