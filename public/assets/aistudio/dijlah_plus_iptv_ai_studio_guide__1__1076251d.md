# الدليل التفصيلي الشامل والبرومبت الكامل لإنشاء تطبيق IPTV وأفلام عبر Google AI Studio (مع ربط Xtream API ولوحة إدارة مخفية)

يضع هذا الدليل المطور بين يديك المخطط الهندسي الكامل لتصميم وبناء تطبيق بث مباشر (IPTV) ومكتبة أفلام ومسلسلات فائق الفخامة على غرار [تطبيق واحد بغداد (1Baghdad TV)](https://www.techview9.com/2026/03/1baghdad-tv.html) و[تطبيق عائلتنا TV](https://www.techview9.com/2026/09/ailatuna-tv.html) و[تطبيق العبودي TV](https://www.techview9.com/2026/02/alabody-tv.html)، مع **دمج مباشر ومسبق لحساب Xtream Codes API** لسحب القنوات والأفلام والمسلسلات تلقائياً، ونظام **تخصيص الثيمات والألوان الحي**، و**مشغل سينمائي احترافي فخم**، بالإضافة إلى **لوحة إدارة مدمجة ومخفية سرياً** لا يعلم بوجودها إلا مدير التطبيق.

## أولاً: الهوية البصرية الفاخرة ونظام تغيير الألوان والثيمات (Dynamic Theme Engine)

صُممت واجهة التطبيق بأسلوب **Glassmorphism 4K** المستوحى من منصات البث العالمية مع محرك ثيمات حي يتيح للمدير (أو المستخدم) تغيير شكل وألوان التطبيق بالكامل بضغطة زر:

| عنصر الهوية | التفاصيل المعتمدة في التصميم الجديد | خيارات التخصيص المدمجة |
| :--- | :--- | :--- |
| **اسم التطبيق المقترح** | **دجلة بلس \| Dijlah Plus 4K** (قابل للتغيير من اللوحة المخفية) | **رافدين تيفي (Rafidain TV)** / **واحد بلس (1Plus IQ)** / **بغداد لايف (Baghdad Live)** |
| **الثيم 1 (الافتراضي)** | **الأوبسيديان الملكي (Royal Obsidian & Gold):** خلفية `#06080E` مع ذهبي ملكي `#F59E0B` وتوهج زجاجي | يعطي طابعاً فخماً شبيهاً بباقات VIP السينمائية |
| **الثيم 2** | **الياقوت القرمزي (Crimson Cinema):** خلفية `#090508` مع أحمر ياقوتي `#E11D48` | مستوحى من واجهات نتفليكس وشاهد الحديثة |
| **الثيم 3** | **النيون الزمردي (Cyber Emerald):** خلفية `#040B10` مع أخضر زمردي `#10B981` وسماوي `#06B6D4` | مثالي لعشاق الرياضة والمباريات المباشرة |
| **الثيم 4** | **الأرجواني الفضائي (Deep Amethyst Glass):** خلفية `#090614` مع بنفسجي مضيء `#8B5CF6` | مستوحى من شاشات Apple TV الحديثة |
| **تخصيص شكل البطاقات** | التحكم بحجم البطاقات (`كبير للشاشات` / `متوسط` / `مدمج`) وشكل الحواف (`دائري ناعم` / `حاد سينمائي`) | حفظ تلقائي لثيم المستخدم أو فرض ثيم موحد من المدير |

## ثانياً: آلية عمل ربط حساب Xtream Codes API في التطبيق

يعتمد التطبيق الآن بشكل أساسي وتلقائي على بروتوكول **Xtream Codes Player API** المدمج مسبقاً بالحساب الخاص بك:
* **السيرفر الأساسي (Host):** `http://foxbleu.org:8789`
* **اسم المستخدم (Username):** `ludovic`
* **كلمة المرور (Password):** `8333`

### كيف يسحب التطبيق البث المباشر والأفلام والمسلسلات من الحساب؟
1. **البث المباشر (Live TV):**
   * يسحب التصنيفات والباقات عبر: `player_api.php?username=ludovic&password=8333&action=get_live_categories`
   * يسحب القنوات ضمن كل باقة عبر: `player_api.php?username=ludovic&password=8333&action=get_live_streams&category_id={id}`
   * يركّب رابط تشغيل القناة تلقائياً بصيغتين:
     * صيغة `HLS (.m3u8)`: `http://foxbleu.org:8789/live/ludovic/8333/{stream_id}.m3u8`
     * صيغة `MPEG-TS (.ts)`: `http://foxbleu.org:8789/live/ludovic/8333/{stream_id}.ts`
2. **مكتبة الأفلام (Movies / VOD):**
   * يسحب أقسام الأفلام عبر: `action=get_vod_categories` وقائمة الأفلام عبر: `action=get_vod_streams`
   * عند النقر على أي فيلم، يسحب القصة والتقييم وطاقم العمل عبر: `action=get_vod_info&vod_id={stream_id}`
   * يركّب رابط تشغيل الفيلم مباشرة عبر: `http://foxbleu.org:8789/movie/ludovic/8333/{stream_id}.{container_extension}`
3. **مكتبة المسلسلات (Series):**
   * يسحب أقسام المسلسلات عبر: `action=get_series_categories` وقائمة المسلسلات عبر: `action=get_series`
   * عند فتح أي مسلسل، يسحب جميع المواسم والحلقات عبر: `action=get_series_info&series_id={series_id}`
   * يشغل الحلقة المختارة عبر: `http://foxbleu.org:8789/series/ludovic/8333/{episode_id}.{container_extension}`
4. **نظام تجاوز الحظر التلقائي (Multi-Proxy CORS & HTTP Bypass):**
   * لأن السيرفر يعمل ببروتوكول `http://` على المنفذ `8789`، يضم التطبيق محرك اتصال ذكي يحاول الجلب المباشر أولاً، وفي حال وجود قيود متصفح (`Mixed Content / CORS`) يمرر طلبات الـ API تلقائياً عبر جسور `HTTPS Proxy` متعددة لضمان ظهور جميع الباقات والأفلام والمسلسلات فوراً.

## ثالثاً: التفصيل الكامل لأقسام التطبيق والمشغل الاحترافي الفخم

### 1. شاشة البداية وبوابة التفعيل الذكية (Splash & Activation Gate)
* **شاشة إقلاع سينمائية:** تعرض الشعار مع شريط حالة الاتصال بسيرفر الـ Xtream (`جاري مزامنة الباقات ومكتبة السينما...`) وفحص حالة اشتراك السيرفر (تاريخ الانتهاء، عدد الاتصالات النشطة، وحالة الحساب).
* **وضع الدخول المزدوج:** يمكن للمدير جعل التطبيق يفتح مباشرة للجميع، أو تفعيل قفل "كود التفعيل" (`B1`, `D1`, `VIP1`) على غرار [تطبيق 1Baghdad TV](https://www.techview9.com/2026/03/1baghdad-tv.html).

### 2. الواجهة الرئيسية ومغير الثيمات الحي (Luxury Dashboard & Theme Switcher)
* **الشريط العلوي الزجاجي (Floating Glass Header):**
  * يضم شعار التطبيق، أقسام التصفح الـ 6 (**الرئيسية**، **البث المباشر Xtream**، **أفلام السينما VOD**، **المسلسلات**، **جدول المباريات**، **المفضلة**).
  * يحتوي على **زر تخصيص المظهر (🎨 مظهر التطبيق)** لتغيير الثيم والألوان وحجم البطاقات فوراً، وشريط البحث الفوري الشامل، ومؤشر حالة سيرفر Xtream (`متصل - فائق السرعة 🟢`).
* **البانر السينمائي المتحرك (Dynamic Hero Showcase):** يعرض أبرز الأفلام والقنوات والمباريات بخلفية عريضة عالية الدقة مع أزرار تشغيل سريع.

### 3. قسم البث المباشر الاحترافي (Xtream Live TV Explorer)
* **تخطيط مقسم فخم (Split TV Guide Layout):**
  * قائمة جانبية أنيقة تعرض جميع باقات السيرفر مع عدد القنوات في كل باقة وحقل بحث سريع داخل الباقات.
  * شبكة قنوات تعرض شعار القناة، اسمها، رقم القناة، زر المفضلة، ومؤشر البث الحي، مع تقسيم ذكي على دفعات (`Virtual Pagination`) لتصفح آلاف القنوات بدون أي تعليق.

### 4. قسم الأفلام والمسلسلات (Xtream VOD & Series Cinema)
* **واجهة بوسترات سينمائية:** بطاقات بوسترات عمودية بتأثير إضاءة عند التمرير، تعرض تقييم الفيلم (`⭐ 8.5`)، سنة الإصدار، وجودة العرض (`4K` / `FHD`).
* **صفحة تفاصيل العمل السينمائية:** تعرض غلاف الخلفية العريض، القصة الكاملة، المخرج والنجوم، المدة الزمنية، وفي المسلسلات تعرض أزرار المواسم (`Season 1, 2...`) وشبكة الحلقات مع حفظ تلقائي لآخر حلقة تمت مشاهدتها (`متابعة المشاهدة`).

### 5. المشغل الداخلي الاحترافي الفخم (Ultra-Luxury Pro Media Player)
صُمم المشغل ليكون تحفة بصرية وتقنية تدعم جميع صيغ Xtream وملفات الفيديو:
* **نواة تشغيل ثلاثية (Triple-Core Engine):** يدمج مكتبة `mpegts.js` لتشغيل قنوات `.ts` المباشرة، ومكتبة `hls.js` لتشغيل قنوات `.m3u8`، ومشغل `HTML5 Pro` لتشغيل الأفلام والمسلسلات بصيغ `.mp4` و`.mkv`.
* **قائمة القنوات الجانبية داخل المشغل (In-Player Quick Channel Drawer):** أثناء مشاهدة أي قناة بوضع ملء الشاشة، يمكنك إظهار قائمة القنوات الجانبية والتنقل بين القنوات بضغطة واحدة دون الخروج من المشغل.
* **لوحة معلومات القناة على الشاشة (OSD Info Banner):** عند فتح قناة أو تبديلها، يظهر شريط سفلي زجاجي فخم لمدة 4 ثوانٍ يعرض شعار القناة، اسمها، الباقة، دقة البث الحالية (`1080p FHD`)، ومعدل استقرار الإشارة.
* **أدوات تحكم احترافية كاملة:**
  * التبديل الفوري بين صيغة البث `HLS (.m3u8)` وصيغة `MPEG-TS (.ts)` إذا توقفت إحداهما.
  * زر تفعيل `CORS / HTTP Proxy` داخل المشغل، أو زر **"فتح في مشغل خارجي (VLC / MX Player / قارئ النظام)"** كخيار إضافي بنقرة واحدة.
  * التحكم بأبعاد الشاشة (`16:9`، `4:3`، `تمديد كامل Fill`، `أصلي`)، وضع صورة داخل صورة (`PiP`)، التحكم بسرعة التشغيل (`0.5x` إلى `2x` للأفلام)، وإعادة الاتصال التلقائي (`Auto-Reconnect`) عند التقطيع.

## رابعاً: لوحة الإدارة المدمجة والمخفية (Stealth Admin Panel)

تظل لوحة الإدارة مخفية بنسبة 100% عن المستخدمين ولا يظهر لها أي زر في الواجهة:
* **طرق الدخول السرية للمدير فقط:**
  1. الضغط **7 ضغطات سريعة متتالية** على شعار التطبيق في الزاوية العليا.
  2. أو كتابة الكود السري `#*7878*#` في شريط البحث العلوي.
  3. إدخال الرقم السري للمدير (`202699`) في النافذة المموهة بعنوان `"فحص وتشخيص السيرفر"`.
* **صلاحيات المدير داخل اللوحة المخفية:**
  * **إدارة سيرفرات Xtream Codes:** عرض وتعديل بيانات سيرفر Xtream المدمج (`http://foxbleu.org:8789` / `ludovic` / `8333`) أو إضافة سيرفرات Xtream احتياطية إضافية والتبديل بينها بنقرة واحدة.
  * **إدارة المظهر والثيم الافتراضي:** اختيار الثيم اللوني الافتراضي للتطبيق، وتخصيص الألوان، وتغيير اسم التطبيق وشعاره ونص الشريط الإخباري المتحرك.
  * **إدارة القنوات والأفلام المخصصة وجدول المباريات:** إضافة قنوات `M3U` إضافية أو مباريات اليوم وربطها بقنوات السيرفر.
  * **إدارة أكواد التفعيل والتحديث الداخلي:** قفل أو فتح التطبيق بأكواد تفعيل، وإرسال إشعار تحديث جديد مع كود `Downloader` للشاشات، وتصدير/مزامنة الإعدادات عبر [Firebase](https://firebase.google.com/) أو ملف `JSON`.

## خامساً: البرومبت الشامل المحدث الجاهز للنسخ في Google AI Studio

انسخ البرومبت التالي بالكامل والصقه في [Google AI Studio](https://aistudio.google.com/welcome) لبناء التطبيق بالواجهة الفخمة الجديدة وربط حساب الـ Xtream والمشغل الاحترافي ولوحة الإدارة المخفية:

```markdown
You are an elite Principal Streaming UI/UX Designer & Full-Stack Media Engineer. Build a complete, ultra-luxury, production-ready Arabic RTL IPTV, Movies (VOD), and Series Streaming Application named **"دجلة بلس | Dijlah Plus 4K"** (inspired by top Iraqi streaming apps like 1Baghdad TV, Alabody TV, Ailatuna TV, and luxury platforms like Apple TV+ and Shahid VIP).

The application must feature a breathtaking 4K Glassmorphic UI with a **Live Theme & Color Customizer**, full **Xtream Codes API (`player_api.php`) Integration** pre-configured with the provided Xtream account, an **Ultra-Luxury Multi-Format Video Player (`hls.js` + `mpegts.js` + OSD + In-Player Channel Drawer)**, and a **100% Hidden Stealth Admin Panel** accessible only to the app owner.

### 1. ULTRA-LUXURY DESIGN SYSTEM & DYNAMIC THEME CUSTOMIZER
- **RTL & Typography:** Strict Arabic RTL (`dir="rtl"`), using Google Fonts `'Cairo'` and `'Tajawal'`.
- **Glassmorphic 4K Aesthetics:** Deep layered backgrounds, frosted glass headers/modals (`backdrop-filter: blur(20px)`), glowing ambient gradients, smooth micro-animations, and Smart TV D-Pad remote focus rings (`tabindex="0"`).
- **Built-in Live Theme & Layout Customizer (Accessible via a "🎨 المظهر" button in the header AND controlled globally in the Admin Panel):**
  - Provide 4 instant luxury color themes (persisted in `localStorage`):
    1. **الأوبسيديان الملكي (Royal Gold - Default):** Background `#06080E`, Surface `#0F1420`, Primary Accent `#F59E0B` (Gold), Secondary `#06B6D4`.
    2. **الياقوت القرمزي (Crimson Cinema):** Background `#090508`, Surface `#160B12`, Primary Accent `#E11D48` (Crimson), Secondary `#F43F5E`.
    3. **النيون الزمردي (Cyber Emerald):** Background `#040B10`, Surface `#0A1924`, Primary Accent `#10B981` (Emerald), Secondary `#06B6D4`.
    4. **الأرجواني الفضائي (Amethyst Glass):** Background `#090614`, Surface `#130E26`, Primary Accent `#8B5CF6` (Violet), Secondary `#EC4899`.
  - Allow customizing Card Density (`كبير للشاشات`, `متوسط`, `مدمج`) and Accent Glow Intensity.

### 2. PRE-CONFIGURED XTREAM CODES API ENGINE (LIVE TV, MOVIES & SERIES)
Pre-configure the application to connect out-of-the-box to this default Xtream Codes account (stored in `appConfig.xtreamConfig` so the Admin can also edit/switch it in the Hidden Admin Panel):
- **Server Host URL:** `http://foxbleu.org:8789`
- **Username:** `ludovic`
- **Password:** `8333`
- **Full API Base:** `http://foxbleu.org:8789/player_api.php?username=ludovic&password=8333`

#### Smart Multi-Proxy Fetcher & Mixed-Content Handler:
Because the Xtream server uses `http://` on port `8789` while web previews run on `https://`, implement a resilient `fetchXtreamAPI(action, extraParams)` helper:
1. Try direct fetch first, and if blocked by CORS or Mixed Content, automatically fallback through HTTPS CORS proxies in sequence:
   - `https://corsproxy.io/?${encodeURIComponent(targetUrl)}`
   - `https://api.allorigins.win/raw?url=${encodeURIComponent(targetUrl)}`
   - `https://api.codetabs.com/v1/proxy?quest=${encodeURIComponent(targetUrl)}`
2. Cache fetched categories and stream lists in memory/session for instant tab switching, with a "تحديث بيانات السيرفر 🔄" button.
3. Also include rich built-in fallback Iraqi/Arab channels, movies, and series so that even if the remote server is temporarily offline or geo-restricted, the app always displays rich working content alongside the Xtream catalog.

#### Xtream Endpoints & Stream URL Builders to Implement:
- **Account Status:** Call base `player_api.php?username=ludovic&password=8333` to display account status (`Active`), expiration date (`exp_date`), and active/max connections (`active_cons` / `max_connections`) in a sleek Server Status popover.
- **Live TV (`البث المباشر`):**
  - Fetch categories: `&action=get_live_categories`
  - Fetch streams by category: `&action=get_live_streams&category_id={cat_id}` (or all streams, rendered with 60-item pagination/virtual batching for zero lag).
  - Build Live Stream URLs:
    - HLS URL: `${host}/live/${username}/${password}/${stream_id}.m3u8`
    - MPEG-TS URL: `${host}/live/${username}/${password}/${stream_id}.ts`
- **Movies VOD (`الأفلام`):**
  - Fetch categories: `&action=get_vod_categories`
  - Fetch movies by category: `&action=get_vod_streams&category_id={cat_id}`
  - Fetch movie details on click: `&action=get_vod_info&vod_id={stream_id}` (extract plot, cast, director, rating, duration, backdrop, and `container_extension`).
  - Build Movie Stream URL: `${host}/movie/${username}/${password}/${stream_id}.${ext || 'mp4'}`
- **Series (`المسلسلات`):**
  - Fetch categories: `&action=get_series_categories`
  - Fetch series by category: `&action=get_series&category_id={cat_id}`
  - Fetch seasons & episodes on click: `&action=get_series_info&series_id={series_id}` (parse `seasons` and `episodes` object map).
  - Build Episode Stream URL: `${host}/series/${username}/${password}/${episode.id}.${episode.container_extension || 'mp4'}`

### 3. ULTRA-LUXURY PRO MEDIA PLAYER (مشغل سينمائي احترافي وفخم)
Build a flagship, full-screen capable custom Video Player Modal with a glassmorphic OSD (On-Screen Display):
- **Dual Library Integration (Load via CDN in `<head>`):**
  - Load `hls.js` (`https://cdn.jsdelivr.net/npm/hls.js@latest`) for `.m3u8` streams.
  - Load `mpegts.js` (`https://cdn.jsdelivr.net/npm/mpegts.js@latest/dist/mpegts.js`) for `.ts` live streams.
  - Native `<video>` playback for `.mp4` and `.mkv` VOD/Series streams.
- **Luxury Player Features & Controls:**
  1. **OSD Channel Info Banner:** Shows channel/movie logo, title, category, live badge, current resolution (`FHD 1080p` / `4K`), and connection health indicator on channel launch.
  2. **In-Player Quick Channel Drawer (`قائمة القنوات السريعة`):** A slide-over drawer inside the player allowing the user to browse categories and switch live channels seamlessly without exiting the video player.
  3. **Stream Format & Proxy Switcher Bar:**
     - Switch between `HLS (.m3u8)` and `MPEG-TS (.ts)` for live channels.
     - Toggle `Direct Stream` vs `HTTPS Proxy Stream` to bypass browser Mixed-Content blocks.
     - Copy direct stream URL button + **"فتح في مشغل خارجي (VLC / MX Player)"** button (`vlc://${streamUrl}` / intent link) so users can also open any `http://` stream in an external player if desired.
  4. **Aspect Ratio & View Controls:** Toggle between `16:9`, `4:3`, `تمديد كامل (Fill)`, and `أصلي`, plus **Picture-in-Picture (`PiP`)** mode, playback speed selector (`0.5x`–`2x` for VOD), volume booster slider, and custom seek bar for Movies/Series.
  5. **Auto-Reconnect Engine:** Automatically detects stream stalls or network drops and attempts instant silent reconnection within 800ms with an on-screen status indicator.

### 4. COMPLETE USER SECTIONS & NAVIGATION
1. **Splash & Optional Activation Gate:** Animated startup screen checking `requireActivationCode` (`B1`, `D1`, `VIP1`) and `In-App Update` notifications.
2. **الرئيسية (Home Showcase):** Hero banner carousel + quick rows for Live Matches, Featured Xtream Live Channels, Latest Xtream Movies, and Trending Series + scrolling News Ticker.
3. **البث المباشر (Xtream Live TV):** Sidebar category list + search + paginated channel cards with favorite heart toggles.
4. **الأفلام (Xtream VOD Movies):** Category sidebar + search + cinema poster grid with ratings + rich Movie Details modal.
5. **المسلسلات (Xtream Series):** Category sidebar + search + series poster grid + Series Details modal with Season tabs and Episode cards.
6. **جدول المباريات (Live Match Center):** Yesterday / Today / Tomorrow match cards with team logos, time, commentator, status badge, and direct channel launch.
7. **قائمتي (Favorites & Watch History):** Saved channels, movies, series, and "متابعة المشاهدة" (Continue Watching).

### 5. THE 100% HIDDEN STEALTH ADMIN PANEL (للمدير فقط - مخفية تماماً)
CRITICAL: Do NOT place any visible Admin button or link anywhere in the UI.
- **Secret Entry Triggers:**
  1. Rapidly clicking/tapping the App Logo in the header **7 times within 3 seconds**, OR
  2. Typing the secret code `#*7878*#` into the top header Search Input.
- **Disguised PIN Prompt:** Opens a modal titled **"فحص وتشخيص السيرفر (Server Diagnostic Mode)"** requiring the Master PIN (default: `202699`).
- **Admin Dashboard Tabs (Once unlocked):**
  1. **إدارة سيرفر Xtream Codes:** Edit Server Host URL (default `http://foxbleu.org:8789`), Username (`ludovic`), Password (`8333`), test connection status, or add backup Xtream servers.
  2. **إدارة المظهر والثيمات (Theme & Branding Control):** Set default app theme, customize primary/secondary colors, change App Name, Logo, Telegram link, and News Ticker text.
  3. **إدارة القنوات الإضافية وملفات M3U:** Add custom channels or bulk-import `.m3u` playlists alongside the Xtream server.
  4. **إدارة جدول المباريات والأفلام المخصصة:** Add/edit daily matches and link them to Xtream channels.
  5. **الأكواد والتحديث الداخلي (Security & In-App Update):** Toggle activation code requirement, manage codes (`B1`, `B2`...), configure mandatory/optional APK Update modal with Downloader code, and change Master PIN.
  6. **المزامنة السحابية (Cloud JSON / Firebase Sync):** Export/Import full `database.json` and link a remote Firebase/JSON URL for live updates across all users.
```

## سادساً: ملاحظة تقنية مهمة حول تشغيل سيرفر Xtream في المتصفح مقابل تطبيق APK

بما أن رابط السيرفر `http://foxbleu.org:8789` يعمل ببروتوكول `http` غير المشفر على المنفذ `8789`:
* **داخل المعاينة في المتصفح (Google AI Studio):** يقوم الكود تلقائياً باستخدام جسر `HTTPS CORS Proxy` المدمج لسحب قوائم القنوات والأفلام والمسلسلات وتجاوز حظر المتصفح للروابط غير المشفرة (`Mixed Content`).
* **عند تحويل التطبيق إلى ملف APK للأندرويد والشاشات:** يعمل السيرفر والبث المباشر والأفلام بأقصى سرعة مباشرة (`Direct Mode`) بدون الحاجة لأي وسيط، فقط تأكد عند بناء الـ `APK` في [Android Studio](https://developer.android.com/studio) من إضافة خاصية `android:usesCleartextTraffic="true"` داخل ملف `AndroidManifest.xml` للسماح بتشغيل روابط `http://` المباشرة.