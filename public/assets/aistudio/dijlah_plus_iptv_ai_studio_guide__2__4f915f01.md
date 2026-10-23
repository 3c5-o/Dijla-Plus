# الدليل التفصيلي الشامل والبرومبت الكامل لإنشاء تطبيق IPTV وأفلام عبر Google AI Studio (نسخة مطابقة لـ 13 شاشة من تطبيق 1 بغداد المطور + Xtream API + لوحة إدارة مخفية)

يضع هذا الدليل المكتمل بين يديك المخطط الهندسي الدقيق المبني على **جميع الشاشات الـ 13 الفعلية لتطبيق 1 بغداد (`v3.9` - نظام IBO Pro المطور)** التي أرفقتها، مع **ترقية الألوان والمظهر إلى تصميم 4K أكثر فخامة وحداثة (عبر زر "غير الواجهة أو التصميم")**، و**الربط المسبق بحساب الـ Xtream Codes الخاص بك** (`http://foxbleu.org:8789` / `ludovic` / `8333`)، و**لوحة الإدارة المدمجة المخفية كلياً للمدير فقط**.

## أولاً: الهوية البصرية المطورة ونظام الثيمات والألوان (أفخم من الشكل التقليدي)

يدعم التطبيق عبر زر **`غير الواجهة او التصميم`** داخل شاشة الإعدادات (أو من لوحة الإدارة المخفية) التبديل الحي بين **4 ثيمات 4K فخمة** وتغيير شكل البطاقات:

