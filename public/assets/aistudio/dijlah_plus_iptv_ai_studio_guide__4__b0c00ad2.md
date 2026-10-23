# الدليل النهائي الشامل والبرومبت الكامل لتطبيق "دجلة بلس | Dijla Plus" عبر Google AI Studio (النسخة العراقية الكاملة + حل جذري للصوت والمسلسلات + الاشتراك العائلي والـ VIP)

يقدم هذا الدليل النسخة النهائية المتكاملة لتطبيق **"دجلة بلس | Dijla Plus"**، المصممة بالكامل **باللغة العربية وبلهجة عراقية مرتبة وفخمة**، والمبنية بدقة على جميع الصور المرفقة (بما فيها شاشات الدخول وتسمية القائمة، وشاشات الأفلام والمسلسلات والبث المباشر بدون أرقام)، مع **حل جذري ونهائي لمشكلة عدم خروج الصوت ومشكلة عدم عمل حلقات المسلسلات**، ونظام إدارة متكامل يدعم **الأكواد المجانية (`D1`)** و**حسابات الـ VIP المحددة بالوقت (من ساعة واحدة إلى سنة)** و**قفل الـ MAC والاشتراك العائلي متعدد الأجهزة**.

## أولاً: هوية وأيقونة تطبيق "دجلة بلس | Dijla Plus" والواجهة العراقية المرتبة

| العنصر | التفاصيل المعتمدة في النسخة النهائية |
| :--- | :--- |
| **اسم التطبيق والهوية** | **دجلة بلس \| Dijla Plus** — بواجهة عربية عراقية مرتبة بالكامل (تم تعريب كافة الأزرار والإعدادات وإزالة الكلمات الأجنبية والركيكة مثل "مسكن" أو "افلام التاريخ واضحة" واستبدالها بعبارات عراقية/عربية فخمة مثل **"الرئيسية"**، **"مسح سجل الأفلام"**، **"أهلاً بيكم في دجلة بلس"**). |
| **أيقونة وشعار التطبيق الجديد** | أيقونة فخمة مدمجة (`SVG` ثلاثي الأبعاد داخل الكود) تجمع بين تموجات نهر دجلة باللون السماوي المضيء (`#00D2FF`) ومثلث التشغيل السينمائي والتاج الذهبي الملكي (`#D4AF37`) داخل درع كحلي زجاجي (`#050B14`). |
| **إخفاء جميع الأعداد (`بدون ذكر العدد`)** | تم إلغاء ظهور أرقام عدد القنوات أو عدد الأفلام أو عدد المسلسلات نهائياً من بجانب كلمة **`الكل`** ومن بجانب أسماء كافة الباقات والأقسام في البث المباشر والأفلام والمسلسلات. |
| **عرض كافة الأقسام والمحتوى بالكامل** | يسحب التطبيق ويعرض **جميع الأقسام والباقات** من السيرفر (مثل: أفلام إنكليزية، أفلام عربية، مسلسلات تركية، مسلسلات رمضانية، رياضة، إسلامية، أطفال... إلخ) ويعرض **جميع القنوات والأفلام والمسلسلات بالكامل** عبر التحميل التلقائي السلس أثناء التمرير بدون إخفاء أي عنصر. |

## ثانياً: الحل الجذري لمشكلتي الصوت وتشغيل حلقات المسلسلات

تم تشخيص ومعالجة السببين البرمجيين اللذين تسببا بغياب الصوت وعدم اشتغال حلقات المسلسلات في النسخة السابقة:

### 1. كيف تم حل مشكلة "الفيديو يعمل ولكن لا يوجد صوت"؟
* **تفعيل الصوت المباشر لحظة النقر (`User Gesture Audio Unlock`):** المتصفحات والشاشات تكتم الصوت تلقائياً إذا تم تشغيل الفيديو بعد طلب غير متزامن (`async`) أو إذا بقي وسم `muted` مفعلاً. في هذا البرومبت، يتم إجبار المشغل في نفس لحظة نقر المستخدم على القناة أو الفيلم على تنفيذ:
  `video.muted = false; video.removeAttribute('muted'); video.volume = 1.0;`
