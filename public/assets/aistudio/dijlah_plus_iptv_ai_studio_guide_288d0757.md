# الدليل التفصيلي الشامل والبرومبت الكامل لإنشاء تطبيق IPTV وأفلام عبر Google AI Studio (مع لوحة إدارة مخفية)

يضع هذا الدليل بين يديك المخطط الهندسي الكامل لتصميم وبناء تطبيق بث مباشر (IPTV) ومكتبة أفلام ومسلسلات احترافي على غرار [تطبيق واحد بغداد (1Baghdad TV)](https://www.techview9.com/2026/03/1baghdad-tv.html) و[تطبيق عائلتنا TV](https://www.techview9.com/2026/09/ailatuna-tv.html) و[تطبيق العبودي TV](https://www.techview9.com/2026/02/alabody-tv.html)، متبوعاً **بالبرومبت الهندسي الشامل (Master Prompt)** الجاهز للنسخ واللصق مباشرة داخل منصة [Google AI Studio](https://aistudio.google.com/welcome) لبناء التطبيق بالكامل مع **لوحة إدارة مدمجة ومخفية سرياً** لا يعلم بوجودها إلا مدير التطبيق.

---

## أولاً: الهوية المقترحة للتطبيق (الاسم، التصميم، والألوان)

لبناء علامة مميزة ومرتبة تعلق في ذهن المستخدم العراقي والعربي وتعمل بسلاسة على الهواتف والشاشات الذكية (Android TV / Google TV)، إليك الهوية المقترحة:

| عنصر الهوية | التفاصيل المقترحة | البدائل الإضافية |
| :--- | :--- | :--- |
| **اسم التطبيق المقترح** | **دجلة بلس \| Dijlah Plus TV** | **رافدين تيفي (Rafidain TV)** / **واحد بلس (1Plus IQ)** / **بغداد لايف (Baghdad Live 4K)** |
| **شعار التطبيق (Slogan)** | *عالمك للرياضة والسينما بدون تقطيع* | *كل القنوات والأفلام في شاشة واحدة* |
| **نظام الألوان (Theme)** | خلفية كحلي سينمائي داكن (`#0B0F19`)، بطاقات زجاجية (`#111827`)، لون تمييز ذهبي ملكي (`#F59E0B`) مع سماوي مضيء للبث المباشر (`#06B6D4`) | أسود كربوني (`#09090B`) مع أحمر قرمزي (`#E11D48`) على غرار منصات السينما |
| **الخط العربي المعتمد** | خط **Tajawal** أو **Cairo** (واضح جداً على الشاشات البعيدة والهواتف) | خط **Readex Pro** |
| **توافق الشاشات** | تصميم متجاوب يدعم اللمس للهواتف + التنقل بأزرار الريموت (`D-Pad Focus`) للشاشات الذكية | وضع عرضي (`Landscape`) افتراضي للشاشات وطولي/عرضي للهواتف |

---

## ثانياً: التفصيل الكامل لأقسام التطبيق (قسم بقسم من البداية إلى النهاية)

### 1. القسم الأول: شاشة البداية وبوابة التفعيل الذكية (Splash & Activation Gate)
* **شاشة الإقلاع (Splash Screen):** تعرض شعار **دجلة بلس** بتأثير نبض ضوئي مع شريط تحميل سريع يقرأ إعدادات السيرفر ويفحص وجود تحديث إجباري أو اختياري.
* **نظام الدخول المزدوج (يتحكم به المدير من اللوحة المخفية):**
  * **الوضع المفتوح (المجاني المباشر):** يفتح التطبيق مباشرة على الصفحة الرئيسية بدون أي كود (مثل [تطبيق عائلتنا TV](https://www.techview9.com/2026/09/ailatuna-tv.html)).
  * **وضع كود التفعيل (Activation Code):** يطلب من المستخدم إدخال كود قصير مثل `D1` أو `B1` أو `VIP1` (مثل [تطبيق 1Baghdad TV](https://www.techview9.com/2026/03/1baghdad-tv.html)) مع زر حفظ الكود تلقائياً لعدم طلبه مرة أخرى.
* **نافذة التحديث الداخلي (In-App Update Modal):** إذا أصدر المدير تحديثاً جديداً من اللوحة المخفية، تظهر نافذة أنيقة تعرض رقم الإصدار الجديد، مميزاته، رابط التحميل المباشر، وكود تطبيق `Downloader` للشاشات.

### 2. القسم الثاني: الصفحة الرئيسية وشريط التنقل (Home & Hero Dashboard)
* **الشريط العلوي (Top Navigation Bar):**
  * يضم شعار التطبيق في الزاوية اليمنى، وأزرار التنقل بين الأقسام الـ 5: **الرئيسية**، **جدول المباريات**، **البث المباشر (IPTV)**، **الأفلام**، **المسلسلات**، و**المفضلة**.
  * يحتوي في الجهة اليسرى على **شريط البحث الذكي الشامل**، ومؤشر حالة السيرفر (`متصل - مستقر 🟢`)، وزر الانضمام لقناة تيليجرام الرسمية.
* **شريط الأخبار والتنبيهات المتحرك (Live Ticker):** شريط إخباري أعلى الشاشة يمرر رسائل المدير (مثل: *"تم تحديث سيرفرات قنوات بي إن سبورت لمباراة الكلاسيكو بدون تقطيع"*).
* **البانر السينمائي المتحرك (Hero Banner):** يعرض أبرز مباراة جارية اليوم أو أحدث فيلم مضاف بدقة عالية مع زر **"شاهد البث الآن"** وزر **"إضافة للمفضلة"**.
* **صفوف الوصول السريع:** تعرض "مباريات جارية الآن"، "أكثر القنوات مشاهدة"، "أحدث الأفلام المضافة"، و"حلقات المسلسلات الجديدة".

### 3. القسم الثالث: صفحة جدول المباريات المباشر (Live Match Center)
* **تبويبات الأيام:** تقسيم المباريات إلى **مباريات الأمس**، **مباريات اليوم**، و**مباريات الغد**.
* **بطاقة المباراة الاحترافية:** تعرض اسم وشعار الفريق الأول ضد الفريق الثاني، توقيت المباراة، اسم البطولة (دوري أبطال أوروبا، الدوري الإسباني، الدوري العراقي...)، اسم المعلق، والقناة الناقلة.
* **حالة المباراة الذكية:** شارة حمراء نابضة `مباشر الآن 🔴` للمباريات الجارية، أو عداد تنازلي `تبدأ بعد ساعتين ⏳`، أو `انتهت النتيجة 2-1`.
* **التشغيل الفوري متعدد السيرفرات:** عند الضغط على أي مباراة، تفتح نافذة اختيار الجودة (`4K Ultra`، `FHD 1080p`، `HD 720p`، `SD للإنترنت الضعيف`) وتشتغل القناة الناقلة فوراً.

### 4. القسم الرابع: صفحة القنوات المباشرة والستلايت (Live IPTV & M3U Hub)
* **تصنيف الباقات الجانبي/العلوي:**
  * **باقة الرياضة (Sports):** قنوات beIN Sports بجميع الجودات، الكأس، الرياضية العراقية، SSC/ثمانية، أبو ظبي الرياضية، وألوان سبورت.
  * **باقة القنوات العراقية والعربية:** MBC العراق، الشرقية، السومرية، الرابعة، دجلة، UTV، وقنوات MBC وروتانا وOSN.
  * **باقة الستلايت العالمي (M3U Index):** قسم خاص يستورد قوائم `M3U` كاملة مع عرض سريع مقسم على دفعات (60 قناة في كل دفعة لمنع أي ثقل على الشاشات الضعيفة) وفلاتر حسب الدولة أو الفئة.
* **عرض الشبكة (Grid View):** بطاقات قنوات تعرض شعار القناة، اسمها، دقة البث (`1080p` / `4K`)، وزر القلب لإضافتها للمفضلة.

### 5. القسم الخامس: مكتبة الأفلام والمسلسلات (VOD Cinema & Series)
* **قسم الأفلام (Movies):** مصنف إلى (أفلام هوليوود مترجمة، أفلام عربية، أفلام هندية بوليوود، أفلام أنمي وكرتون، وأفلام أكشن ورعب).
* **قسم المسلسلات (Series):** مصنف إلى (مسلسلات عراقية، دراما عربية ورمضانية، مسلسلات تركية مترجمة ومدبلجة، دراما كورية وآسيوية).
* **صفحة تفاصيل الفيلم أو المسلسل:**
  * تعرض البوستر العريض، سنة الإنتاج، التقييم، نوع العمل، القصة الكاملة باللغة العربية، وزر التشغيل.
  * في المسلسلات: قائمة منسدلة لاختيار **الموسم (Season 1, 2...)** وشبكة أزرار لاختيار **الحلقة (الحلقة 1، الحلقة 2...)** مع تذكر آخر حلقة شاهدها المستخدم تلقائياً.

### 6. القسم السادس: المشغل الداخلي الذكي (Built-in Smart Player)
* **دعم الصيغ المتعددة:** يشغل روابط البث المباشر `HLS (.m3u8)` و`MPEG-TS (.ts)` وملفات الفيديو `MP4 / MKV / WebM` مباشرة داخل التطبيق دون الحاجة لأي مشغل خارجي.
* **نظام منع التقطيع (Auto-Reconnect Engine):** إذا انقطع اتصال السيرفر للحظة بسبب ضغط المشاهدين، يقوم المشغل بإعادة طلب حزم البث تلقائياً خلال أقل من ثانية دون إغلاق الشاشة، مع إمكانية التبديل التلقائي للسيرفر الاحتياطي (`Backup Stream`) أو تفعيل `CORS Proxy` لتجاوز حظر المتصفح/المصدر.
* **أدوات تحكم متكاملة:** أزرار لتبديل الجودة (`FHD / HD / SD`)، تغيير السيرفر (`سيرفر 1 / سيرفر 2 / سيرفر 3`)، التحكم بالصوت، ملء الشاشة، واختيار أبعاد الشاشة (`16:9` أو `تمديد كامل`).

---

## ثالثاً: آلية لوحة الإدارة المدمجة والمخفية (Stealth Admin Panel)

صُممت لوحة الإدارة لتكون **جزءاً مدمجاً داخل كود التطبيق نفسه** ولكنها **مخفية بنسبة 100%** بحيث يستحيل على أي مستخدم عادي رؤيتها أو اكتشافها:

### 1. كيف يدخل المدير إلى اللوحة المخفية؟ (3 طبقات حماية سرية)
1. **الطبقة الأولى (انعدام الأزرار):** لا يوجد في أي قائمة أو صفحة أو إعدادات داخل التطبيق أي زر اسمه "لوحة التحكم" أو "Admin" أو "إدارة".
2. **الطبقة الثانية (الحركة السرية المزدوجة - Secret Trigger):** تفتح بوابة التحقق السري بإحدى طريقتين لا يعرفهما إلا المدير:
   * **الطريقة أ (الضغطات السرية):** الضغط **7 ضغطات متتالية سريعة** (خلال أقل من 3 ثوانٍ) على **شعار التطبيق** في الزاوية العليا.
   * **الطريقة ب (كود البحث السري):** كتابة الرمز السري `#*7878*#` داخل **خانة البحث العادية** عن القنوات؛ يظن أي شخص يراقب الشاشة أنك تبحث عن قناة، لكن بمجرد كتابة آخر رمز تفتح بوابة المدير فوراً.
3. **الطبقة الثالثة (نافذة التحقق المموهة + الرقم السري Master PIN):**
   * عند تفعيل الحركة السرية، لا تظهر نافذة مكتوب عليها "دخول المدير"، بل تظهر نافذة مموهة بعنوان تقني: **`فحص تشخيص السيرفر (Server Diagnostic Code)`**.
   * تطلب النافذة إدخال **الرقم السري الخاص بالمدير** (الافتراضي: `202699` ويمكنك تغييره من داخل اللوحة). إذا أدخل شخص رقماً خاطئاً 3 مرات، تُقفل البوابة السرية لمدة 15 دقيقة.

### 2. ماذا يوجد داخل لوحة الإدارة المخفية؟ (تحكم كامل 100%)
بمجرد إدخال الرقم السري الصحيح، تفتح لوحة إدارة احترافية مقسمة إلى 6 تبويبات:
* **تبويب إدارة القنوات والباقات (Channels & M3U Manager):**
  * إضافة قناة جديدة (الاسم، الباقة، رابط الشعار، و3 روابط بث: سيرفر رئيسي `FHD`، سيرفر ثاني `HD`، وسيرفر ثالث `SD`) أو تعديل وحذف أي قناة موجودة.
  * **مستورد قوائم M3U الذكي:** لصق رابط `M3U` أو نص ملف `M3U` كامل ليقوم التطبيق تلقائياً باستخراج آلاف القنوات مع شعاراتها وتصنيفاتها بضغطة زر.
* **تبويب إدارة جدول المباريات (Match Schedule Manager):**
  * إضافة مباراة جديدة (اسم وشعار الفريق الأول، الفريق الثاني، البطولة، وقت المباراة، المعلق، حالة المباراة، وربطها بالقناة الناقلة أو وضع روابط بث خاصة بالمباراة).
* **تبويب إدارة الأفلام والمسلسلات (VOD & Series Manager):**
  * إضافة فيلم أو مسلسل جديد (العنوان، البوستر، التصنيف، سنة الإنتاج، التقييم، القصة، وروابط المشاهدة أو إضافة مواسم وحلقات متعددة للمسلسلات).
* **تبويب إدارة الأكواد والأمان (Activation Codes & Security):**
  * زر تشغيل/إيقاف مطالبة المستخدمين بكود التفعيل عند فتح التطبيق.
  * إنشاء أكواد تفعيل جديدة (مثل `B1`, `B2`, `VIP2026`) وتحديد تاريخ صلاحيتها أو إيقاف أي كود فوراً، وتغيير كود البحث السري والرقم السري للمدير (`Master PIN`).
* **تبويب الإعلانات والتحديث الداخلي (Ticker, Ads & In-App Update):**
  * تعديل نص الشريط الإخباري العاجل أعلى التطبيق، وتغيير رابط قناة التيليجرام واسم التطبيق وشعاره.
  * تفعيل إشعار **"تحديث جديد متوفر"** مع وضع رابط تحميل الـ `APK` الجديد وكود تطبيق `Downloader` للشاشات، وتحديد ما إذا كان التحديث اختيارياً أو إجبارياً (يمنع استخدام النسخة القديمة كما يفعل [فريق العبودي](https://t.me/s/stlaet1)).
* **تبويب المزامنة السحابية والنسخ الاحتياطي (Cloud Sync & JSON Export/Import):**
  * زر لتصدير كافة القنوات والمباريات والأفلام كملف `JSON`، أو ربط اللوحة مباشرة برابط [Firebase Realtime Database](https://firebase.google.com/) أو نقطة `API / JSON URL` بحيث ينعكس أي تعديل يجريه المدير عند جميع المستخدمين في نفس اللحظة.

---

## رابعاً: البرومبت الشامل الجاهز للنسخ في Google AI Studio

انسخ البرومبت التالي بالكامل والصقه في [Google AI Studio](https://aistudio.google.com/welcome) (يفضل استخدام وضع **Build / Web App** أو نموذج **Gemini 2.5 Pro / Gemini 3**):

```markdown
You are an elite Senior Full-Stack & Streaming Media Architect. Build a complete, production-ready, single-page Arabic RTL IPTV & VOD Streaming Application named **"دجلة بلس | Dijlah Plus TV"** (inspired by Iraqi streaming platforms like 1Baghdad TV, Alabody TV, and Ailatuna TV).

The application must be fully functional, visually stunning with a dark cinematic UI, responsive for both mobile phones and Android TV / Smart TV screens (including keyboard/D-Pad arrow navigation support), and must include a **100% Hidden Stealth Admin Panel** integrated directly into the app that no regular user can ever see or discover.

### 1. DESIGN, RTL TYPOGRAPHY & VISUAL IDENTITY
- **Direction & Font:** Strict Arabic RTL (`dir="rtl"`), using Google Fonts `'Cairo'` and `'Tajawal'`.
- **Color Palette:** Deep cinematic dark background (`#0B0F19`), elevated glassmorphic cards (`#111827` with subtle border `rgba(255,255,255,0.08)`), primary accent Royal Amber/Gold (`#F59E0B`), live badge pulse Crimson (`#EF4444`), and secondary accent Cyan (`#06B6D4`).
- **TV & Mobile UX:** Large focusable cards (`tabindex="0"` with clear gold focus ring for Smart TV remote navigation), smooth horizontal category carousels, and a responsive top navigation bar.

### 2. COMPLETE SECTION-BY-SECTION USER ARCHITECTURE
Build the following interconnected views/tabs seamlessly without page reloads:

1. **Splash & Conditional Activation Gate:**
   - Animated startup splash screen with the "دجلة بلس | Dijlah Plus TV" logo.
   - Check `appConfig.requireActivationCode`:
     - If `false` (default): Enter the main app directly.
     - If `true`: Show a sleek Activation Code screen (like 1Baghdad TV) asking the user for an activation code (pre-configure valid codes: `B1`, `B2`, `D1`, `VIP1`). Save valid activation in `localStorage`.
   - Check `appConfig.updateConfig`: If an update is active, show an In-App Update modal with version number, changelog, direct APK link, and TV Downloader Code (e.g., `3667884`), supporting both optional and mandatory (blocking) update modes.

2. **Top Header & Marquee News Ticker:**
   - Right side: App Logo & Title ("دجلة بلس TV") + Live Server Status badge (`متصل - سيرفرات مستقرة 🟢`).
   - Center Navigation Tabs:
     - `الرئيسية` (Home Dashboard)
     - `جدول المباريات` (Live Match Center)
     - `البث المباشر` (Live IPTV Channels)
     - `الأفلام` (Movies VOD)
     - `المسلسلات` (Series Library)
     - `قائمتي` (My Favorites)
   - Left side: Instant Search Input (`ابحث عن قناة، مباراة، فيلم، أو مسلسل...`) + Telegram Channel button.
   - Below Header: A smooth scrolling RTL News/Announcement Ticker displaying `appConfig.tickerText`.

3. **Home Dashboard (`الرئيسية`):**
   - Featured Hero Banner showcasing the top live match or featured movie with "شاهد الآن" and "إضافة للقائمة" buttons.
   - Quick Horizontal Rows: "مباريات اليوم الهامة", "القنوات الرياضية المباشرة", "أحدث الأفلام المضافة", and "المسلسلات الأكثر مشاهدة".

4. **Live Match Center (`جدول المباريات`):**
   - Filter tabs: `مباريات الأمس` | `مباريات اليوم` | `مباريات الغد`.
   - Match Cards showing Team A (name & logo) vs Team B (name & logo), League Name, Match Time, Commentator name, Channel name, and Status Badge (`مباشر الآن 🔴`, `قريباً ⏳`, or `انتهت`).
   - Clicking any match opens the Built-in Smart Player directly on the linked channel with multi-server/quality selection (`4K`, `FHD 1080p`, `HD 720p`, `SD`).

5. **Live IPTV & Satellite Hub (`البث المباشر`):**
   - Category Filter Pills: `الكل`, `قنوات رياضية`, `قنوات عراقية`, `قنوات MBC والترفيه`, `قنوات أخبار`, `أطفال وكرتون`, `ستلايت عالمي (M3U)`.
   - Channel Grid Cards with channel logo, title, quality badge (`FHD` / `4K`), category tag, and Favorite heart toggle.
   - Pagination / Load More batching (display 60 channels per batch) so even 10,000+ imported M3U channels render with zero lag.

6. **Movies (`الأفلام`) & Series (`المسلسلات`) VOD Library:**
   - Sub-genre filters: `الكل`, `أجنبي مترجم`, `عراقي وعربي`, `تركي`, `كوري وآسيوي`, `هندي بوليوود`, `أنمي`.
   - Clicking a Movie or Series opens a rich **Details Modal/View** with backdrop image, poster, IMDb rating badge, release year, genre tags, full Arabic synopsis, and a "تشغيل الآن" button.
   - For Series: Include a Season selector (`الموسم 1`, `الموسم 2`) and an Episode grid (`الحلقة 1`, `الحلقة 2`...) where clicking any episode plays its video stream in the Built-in Smart Player.

7. **Built-in Smart Video Player Modal (HLS / MPEG-TS / MP4 + Auto-Reconnect):**
   - Integrate `hls.js` (via CDN) + native HTML5 `<video>` fallback to play `.m3u8` (HLS), `.ts`, and `.mp4` streams directly inside the app.
   - **Multi-Server & Multi-Quality Switcher Bar** inside the player: Allow switching between `سيرفر 1 (FHD)`, `سيرفر 2 (HD)`, `سيرفر 3 (SD - للإنترنت الضعيف)`, plus a toggle for `تفعيل Proxy لتجاوز الحظر (CORS Proxy)` which routes the stream URL through a configurable CORS proxy when enabled.
   - **Auto-Reconnect Engine:** If a stream errors or stalls, display a subtle overlay (`جاري إعادة الاتصال بالسيرفر تلقائياً...`) and automatically retry/recover playback within 800ms without closing the player.

### 3. THE STEALTH HIDDEN ADMIN PANEL (للمدير فقط - مخفية تماماً)
CRITICAL REQUIREMENT: There must be **ZERO** visible buttons, links, icons, or mentions of an Admin Panel anywhere in the UI. Regular users must have no idea an admin panel exists.

Implement **Two Secret Entry Triggers** that open a disguised authentication modal:
- **Trigger 1 (Logo 7-Tap Gesture):** Clicking/tapping the App Logo in the top header **7 times rapidly within 3 seconds**.
- **Trigger 2 (Secret Search Code):** Typing the exact secret code `#*7878*#` (configurable in settings) into the main header Search Box.

When either trigger is activated:
- Open a disguised modal titled: **"فحص وتشخيص السيرفر (Server Diagnostic Mode)"** asking for an access code.
- Verify against the Master Admin PIN (default: `202699`). Lock out for 15 minutes after 5 failed attempts.

Once authenticated with the Master PIN, open the **Full Stealth Admin Dashboard** with 6 comprehensive management tabs:
1. **إدارة القنوات وملفات M3U (Channels & M3U Importer):**
   - Add, edit, or delete live channels (Name, Category, Logo URL, Quality badge, Server 1 FHD URL, Server 2 HD URL, Server 3 SD URL).
   - **Bulk M3U Parser & Importer:** Allow pasting raw `#EXTM3U` playlist text or fetching an `.m3u` URL to automatically parse `#EXTINF` tags (`tvg-logo`, `group-title`, channel name, and stream URL) and import them into the channel database.
2. **إدارة جدول المباريات (Matches Manager):**
   - Add, edit, or delete matches (Team 1 Name & Logo, Team 2 Name & Logo, League, Time, Commentator, Match Status, and linked Channel/Stream URLs).
3. **إدارة الأفلام والمسلسلات (VOD & Series Manager):**
   - Add, edit, or delete Movies and Series (Title, Type, Category, Year, Rating, Poster URL, Story Synopsis, Stream URLs, and dynamic Seasons/Episodes builder for Series).
4. **الأكواد والحماية (Activation Codes & Security):**
   - Toggle `مطالبة المستخدمين بكود تفعيل عند الدخول` (On/Off).
   - Add or revoke activation codes (`B1`, `B2`, `D1`, etc.).
   - Change the Secret Search Trigger Code (default `#*7878*#`) and change the Master Admin PIN (default `202699`).
5. **إعدادات التطبيق والتحديث الداخلي (App Branding, Ticker & In-App Update):**
   - Edit App Name, Logo URL, Telegram URL, and the scrolling News Ticker text.
   - Configure In-App Update notification: Toggle On/Off, Toggle Mandatory Update (blocks old app usage), Version Number, Update Message, Direct APK URL, and TV Downloader Code.
6. **المزامنة السحابية والنسخ الاحتياطي (Cloud Sync & JSON Database):**
   - Save all changes immediately to `localStorage` AND provide:
     - **Export Full Database (`تصدير ملف JSON`)**: Downloads the entire app state (config, channels, matches, movies, series, codes) as a `database.json` file.
     - **Import Database (`استيراد ملف JSON`)**: Restores or updates all content from a JSON file.
     - **Remote Cloud JSON / Firebase Sync URL**: Input field to link a remote JSON endpoint (e.g., Firebase Realtime Database `.json` URL or GitHub Gist raw URL) so the app automatically fetches live updates for all users on startup.

### 4. PRE-POPULATED REALISTIC DEMO DATA
Pre-populate the app with rich, realistic Iraqi & Arab sample data so every tab works immediately out of the box:
- At least 12 categorized channels (Sports, Iraqi channels like MBC Iraq, Al-Sharqiya, UTV, News, Entertainment) with working public test HLS `.m3u8` streams.
- 4 realistic today/tomorrow matches (e.g., Real Madrid vs Barcelona, Iraq vs Jordan, Arsenal vs Liverpool) linked to the sports channels.
- 6 Movies and 4 Series (with multiple seasons and episodes) with high-quality posters, Arabic descriptions, ratings, and working sample video streams.
```

---

## خامساً: كيف تحول التطبيق بعد إنشائه في AI Studio إلى ملف APK للهواتف والشاشات؟

بعد أن ينشئ لك [Google AI Studio](https://aistudio.google.com/welcome) التطبيق وتجربه أمامك، يمكنك إخراجه كملف **APK** يشتغل على جميع هواتف الأندرويد وشاشات التلفزيون الذكية (Android TV / Google TV) وكود `Downloader` بثلاث خطوات بسيطة:

1. **ربط التحديثات السحابية لجميع المستخدمين:**
   * من داخل لوحة الإدارة المخفية في تطبيقك (بعد الضغط 7 مرات على الشعار وإدخال `202699`)، اضغط على **"تصدير ملف JSON"**، وارفع هذا الملف على قاعدة بيانات مجانية في [Firebase Realtime Database](https://firebase.google.com/)، ثم ضع رابط القاعدة في خانة **Remote Cloud JSON URL** داخل التطبيق؛ بذلك أي قناة أو مباراة تضيفها من جوالك تظهر فوراً عند كل من ثبّت التطبيق.
2. **تحويل الكود إلى تطبيق APK مستقل:**
   * يمكنك تصدير ملفات المشروع من [Google AI Studio](https://aistudio.google.com/welcome) وتحويلها إلى حزمة `APK` عبر أداة **CapacitorJS** أو عبر مشروع [Android Studio](https://developer.android.com/studio) بضبط إعدادات الشاشات الذكية (`Leanback Launcher`) ليعمل بالريموت كونترول على الشاشات وباللمس على الهواتف.
3. **إنشاء كود Downloader للشاشات (مثل تطبيقات العبودي):**
   * ارفع ملف الـ `APK` النهائي على رابط تحميل مباشر (مثل GitHub Releases أو MediaFire Direct)، ثم ادخل إلى خدمة اختصار روابط تطبيق `Downloader` (`aftv.news`) وضع رابط الـ `APK` ليمنحك **كوداً رقمياً من 7 أرقام** يكتبه المستخدمون في شاشاتهم لتحميل تطبيقك مباشرة.