| الثيم اللوني | تدرج البطاقات والخلفية | الطابع البصري |
| :--- | :--- | :--- |
| **1. النحاسي الملكي المطور (Royal Bronze & Slate - الافتراضي)** | تدرج كحلي بترولي `#111E27` إلى ذهبي برونزي `#9A5B25` مع حواف مضيئة | يحافظ على روح [تطبيق 1 بغداد](https://www.techview9.com/2026/03/1baghdad-tv.html) مع وضوح وتباين أعلى |
| **2. الأوبسيديان الذهبي (VIP Gold & Obsidian)** | خلفية `#06080E` مع بطاقات `#111827` وتدرج ذهبي `#F59E0B` | طابع سينمائي فخم جداً للشاشات الكبيرة |
| **3. الياقوت القرمزي (Crimson Velvet)** | خلفية `#090508` مع تدرج أحمر ياقوتي `#E11D48` إلى كحلي داكن | مستوحى من منصات الأفلام العالمية |
| **4. النيون الرياضي (Cyber Emerald & Cyan)** | خلفية `#040B10` مع تدرج زمردي `#10B981` وسماوي `#06B6D4` | مثالي لمتابعة الدوريات والمباريات المباشرة |

## ثانياً: تفصيل جميع شاشات التطبيق (13 شاشة كاملة من البداية إلى النهاية)

### 1. شاشة تسجيل الدخول المزدوجة (`aaaaaaaaaaLogin` - الصور 12 و13)
تظهر عند فتح التطبيق لأول مرة أو عند الضغط على **`+ أضف قائمة التشغيل`**، وتتكون من بطاقة عمودية متدرجة بحواف دائرية تتوسط الشاشة وخلفها شعار التطبيق المموه:
* **ترويسة البطاقة:** تعرض عبارة الترحيب `اهلا بكم في بغداد الحضارة` (أو اسم تطبيقك) وتحتها `لاتنسوا متابعتنا على التلجرام @stlaet1`.
* **وضع الدخول الأول (اليوزر والباسورد - عندما يكون مربع `Activation code ☐` غير محدد):**
  * يعرض حقلين باللون الأبيض: حقل `username` (مع أيقونة مستخدم `👤` يميناً) وحقل `password` (مع أيقونة قفل `🔒` يميناً).
  * بمجرد كتابة `ludovic` و`8333` (أو تركهما مملوءين تلقائياً)، يتصل التطبيق مباشرة بسيرفر `http://foxbleu.org:8789`.
* **وضع الدخول الثاني (كود التفعيل السريع - عند تحديد علامة الصح `Activation code ☑`):**
  * يختفي حقلا اليوزر والباسورد تلقائياً ويتحولان إلى **حقل واحد فقط** باسم `Activation code` (مع أيقونة مفتاح `🔑` يميناً).
  * عند كتابة كود قصير مثل `1` أو `B1` والضغط على زر `LOGIN` الداكن، يربط التطبيق الكود تلقائياً بحساب الـ Xtream (`http://foxbleu.org:8789` / `ludovic` / `8333`) ويفتح القنوات والأفلام والمسلسلات فوراً.
* **أسفل البطاقة:** يعرض عنوان الـ MAC الخاص بالجهاز (`24:43:dd:c9:07:47`).

### 2. شاشة إدارة قوائم التشغيل وتفعيل الـ MAC (`ChangePlaylistActivity` - الصور 1 و2)
* **القسم الأيسر (بطاقات القوائم):**
  * يعرض الشعار بجانب كلمة `Playlist`، وتحته بطاقات الحسابات المحفوظة مثل بطاقة `1 🔑 Protected` مع شارة `Connected` البيضاء، وبطاقة `B1 🔑 Protected`، وبطاقة `+ أضف قائمة التشغيل` (تفتح شاشة `aaaaaaaaaaLogin`).
  * عند الضغط على أي بطاقة قائمة تشغيل، تفتح **نافذة ضبابية (الصورة 2)** تحتوي على 3 أزرار متدرجة فوق بعضها: **`يتصل`**، **`تعديل`**، و**`حذف`**.
* **القسم الأيمن (معلومات التفعيل):**
  * مفصول بخط أبيض عمودي، ويعرض زر `افتح الموقع`، وتحته `عنوان ماك` و`مفتاح الجهاز` باللون الذهبي، ورقم الإصدار `v3.9`.

### 3. الشاشة الرئيسية (`HomeActivity` - الصورة 3)
* **الشريط العلوي:** يعرض الشعار في الوسط، وفي اليمين تاريخ انتهاء الاشتراك المسحوب حياً من حساب الـ Xtream: `قائمة التشغيل الحالية تنتهي: ١٨/٠٦/٢٠٢٧`.
* **توزيع البطاقات الهندسي:**
  * **يساراً:** بطاقة **`بث مباشر`** الطولية الكبيرة بأيقونة التلفزيون والريموت الذهبية.
  * **العمود الثاني:** بطاقة **`افلام`** في الأعلى، وبطاقة **`Sports`** في الأسفل.
  * **العمود الثالث:** بطاقة **`مسلسلات`** في الأعلى، وبطاقة **`تغيير قائمة التسجيل`** في الأسفل.
  * **يميناً:** البانر الترويجي العريض في الأعلى، وتحته 3 أزرار مربعة: **الإعدادات (`⚙️`)**، **تحديث السيرفر (`🔄`)**، و**الخروج (`🚪`)**.

### 4. شاشة البث المباشر ثلاثية الأعمدة (`LiveMobileActivity` - الصورة 4)
* **الشريط العلوي:** `مسكن` | `بث مباشر` (نشط بالذهبي) | `افلام` | `مسلسلات` | حقل البحث البيضاوي `🔍` | الشعار.
* **الأعمدة الـ 3 المتجاورة:**
  1. **عمود الباقات (يسار):** يعرض `ALL` مع إجمالي القنوات (`15885`)، `المفضلات`، `Lock`، ثم باقات الـ Xtream (`BeIN Sports`, `الدوري السعودي`, `Shahid sport`, `AL KASS`, `AD Sport`...) مع عداد القنوات.
  2. **عمود القنوات (وسط):** القنوات مرقمة تسلسلياً (`27`, `28`...) مع شعار القناة المصغر واسمها ونجمة المفضلة الذهبية `★`.
  3. **عمود المشغل المصغر (يمين):** نافذة فيديو تشغل القناة المختارة مباشرة (وتكبر لملء الشاشة عند النقر عليها)، وتحتها اسم القناة و3 أزرار: **`القبض`** | **`إزالة من المفضلة`** | **`بحث`**.

### 5. مشغل البث المباشر الكامل (`Live Fullscreen Player OSD` - الصورة 5)
* **الشريط العلوي:** زر الرجوع `↩` يساراً، وفي اليمين 5 أيقونات (`▤` دليل البرامج، `♡` المفضلة، `🔒` قفل اللمس، `🔍` البحث، `⛶` تغيير الأبعاد).
* **الأشرطة الجانبية والمنتصف:** شريط **الإضاءة العمودي (`☀️`)** يساراً، وشريط **الصوت العمودي (`🔊`)** يميناً، وأزرار القناة السابقة `⏮` وإيقاف/تشغيل `⏸` والقناة التالية `⏭` في الوسط.
* **الشريط السفلي:** شعار القناة ورقمها واسمها يساراً، وشارة البث والدقة الحية **`🔴 LIVE | 1920x1080`** أسفل اليمين.

### 6. شاشات تفاصيل الفيلم والمسلسل (`MovieInfoActivity` & `SeriesInfoActivity` - الصور 6 و8)
* خلفية كاملة من غلاف العمل، مع البوستر العمودي يساراً يخترق **شريطاً أفقياً متدرجاً** يضم أزرار: **`▶ بداية / مشاهدة الموسم`**، **`🎬 المقطع الدعائي`**، و**`☆`**.
* أسفل الشريط: اسم العمل، سنة الإنتاج والتصنيف، أسماء الممثلين، التقييم بالنجوم الذهبية (`★★★★☆`)، مدة العرض، تاريخ الإضافة، القصة الكاملة بالعربية، وشريط صور **`ممثلين / Show Cast`** في الأسفل.

### 7. شاشة المواسم والحلقات (`SeasonActivity` - الصورة 9) ومشغل الأفلام والمسلسلات (الصور 7 و10)
* **شاشة المواسم:** عمود جانبي للمواسم (`الموسم 1` بالذهبي، `الموسم 2`...) وقسم رئيسي يعرض بطاقات الحلقات المستطيلة مع رقم الحلقة بالأحمر ورمزها (`S01E01`).
* **مشغل الأفلام والمسلسلات:** يضم في الأعلى أيقونات **الترجمة (`▤`)** و**المسار الصوتي (`♪`)** وأبعاد الشاشة `⛶`، وفي الوسط أزرار **تأخير وتقديم 10 ثوانٍ (`↺ 10` و `↻ 10`)**، مع أشرطة الإضاءة والصوت العمودية، وعرض الترجمة العربية وشريط التقدم الزمني في الأسفل.

### 8. شاشة الإعدادات الشاملة ذات الـ 20 خياراً (`SettingActivity` - الصورة 11)
تفتح عند الضغط على زر الترس `⚙️` في الشاشة الرئيسية، وتضم **شبكة من 4 أعمدة × 5 صفوف (20 بطاقة إعدادات متدرجة)** مطابقة للصورة 11:
* **الصف الأول:** `أضف قائمة التشغيل` | `مراقبة اهلية (قفل أبوي 🔒)` | `تغيير قائمة التسجيل` | `تغيير اللغة (عربي/إنجليزي)`
* **الصف الثاني:** **`غير الواجهة او التصميم (تغيير الثيم والألوان وشكل البطاقات ⊞)`** | `إخفاء الفئات الحية 👁️‍🗨️` | `إخفاء الفئات Vod 👁️‍🗨️` | `إخفاء فئات السلسلات 👁️‍🗨️`
* **الصف الثالث:** `Clear History Channels 🗑️` | `افلام التاريخ واضحة 🗑️` | `سلسلة مسح التاريخ 🗑️` | `نوع البث المباشر (ترتيب القنوات A-Z)`
* **الصف الرابع:** `Live Stream Format (تبديل صيغة البث بين m3u8 و ts)` | `Change Player` | `اختر مشغل وسائط خارجي (VLC / MX)` | `تلقائي (تشغيل تلقائي)`
* **الصف الخامس:** `تنسيق الوقت (12/24 ساعة)` | `إعدادات الترجمة (حجم ولون الخط)` | `Select Device Type (شاشة TV / موبايل)` | `تحديث الان (جلب آخر تحديث للسيرفر والتطبيق)`
* **تذييل الإعدادات في الأسفل:** يعرض `عنوان ماك: 24:43:dd:c9:07:47` و`مفتاح الجهاز: 136115`.

## ثالثاً: البرومبت الهندسي الشامل والنهائي (مطابق للصور الـ 13 + الثيمات الفخمة + حساب Xtream + اللوحة المخفية)

انسخ البرومبت التالي بالكامل والصقه في [Google AI Studio](https://aistudio.google.com/welcome) لبناء التطبيق بجميع شاشاته الـ 13 ولوحة إدارته المخفية:

```markdown
You are a Principal Streaming UI/UX Architect & Senior Full-Stack Media Engineer. Build a complete, production-ready, landscape/responsive Arabic RTL IPTV, Movies (VOD), and Series Streaming Application modeled directly after all **13 screens of the 1Baghdad TV v3.9 (`com.abo_ali` IBO Pro Custom Layout)** app, upgraded with a **4K Luxury Theme & Layout Customizer**, pre-configured **Xtream Codes API integration**, a **Pro Triple-Core Media Player (`hls.js` + `mpegts.js` + HTML5)**, and a **100% Hidden Stealth Admin Panel**.

### 1. PRE-CONFIGURED XTREAM CODES API & MULTI-PROXY ENGINE
Connect the application automatically out-of-the-box to this default Xtream Codes account (also linked to Activation Codes `1` and `B1`, and editable in the Hidden Admin Panel):
- **Server Host:** `http://foxbleu.org:8789`
- **Username:** `ludovic`
- **Password:** `8333`
- **Full API Endpoint:** `http://foxbleu.org:8789/player_api.php?username=ludovic&password=8333`

#### Smart Multi-Proxy Fetcher (CORS & Mixed-Content Bypass):
Implement `fetchXtream(action, extraParams)` that attempts a direct fetch first, and if blocked by browser Mixed-Content/CORS (`http://` on port `8789`), automatically falls back through:
1. `https://corsproxy.io/?${encodeURIComponent(url)}`
2. `https://api.allorigins.win/raw?url=${encodeURIComponent(url)}`
3. `https://api.codetabs.com/v1/proxy?quest=${encodeURIComponent(url)}`
Include rich built-in fallback channels (`TOD`, `beIN Sports`, `Shahid Sport`, `AL KASS`, `AD Sport`, `MBC Iraq`), movies (`Escape Plan 2013`, etc.), and series (`Lost` with 6 seasons, etc.) so every screen is 100% interactive immediately even if a proxy is slow.

#### Xtream API Actions & Stream URL Builders:
- **Account Info:** Fetch base `player_api.php` to extract `user_info.exp_date` and display `قائمة التشغيل الحالية تنتهي: ١٨/٠٦/٢٠٢٧` in the Home header.
- **Live TV:** `action=get_live_categories` & `action=get_live_streams`. Build stream URLs according to the `Live Stream Format` setting (`m3u8` default or `ts`):
  - HLS: `${host}/live/${username}/${password}/${stream_id}.m3u8`
  - MPEG-TS: `${host}/live/${username}/${password}/${stream_id}.ts`
- **VOD Movies:** `action=get_vod_categories`, `action=get_vod_streams`, and `action=get_vod_info&vod_id=${id}`. Stream URL: `${host}/movie/${username}/${password}/${stream_id}.${ext || 'mp4'}`.
- **Series:** `action=get_series_categories`, `action=get_series`, and `action=get_series_info&series_id=${id}`. Stream URL: `${host}/series/${username}/${password}/${episode_id}.${ext || 'mp4'}`.

### 2. COMPLETE 13-SCREEN ARCHITECTURE (EXACT MATCH TO SCREENSHOTS + LUXURY UPGRADES)

#### Screen 1 & 2: Dual-Mode Login Modal/Screen (`aaaaaaaaaaLogin`)
- Centered vertical rounded gradient card over a dark smoky backdrop with the app logo softly visible above it:
  - Header text: `اهلا بكم في بغداد الحضارة` (editable by Admin) and `لاتنسوا متابعتنا على التلجرام @stlaet1`.
  - **Checkbox toggle: `Activation code ☐`:**
    - **When Unchecked (`☐`):** Show two white input boxes: `username` (with user icon `👤` on right, pre-filled or ready for `ludovic`) and `password` (with lock icon `🔒` on right, pre-filled or ready for `8333`), plus an optional Server Host field.
    - **When Checked (`☑`):** Collapse the two inputs into a **single white input box**: `Activation code` (with key icon `🔑` on right). Entering `1`, `B1`, `B2`, or `D1` automatically maps to the pre-configured Xtream server (`http://foxbleu.org:8789` / `ludovic` / `8333`).
  - Dark `LOGIN` button + bottom device MAC address `24:43:dd:c9:07:47`.

#### Screen 3 & 4: Playlist Manager & MAC Screen (`ChangePlaylistActivity`)
- **Left Panel (70%):** App Logo + `| Playlist` header; gradient cards for active playlists (`1 🔑 Protected` with white `Connected` badge, `B1 🔑 Protected`, and `+ أضف قائمة التشغيل` which opens the `aaaaaaaaaaLogin` card). Bottom welcome & WhatsApp text.
- **Right Panel (30% separated by vertical line):** `افتح الموقع` gradient pill button, `تفعيل ماك`, `عنوان ماك: 24:43:dd:c9:07:47` and `مفتاح الجهاز: 136115` in gold, and `v3.9`.
- **Playlist Action Overlay:** Clicking a playlist card opens a blurred overlay with 3 stacked gradient pill buttons: **`يتصل`**, **`تعديل`**, **`حذف`**.

#### Screen 5: Main Home Dashboard (`HomeActivity`)
- **Header:** Top-right shows `قائمة التشغيل الحالية تنتهي: ١٨/٠٦/٢٠٢٧`; Top-center shows the App Logo.
- **Bento Grid Layout:**
  - **Left Tall Card:** **`بث مباشر`** (golden TV & remote icon).
  - **Middle-Left Column (2 Square Cards):** Top **`افلام`** (golden film reel); Bottom **`Sports`** (golden soccer ball with waves — opens Live Sports & Match Center).
  - **Middle-Right Column (2 Square Cards):** Top **`مسلسلات`** (golden clapperboard/strip); Bottom **`تغيير قائمة التسجيل`** (golden server rack — opens `Playlist` screen).
  - **Right Column:** Upper wide promotional banner card + Lower row of **3 square gradient icon buttons**: **Settings (`⚙️` - opens `SettingActivity`)**, **Refresh Xtream (`🔄`)**, and **Exit/Logout (`🚪`)**.

#### Screen 6: 3-Column Live TV Split Screen (`LiveMobileActivity`)
- **Top Bar:** `مسكن` (Home) | `بث مباشر` (gold active) | `افلام` | `مسلسلات` | Rounded Search input `🔍` | App Logo.
- **3-Column Split Body:**
  - **Col 1 (Categories - Left):** `ALL` (with total channel count), `المفضلات`, `Lock`, and all non-hidden Xtream Live Categories with channel counts.
  - **Col 2 (Channels - Middle):** Numbered channel rows (`27`, `28`...) + mini logo + Channel Name + gold favorite star `★`.
  - **Col 3 (Mini Preview Player - Right):** Live embedded player playing the selected channel (`click/tap video to expand to Full-Screen OSD`), channel title below, and 3 bottom pill buttons: **`القبض`** (Reconnect/Catchup) | **`إزالة من المفضلة / إضافة للمفضلة`** | **`بحث`**.

#### Screen 7: Full-Screen Live TV Player OSD (`Live Fullscreen Player`)
- **Top Bar:** Back arrow `↩` on left; 5 icons on right: EPG `▤`, Favorite `♡`, Lock `🔒`, Search `🔍`, Aspect Ratio `⛶`.
- **Side Sliders:** Left vertical **Brightness Slider (`☀️`)**; Right vertical **Volume Slider (`🔊`)**.
- **Center Controls:** Previous Channel `⏮` | Play/Pause `⏸` | Next Channel `⏭`.
- **Bottom OSD:** Left shows Channel Logo + Number & Name + EPG text; Bottom-right shows the glass badge **`🔴 LIVE | 1920x1080`** (with quick stream format `.m3u8`/`.ts` & Proxy switcher).

#### Screen 8 & 10: Movie Details (`MovieInfoActivity`) & Series Details (`SeriesInfoActivity`)
- Dimmed full-screen backdrop + left vertical Poster Card overlapping a **full-width horizontal gradient action bar** containing:
  - Movies: **`▶ بداية`** | **`🎬 المقطع الدعائي`** | **`☆`**.
  - Series: **`▶ مشاهدة الموسم`** | **`🎬 المقطع الدعائي`** | **`☆`**.
- Below bar: Title, Year & Genre tags, Cast line, Gold Star Rating (`★★★★☆`) + Duration, Date Added, Arabic Synopsis paragraph, and bottom **`ممثلين / Show Cast`** photo strip.

#### Screen 11: Series Seasons & Episodes Split Screen (`SeasonActivity`)
- **Left Sidebar:** Back arrow `↩` + vertical list of Seasons (`الموسم 1` highlighted in gold, `الموسم 2`...).
- **Right Panel:** Series title + vertical stack of wide Episode Cards with Red Episode Number (`1`, `2`...), thumbnail/logo, and `S01E01` title.

#### Screen 9 & 12: VOD Movie & Series Episode Player (`MovieMobilePlayer` & `SeriesMobilePlayer`)
- **Top Bar:** Back arrow `↩` | Centered Movie Title or Episode Code (`S01E01`) | Right icons: Info `ⓘ`, Favorite `♡`, **Subtitles (`▤`)**, **Audio Track (`♪`)**, Aspect Ratio `⛶`.
- **Side Sliders & Center:** Left Brightness `☀️`, Right Volume `🔊`, Center **Rewind 10s (`↺ 10`)**, Play/Pause `⏸`, **Forward 10s (`↻ 10`)**.
- **Bottom:** Centered Arabic subtitles overlay, timeline seekbar (`00:01` — `42:47`), and bottom chevron `⌄` for quick episode switching.

#### Screen 13: The 20-Card Settings Grid (`SettingActivity` - Exact 4x5 Grid)
Clicking `⚙️` on the Home screen opens `الإعدادات` with Back arrow `↩`, bottom MAC/Device Key footer, and **20 interactive gradient cards arranged in a 4x5 grid**:
1. `أضف قائمة التشغيل`: Opens the `aaaaaaaaaaLogin` modal.
2. `مراقبة اهلية`: Opens Parental Control PIN setup & category lock manager.
3. `تغيير قائمة التسجيل`: Navigates to `ChangePlaylistActivity`.
4. `تغيير اللغة`: Switches UI labels between Arabic and English.
5. **`غير الواجهة او التصميم` (Theme & UI Customizer):** Opens a luxury modal allowing the user to switch between **4 Color Themes** (`Royal Bronze`, `VIP Gold Obsidian`, `Crimson Velvet`, `Cyber Emerald`), card border style, and glow intensity!
6. `إخفاء الفئات الحية`: Checklist modal to hide/show specific Live TV categories.
7. `إخفاء الفئات Vod`: Checklist modal to hide/show specific Movie categories.
8. `إخفاء فئات السلسلات`: Checklist modal to hide/show specific Series categories.
9. `Clear History Channels`: Clears recently watched channels with confirmation toast.
10. `افلام التاريخ واضحة`: Clears watched movies history.
11. `سلسلة مسح التاريخ`: Clears watched series history.
12. `نوع البث المباشر`: Modal to sort channels (`الافتراضي`, `أبجدي A-Z`, `أبجدي Z-A`).
13. `Live Stream Format`: Modal to switch live playback format between `HLS (.m3u8)` and `MPEGTS (.ts)`.
14. `Change Player`: Switches internal player engine mode (`ExoPlayer / HLS Pro`, `MPEG-TS Low Latency`, `Native HTML5`).
15. `اختر مشغل وسائط خارجي`: Configures external player intent (`VLC Player`, `MX Player`, `System Player`).
16. `تلقائي`: Toggles auto-play last channel on startup & auto-reconnect on stall.
17. `تنسيق الوقت`: Switches between `12 ساعة (AM/PM)` and `24 ساعة`.
18. `إعدادات الترجمة`: Customizes subtitle font size, text color, and background opacity.
19. `Select Device Type`: Switches layout optimization between `TV / شاشة ذكية` and `Mobile / هاتف`.
20. `تحديث الان`: Re-fetches Xtream API categories/streams and checks for In-App APK updates.

### 3. THE 100% HIDDEN STEALTH ADMIN PANEL (للمدير فقط - مخفية تماماً)
CRITICAL: Keep **ZERO** visible Admin buttons anywhere in the app.
- **Secret Triggers:**
  1. Rapidly clicking/tapping the App Logo **7 times within 3 seconds** on the Home or Playlist screen, OR
  2. Typing `#*7878*#` into the Search bar, OR
  3. Clicking the `عنوان ماك` text at the bottom of `SettingActivity` **5 times rapidly**.
- **Disguised Prompt:** Opens a modal titled **"فحص وتشخيص السيرفر (Server Diagnostic Mode)"** requiring Master PIN `202699`.
- **Admin Dashboard Capabilities:**
  - Edit default Xtream Server credentials (`http://foxbleu.org:8789`, `ludovic`, `8333`), map Activation Codes (`1`, `B1`, `B2`...) to different Xtream servers or M3U URLs.
  - Set the default App Theme, customize App Name, Logo, Home Promo Banner, Login Welcome Text (`اهلا بكم في بغداد الحضارة`), Telegram handle (`@stlaet1`), and WhatsApp number.
  - Manage custom Live Channels, Movies, Series, and Mandatory/Optional In-App Update modal (with Downloader code), plus Export/Import full `JSON` database or link a remote Firebase/JSON sync URL.
```