* **معالج المسارات الصوتية ومضخم الصوت (`Web Audio API + AudioTrack Unlocker`):** إضافة دائرة صوتية تفتح مسار الصوت تلقائياً حتى في القنوات والأفلام ذات الترميز الصوتي الخاص (`AAC / AC3 / MP3`)، مع وضع زر واضح داخل المشغل باسم **"تنشيط / رفع الصوت"** يضمن اشتغال الصوت بنسبة 100%.

### 2. كيف تم حل مشكلة "قسم المسلسلات عند تشغيل حلقة لا يعمل"؟
* **سبب المشكلة في Xtream API:** في سيرفرات الاكستريم، القنوات والأفلام تستخدم المتغير `stream_id`، بينما **حلقات المسلسلات داخل `get_series_info` تستخدم المتغير `id` (وليس `stream_id`)**! لذلك كان الكود السابق يطلب الرابط `.../series/user/pass/undefined.mp4` فلا تشتغل الحلقة.
* **الحل الجذري في البرومبت الجديد:**
  1. قراءة معرف الحلقة الصحيح عبر: `const epId = episode.id || episode.stream_id || episode.episode_id`.
  2. تنظيف امتداد الحلقة: `const ext = (episode.container_extension || 'mp4').replace(/^\./, '')`.
  3. بناء الرابط الصحيح: `${server.host}/series/${server.username}/${server.password}/${epId}.${ext}`، مع ميزة التبديل التلقائي الصامت إلى `.mp4` أو `.m3u8` إذا كانت الحلقة بصيغة `.mkv`، مما يضمن اشتغال جميع حلقات المسلسلات فوراً.

## ثالثاً: تسلسل صفحات التطبيق من البداية للنهاية (حسب الصور المرفقة)

### 1. شاشة البداية والأيقونة الجديدة -> شاشة تسجيل الدخول (`aaaaaaaaaaLogin`) -> نافذة تسمية القائمة
1. **شاشة البداية (Splash Screen):** تعرض أيقونة وشعار **دجلة بلس** الجديد بتأثير ذهبي فخم، ثم تنتقل مباشرة إلى شاشة تسجيل الدخول (إذا لم يكن المستخدم مسجلاً).
2. **شاشة تسجيل الدخول (مطابقة للصور 6 و8):**
   * بطاقة متدرجة فخمة في وسط الشاشة مكتوب أعلاها: **"أهلاً بيكم في دجلة بلس"** وتحتها **"تابعونا على التلغرام @Dijla_Plus"**.
   * **الوضع الافتراضي (يوزر وباسورد VIP):** يعرض حقل **`اسم المستخدم`** وحقل **`كلمة المرور`**، وتحتهما زر/خيار واضح **`الدخول بكود تفعيل`**، ثم زر **`تسجيل الدخول`**.
   * **وضع الكود:** عند الضغط على خيار **`الدخول بكود تفعيل`**، يتحول الحقلان فوراً إلى حقل واحد **`كود التفعيل`** (يكتب فيه مثلاً `D1`)، ثم يضغط **`تسجيل الدخول`**.
3. **نافذة تسمية قائمة التشغيل (مطابقة للصورة 10):**
   * فور نجاح التحقق من اليوزر/الباسورد أو الكود، تنبثق نافذة **"اسم قائمة التشغيل"** (*"إذا لم تختر اسماً للقائمة، سيتم تسميتها تلقائياً باسم دجلة بلس"*) مع حقل الإدخال وزري **`حفظ`** و**`خروج`**.

### 2. الشاشة الرئيسية (`HomeActivity`) ونافذة التلغرام والسلايدر الإعلاني
* **نافذة التلغرام المنبثقة عند كل دخول:** تظهر في كل مرة يدخل فيها المستخدم للتطبيق مع زر **`اشتراك في القناة`** ([https://t.me/Dijla_Plus](https://t.me/Dijla_Plus)) وزر **`إلغاء`**.
* **توزيع البطاقات الفخم (عربي كامل بدون أقسام زائدة):**
  * بطاقة **`البث المباشر`** الطولية الكبيرة يساراً.
  * بطاقة **`الأفلام`** وبطاقة **`الرياضة والمباريات`** في العمود الثاني.
  * بطاقة **`المسلسلات`** وبطاقة **`تغيير الحساب / القوائم`** في العمود الثالث.
  * **المربع الإعلاني المتحرك (يتغير كل 3 ثوانٍ تلقائياً)** في اليمين، وتحته الأزرار الـ 3 بأيقونات `SVG` ذهبية: **الإعدادات**، **تحديث المحتوى**، و**تسجيل الخروج**.
  * في أعلى اليمين: **`الاشتراك الحالي ينتهي في: [التاريخ والوقت]`**.

### 3. شاشات الأفلام (`MovieActivity`) والمسلسلات (`SeriesActivity`) والبث المباشر (`LiveMobileActivity`)
* **شاشة الأفلام (مطابقة للصور 2 و3) وشاشة المسلسلات (مطابقة للصورة 4):**
  * **الشريط العلوي:** `الرئيسية` | `البث المباشر` | `الأفلام` | `المسلسلات` | مربع البحث الفوري | شعار **دجلة بلس**.
  * **القائمة الجانبية (يسار):** أيقونة `SVG` ذهبية كبيرة في الأعلى (بكرة سينما للأفلام / شريط فيديو للمسلسلات)، تحتها زر **`عودة`** وزر **`بحث`**، ثم قائمة جميع الأقسام المسحوبة من السيرفر (`استئناف المشاهدة / شوهدت مؤخراً`، `الكل`، `المفضلة`، ثم كافة الأقسام مثل أفلام أجنبية، أفلام عربية، مسلسلات تركية، مسلسلات رمضانية، شاهد، نتفلكس... **بدون عرض أي أرقام بجانبها**).
  * **شبكة البوسترات (يمين):** زر الترتيب **`بحسب ما تم إضافته`** في الأعلى، وتحته شبكة من 5 أعمدة تعرض جميع الأفلام أو المسلسلات بالكامل مع شريط الاسم المتدرج أسفل كل بوستر (وإذا كان الفيلم بدون صورة من السيرفر، تظهر بطاقة شعار **دجلة بلس** الفخمة تلقائياً كما في الصور).

## رابعاً: نظام لوحة الإدارة المخفية الكامل (`2008`) — الأكواد المجانية، حسابات VIP بالمدد، قفل الجهاز، والاشتراك العائلي

تفتح لوحة الإدارة سرياً فقط عبر **الضغط 7 مرات سريعة على شعار "دجلة بلس"** أو كتابة `#*2008*#` في البحث، ثم إدخال كلمة المرور **`2008`**:

1. **إضافة حساب اكستريم مجاني بكود (مثل `D1`):**
   * يدخل الأدمن بيانات سيرفر الاكستريم (`Host`, `Username`, `Password`) ويعطيه كوداً مثل **`D1`** (مضبوط مسبقاً في الكود على سيرفرك `http://foxbleu.org:8789` / `ludovic` / `8333` تحت الكود `D1`).
2. **إنشاء وإدارة حسابات VIP (حساب اكستريم + يوزر وباسورد يحددهما الأدمن + المدة الزمنية):**
   * يختار الأدمن سيرفر الاكستريم (أو يدخل بياناته)، ثم يملأ **حقلين إضافيين خاصين بالمشترك**:
     * **اسم مستخدم المشترك (`VIP Username`)**
     * **كلمة مرور المشترك (`VIP Password`)**
   * **تحديد مدة الاشتراك بدقة من الأزرار الجاهزة:**
     * **`تجريبي 1 ساعة`** (ينتهي تلقائياً بعد 60 دقيقة بالضبط)
     * **`شهري (30 يوم)`**
     * **`3 أشهر (90 يوم)`**
     * **`6 أشهر (180 يوم)`**
     * **`سنة كاملة (366 يوم)`**
3. **نظام قفل الماك أدرس (MAC Lock) وميزة الاشتراك العائلي (تحديد عدد الهواتف):**
   * عند إنشاء أو تعديل أي حساب VIP أو كود في الإدارة، يحدد الأدمن **نوع الربط بالأجهزة**:
     * **قفل على جهاز واحد (اشتراك فردي):** يقدر الأدمن إما يكتب الـ `MAC Address` الخاص بجهاز المشترك يدوياً، أو يختار **"قفل تلقائي على أول جهاز يدخل"**؛ فلا يفتح الحساب على أي هاتف آخر نهائياً.
     * **اشتراك عائلي (تحديد عدد الهواتف المسموح بها من الإدارة):** يحدد الأدمن عدد الأجهزة المسموح بها لهذا الحساب (مثلاً: `2 هاتف`، `3 هواتف`، `4 هواتف`، `5 هواتف`، أو `بدون حد`). يسجل النظام عناوين الأجهزة التي دخلت، وبمجرد اكتمال العدد المحدد (مثلاً 3 هواتف للعائلة)، يمنع أي جهاز رابع من الدخول!
     * **منع الدخول برسالة حماية ذكية:** إذا حاول جهاز غير مسموح به الدخول بنفس الحساب، يرفض النظام دخوله ولا يعرض تفاصيل السيرفر.
     * **زر تصفير الأجهزة (`تصفير الـ MAC`):** بضغطة زر واحدة في لوحة الإدارة يمكن للأدمن مسح الأجهزة المسجلة للمشترك إذا اشترى هاتفاً جديداً.

## خامساً: البرومبت النهائي الشامل الجاهز للنسخ في Google AI Studio

انسخ هذا البرومبت الكامل والصقه في [Google AI Studio](https://aistudio.google.com/welcome) لبناء النسخة النهائية الكاملة من تطبيق **"دجلة بلس | Dijla Plus"**:

```markdown
You are an elite Principal Streaming Architect & Senior Full-Stack Media Engineer. Build the final, ultra-luxury, 100% Arabic (classy Iraqi/Modern Standard Arabic phrasing) RTL IPTV, Movies (VOD), and Series Streaming Application named **"دجلة بلس | Dijla Plus"**, strictly following the uploaded screenshots and solving all audio, series playback, category display, and subscription management requirements.

CRITICAL MANDATES — IMPLEMENT EVERY SINGLE REQUIREMENT WITH 100% PRECISION:

### 1. BRAND IDENTITY, NEW CUSTOM APP ICON & 100% IRAQI/ARABIC UI (ZERO EMOJIS, ZERO UNNEEDED SECTIONS)
- **App Name:** **"دجلة بلس | Dijla Plus"**.
- **Custom Dijla Plus App Icon & Logo (`<DijlaLogo />` SVG Component):**
  - Design a bespoke, ultra-luxury inline SVG App Icon & Logo reflecting "دجلة بلس": a glowing rounded royal shield (`#050B14` to `#0D253F`) bordered in metallic gold (`#D4AF37`), featuring a golden 3D Play emblem intertwined with two glowing turquoise Tigris river waves (`#00D2FF`) and a golden crown/plus star on top. Display this icon prominently on the Splash Screen, Login Card, Top Headers, and as the fallback poster on cards without images.
- **Colors:** Deep Tigris Midnight `#050A12`, Card Gradients `linear-gradient(135deg, #0D1E33 0%, #17324D 55%, #7C4D25 100%)`, Royal Gold borders/accents `#D4AF37` & `#F59E0B`, and Cyan highlights `#00D2FF`.
- **100% Arabic / Classy Iraqi Phrasing:** Translate every single label in the app into natural, polished Arabic/Iraqi phrasing:
  - Top Nav: `الرئيسية` (instead of مسكن), `البث المباشر`, `الأفلام`, `المسلسلات`.
  - Settings Cards (cleanly phrased): `إضافة حساب أو كود`, `القفل العائلي`, `تبديل الحسابات`, `إعدادات اللغة`, `تغيير مظهر التطبيق`, `إخفاء باقات البث المباشر`, `إخفاء أقسام الأفلام`, `إخفاء أقسام المسلسلات`, `مسح سجل القنوات`, `مسح سجل الأفلام`, `مسح سجل المسلسلات`, `ترتيب القنوات`, `صيغة البث المباشر`, `تغيير المشغل الداخلي`, `مشغل خارجي`, `التشغيل التلقائي`, `تنسيق الوقت`, `إعدادات الترجمة`, `نوع الجهاز (شاشة / موبايل)`, `تحديث القوائم الآن`.
- **ZERO EMOJIS:** Use ONLY clean inline SVG vector icons everywhere. Remove any unneeded extra sections not present in the screenshots.

### 2. EXACT STARTUP & LOGIN FLOW (MATCHING SCREENSHOTS 6, 8, AND 10)
1. **Splash Screen:** Shows the new **دجلة بلس** App Icon, title, and loading bar for 1.5s, then routes to the Login Screen (if no active session) or Home Screen.
2. **Login Screen (`aaaaaaaaaaLogin` - Screenshots 6 & 8):**
   - Centered vertical rounded bronze-slate gradient card over a dark smoky background with the large **دجلة بلس** icon behind the top edge.
   - Card Header: **"أهلاً بيكم في دجلة بلس"** and **"لاتنسوا متابعتنا على التلجرام @Dijla_Plus"**.
   - **Two Login Modes:**
     - **Default Mode (VIP Username & Password):** Two clean white input fields with dark icons on the right: `اسم المستخدم (Username)` and `كلمة المرور (Password)`.
     - **Activation Code Mode (`الدخول بكود تفعيل`):** A clear checkbox/button toggle (`الدخول بكود تفعيل / Activation code`). When checked/clicked, the two fields smoothly turn into a single white input field: `أدخل كود التفعيل (مثال: D1)` with a Key SVG icon on the right.
   - Dark **`تسجيل الدخول (LOGIN)`** button + bottom device MAC Address (e.g. `24:43:dd:c9:07:47` — generated uniquely per browser/device and saved in `localStorage`).
3. **Enter Playlist Name Modal (Screenshot 10):**
   - Immediately after valid login credentials or code (`D1`) are entered, display the exact modal from Screenshot 10:
     - Header: **`تسمية قائمة التشغيل (Enter Playlist Name)`**
     - Subtitle: **`إذا لم تختر اسماً للقائمة، سيتم تسميتها تلقائياً باسم دجلة بلس`**
     - White input field with placeholder `مثال: اشتراك دجلة بلس VIP` and user SVG icon on the right.
     - Two bottom buttons side-by-side: **`حفظ (SAVE)`** and **`خروج (EXIT)`**.
   - Clicking `حفظ` saves the playlist, enters `HomeActivity`, and shows the **Telegram Subscription Popup (`https://t.me/Dijla_Plus`)** with **`اشتراك في القناة`** and **`إلغاء`** buttons (which appears on every app entry).

### 3. DISPLAY ALL CATEGORIES & ALL ITEMS WITHOUT NUMBERS (SOLVE CATEGORY & LIST ISSUES)
- **NO ITEM COUNTS / NUMBERS:** NEVER show numbers/counts next to `الكل (ALL)`, `المفضلة`, or any category name in Live TV, Movies, or Series! Display only the clean category name (e.g. `الكل`, `أفلام إنكليزية 4K`, `مسلسلات تركية`, `مسلسلات رمضان`, `beIN Sports`, etc.).
- **SHOW ALL CATEGORIES & ALL ITEMS:**
  - Fetch and display **100% of categories** from the Xtream server (`get_live_categories`, `get_vod_categories`, `get_series_categories`).
  - When `الكل` or any specific category is selected, load all items from the Xtream server (`get_live_streams`, `get_vod_streams`, `get_series`) and render them smoothly using infinite scroll (`IntersectionObserver` loading 80 items at a time as the user scrolls) so **every single movie, series, and live channel on the server is accessible without any artificial cap**.
- **Exact Layout of Movies (`MovieActivity` - Screenshots 2 & 3) and Series (`SeriesActivity` - Screenshot 4):**
  - **Top Header:** `الرئيسية` | `البث المباشر` | `الأفلام` | `المسلسلات` | Rounded Search bar | **دجلة بلس** Logo.
  - **Left Sidebar (22% width):**
    - Top large golden SVG icon (Film Reel for Movies, Video Clapper/Strip for Series).
    - Action row below icon: `عودة ↩` on left, `بحث 🔍` on right (opens instant category/item filter).
    - Vertical scrollable list of all categories (**without numbers**): `استئناف للمشاهدة` (for Movies) / `شوهدت مؤخراً` (for Series), `الكل`, `المفضلة`, followed by all Xtream categories.
  - **Right Main Grid (78% width):**
    - Top bar with sort dropdown button **`بحسب ما تم إضافته ⌄`** (Added date / Rating / A-Z) and current category title (**without count numbers**).
    - **5-Column Poster Grid:** Vertical poster cards (`aspect-ratio: 2/3`) with the movie/series poster image (and if `stream_icon` / `cover` is empty or fails `onError`, automatically render a sleek gradient card with the **دجلة بلس** golden logo in the center, matching Screenshots 2–4!) and a bronze-gold gradient bottom title strip.

### 4. ROOT-CAUSE FIX FOR AUDIO (NO SOUND BUG) & SERIES EPISODE PLAYBACK BUG
Implement these exact engineering fixes so audio and series episodes work 100% of the time:

#### Fix A — Guaranteed Audio Playback (Solve "Video plays but NO sound"):
1. **Immediate Synchronous Unmute on User Click:** Inside every channel, movie, and episode click handler, immediately set:
   ```js
   videoElement.muted = false;
   videoElement.removeAttribute('muted');
   videoElement.volume = currentVolume || 1.0;
   ```
2. **Audio Track & Codec Recovery in `hls.js` and `mpegts.js`:**
   - In `hls.js`, on `Hls.Events.MANIFEST_PARSED` and `Hls.Events.AUDIO_TRACKS_UPDATED`, ensure an active audio track is selected (`if (hls.audioTracks.length > 0 && hls.audioTrack === -1) hls.audioTrack = 0;`) and force `videoElement.muted = false`.
   - In the Player OSD, include an Audio Track switcher (`♪`) and a one-click **"تنشيط الصوت 🔊"** button that resumes Web Audio `AudioContext` and unmutes/boosts audio if a browser policy muted it.
   - Never put `crossorigin="anonymous"` on `<video>` (which blocks audio/video on IPTV streams without CORS headers).

#### Fix B — Guaranteed Series Episode Playback (Solve "Series episodes do not play"):
1. When a user opens a Series (`SeriesInfoActivity` -> `SeasonActivity`), call `player_api.php?username=${u}&password=${p}&action=get_series_info&series_id=${series.series_id}`.
2. **Correctly parse `data.episodes`:** `data.episodes` in Xtream API can be an Object keyed by season number (`{"1": [...], "2": [...]}`) or an Array. Normalize all keys so clicking any season in the left sidebar (`الموسم 1`, `الموسم 2`...) displays all its episodes in the right panel (`S01E01`, `S01E02`...).
3. **CRITICAL EPISODE URL BUILDER:** In Xtream API `get_series_info`, each episode's stream ID is stored in **`episode.id`** (NOT `episode.stream_id`!). Always extract the episode ID and extension as:
   ```js
   const epId = episode.id || episode.episode_id || episode.stream_id;
   const rawExt = episode.container_extension || (episode.info && episode.info.container_extension) || 'mp4';
   const cleanExt = String(rawExt).replace(/^\./, '').trim() || 'mp4';
   const primaryUrl = `${server.host}/series/${server.username}/${server.password}/${epId}.${cleanExt}`;
   ```
4. **Automatic Format Fallback for Series & Movies:**
   - Play `primaryUrl` in `<video>`. If the stream fails to play within 3.5 seconds (for example if `cleanExt` is `mkv` and the browser prefers `mp4` or `m3u8`), automatically fallback in sequence to:
     - `${server.host}/series/${server.username}/${server.password}/${epId}.mp4`
     - `${server.host}/series/${server.username}/${server.password}/${epId}.m3u8` (via `hls.js`)
     - `${server.host}/series/${server.username}/${server.password}/${epId}.ts` (via `mpegts.js`)
   - Apply the exact same fallback chain for Movies (`${server.host}/movie/${server.username}/${server.password}/${vodId}.${cleanExt}`).

### 5. HIDDEN ADMIN PANEL (PASSWORD: `2008`) — FREE CODES (`D1`), VIP ACCOUNTS WITH DURATIONS, MAC LOCK & FAMILY MULTI-DEVICE SUBSCRIPTIONS
- **100% Hidden from users:** Open only via **7 rapid clicks on the "دجلة بلس" Logo**, OR typing `#*2008*#` in Search, OR **5 rapid clicks on `عنوان ماك` in Settings**, then entering Admin Password **`2008`**.
- **Complete Xtream & Subscriber Management System (Zero Fake Data):**
  Pre-configure the default Xtream Server (`http://foxbleu.org:8789` / `ludovic` / `8333`) internally and hide all server URLs/credentials from users. Provide these Admin tabs:

  1. **إدارة الأكواد المجانية والمدفوعة (Free & Promo Xtream Codes - e.g., `D1`):**
     - Admin enters Xtream Server details (`Host URL`, `Xtream Username`, `Xtream Password`) and assigns a short Activation Code (pre-configure **`D1`** as an active code linked to `http://foxbleu.org:8789` / `ludovic` / `8333`).
     - Admin can set duration, max devices, or toggle the code active/inactive.

  2. **إنشاء وإدارة حسابات VIP (VIP Accounts with Custom User/Pass, Durations, MAC Lock & Family Multi-Device):**
     - Admin form to create a VIP Account:
       - **بيانات سيرفر الاكستريم المربوط:** Select from saved Xtream servers OR enter `Host URL`, `Xtream Username`, `Xtream Password` (kept 100% hidden from the subscriber).
       - **حقلين إضافيين للمشترك (Subscriber Login Credentials):**
         1. `اسم مستخدم المشترك (VIP Username)` — entered by Admin.
         2. `كلمة مرور المشترك (VIP Password)` — entered by Admin.
       - **تحديد مدة الاشتراك (Subscription Duration Selector):**
         - `تجريبي 1 ساعة (1 Hour Trial)`
         - `شهري - 30 يوم (1 Month)`
         - `3 أشهر - 90 يوم (3 Months)`
         - `6 أشهر - 180 يوم (6 Months)`
         - `سنة كاملة - 366 يوم (1 Year)`
         (Automatically calculates exact expiration timestamp `expiresAt` and displays remaining days/hours in the Admin table and user header).
       - **نظام حماية الأجهزة والاشتراك العائلي (MAC Lock & Family Multi-Device Control):**
         - **تحديد الماك أدرس (Manual MAC Lock):** Optional field to lock the account to a specific device MAC address.
         - **تحديد عدد الأجهزة المسموح بها (Family Subscription Limit):** Dropdown/number input allowing the Admin to choose:
           - `جهاز واحد فقط (قفل تلقائي على أول جهاز)` (`maxDevices = 1`)
           - `اشتراك عائلي - جهازين (2 هاتف/شاشة)` (`maxDevices = 2`)
           - `اشتراك عائلي - 3 أجهزة` (`maxDevices = 3`)
           - `اشتراك عائلي - 4 أجهزة` (`maxDevices = 4`)
           - `اشتراك عائلي - 5 أجهزة` (`maxDevices = 5`)
           - `غير محدود (مفتوح)`
         - **Smart Login Enforcement (No misleading "Wrong Password" error on device limit!):**
           - When a subscriber logs in with their VIP Username & Password (or Code), verify:
             1) Credentials exist,
             2) Current time < `expiresAt` (if expired, show: *"انتهت صلاحية اشتراكك في دجلة بلس، يرجى التواصل مع الإدارة للتجديد"*),
             3) Device MAC check: If the current device MAC is not in `account.registeredMacs` and `account.registeredMacs.length >= account.maxDevices` (or doesn't match the locked MAC), **do NOT allow login** (and do not say wrong password—instead block access cleanly or show a dedicated device-limit notice), while providing a one-click **"تصفير الأجهزة (Reset MACs)"** button for the Admin in the table!

  3. **إدارة السلايدر الإعلاني (3-Second Auto-Rotating Home Banner):**
     - Manage the images in the Home screen promo box (`HomeActivity` right panel) which automatically transitions every **3 seconds (`3000ms`)**. Admin can add image URLs or upload images from device, reorder, or delete them.

  4. **إدارة قناة التلغرام والنافذة المنبثقة والإشعارات (`https://t.me/Dijla_Plus`):**
     - Edit the Telegram link (default `https://t.me/Dijla_Plus`), customize the entry popup message that appears on every app launch, send live broadcast messages to users, and change Admin Password (`2008`).
```