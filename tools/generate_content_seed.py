#!/usr/bin/env python3
"""Generates app/src/main/assets/content_seed.json — the curated, human-authored
content library. No AI is involved at runtime; this script is the only place
content is authored.

SCHEMA (v2, PRD v0.5 P0)
------------------------
Content is normalised into locale-independent facts and localised text, so
adding a language never duplicates ages, areas or image credits:

    activities[]        id, age range, area, infant_focus, session_minutes,
                        mess_level, image/credit/licence, provenance
    activity_texts[]    (activity_id, locale) -> title, summary, why_it_matters,
                        how_to_present, what_to_observe, common_mistakes,
                        materials_needed, homemade_alternative, supervision_note
    sensitive_periods[] id, age range
    sensitive_period_texts[]  (period_id, locale) -> name, description,
                        what_youll_notice, how_to_support

AREAS are the classic Montessori curriculum areas. Do not invent new ones
without updating logic/category_period_map.py and the Kotlin AreaFilter.

PROVENANCE is required on every activity. See PRD v0.5 §6 — content must
declare where it came from. "own_words" means written for this app from
general knowledge of the method; "montessori_pd" means derived from Maria
Montessori's public-domain writing (cite the work).

RUSSIAN TRANSLATIONS in this file are a FIRST PASS and need native review
before release — these are words a parent will say to their child.

Usage:
    python3 tools/generate_content_seed.py
"""
import json
from pathlib import Path

OUTPUT_PATH = Path(__file__).resolve().parent.parent / "app/src/main/assets/content_seed.json"

LOCALES = ("en", "ru")

# --- Vocabulary -----------------------------------------------------------

# The eight classic Montessori curriculum areas.
AREAS = (
    "practical_life",
    "sensorial",
    "language",
    "mathematics",
    "movement",
    "art_and_music",
    "grace_and_courtesy",
    "culture_and_nature",
)

# Secondary axis for roughly 0-12 months, where the classic areas assume a
# mobile child and don't discriminate usefully. See PRD v0.5 §6.2.
INFANT_FOCUS = ("visual_development", "auditory", "grasping", "gross_motor_infant")

MESS_LEVELS = ("none", "low", "medium", "high")

PROVENANCE = ("own_words", "montessori_pd")


# --- Activities -------------------------------------------------------------
# Age bounds are inclusive, in months.
#
# Every entry needs: id, age_min_months, age_max_months, area, session_minutes,
# mess_level, provenance, and text for every locale in LOCALES.
# Optional: infant_focus, supervision_note, image/image_credit/image_licence.

ACTIVITIES = [
    # ---------------- 0-6 months -------------------------------------------
    {
        "id": 1, "age_min_months": 0, "age_max_months": 4, "area": "sensorial",
        "infant_focus": "visual_development", "session_minutes": 10,
        "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "High-contrast mobile",
            "summary": "A black-and-white mobile hung within focal range above where the baby lies.",
            "why_it_matters": "A newborn sees high contrast long before colour. A still, simple mobile gives the eyes something to work at, building focus and tracking.",
            "how_to_present": "Hang it about 20-30cm above the baby's chest, not directly over the face. Say nothing. Let the baby look for as long as they are interested, and take it down when they turn away.",
            "what_to_observe": "Does the baby fix on it? Follow it as it turns? How long before they look away?",
            "common_mistakes": "Adding movement, sound or colour 'to make it more interesting' — that turns work into entertainment.",
            "materials_needed": "High-contrast mobile (e.g. a Munari), a stand or hook",
            "homemade_alternative": "Black card shapes on white, hung from a wooden hoop or an embroidery ring. Under $5.",
            "supervision_note": "Never hang within reach, and never over an unattended sleeping baby.",
        },
        "ru": {
            "title": "Контрастный мобиль",
            "summary": "Чёрно-белый мобиль, подвешенный на расстоянии фокусировки над лежащим малышом.",
            "why_it_matters": "Новорождённый различает контраст задолго до цвета. Простой неподвижный мобиль даёт глазам работу и развивает умение фокусироваться и следить.",
            "how_to_present": "Повесьте примерно в 20-30 см над грудью ребёнка, не прямо над лицом. Ничего не говорите. Дайте смотреть столько, сколько интересно, и уберите, когда ребёнок отвернётся.",
            "what_to_observe": "Фиксирует ли взгляд? Следит ли за поворотом? Сколько времени проходит до потери интереса?",
            "common_mistakes": "Добавлять движение, звук или цвет «чтобы было интереснее» — это превращает работу в развлечение.",
            "materials_needed": "Контрастный мобиль (например, Мунари), стойка или крючок",
            "homemade_alternative": "Чёрные фигуры из картона на белом фоне, подвешенные на деревянных пяльцах. Меньше 500 ₽.",
            "supervision_note": "Никогда не вешайте в пределах досягаемости и не оставляйте над спящим ребёнком без присмотра.",
        },
    },
    {
        "id": 2, "age_min_months": 0, "age_max_months": 3, "area": "practical_life",
        "session_minutes": 5, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Topponcino handling",
            "summary": "A small firm pillow used to hold and move a newborn consistently.",
            "why_it_matters": "Being passed between adults is disorienting for a newborn. The topponcino keeps the surface, smell and support constant, so handovers feel safe.",
            "how_to_present": "Lay the baby on it, and lift baby and pillow together. Let visitors hold the topponcino rather than the baby directly.",
            "what_to_observe": "Does the baby settle faster during handovers? Do they stay asleep when put down?",
            "common_mistakes": "Washing it constantly — some of its value is that it smells familiar.",
            "materials_needed": "Topponcino (firm flat newborn pillow)",
            "homemade_alternative": "Sewn from cotton wadding and a soft cover; a common first sewing project.",
            "supervision_note": "For holding and transferring only, never for unsupervised sleep.",
        },
        "ru": {
            "title": "Топпончино",
            "summary": "Небольшая плотная подушечка, на которой держат и переносят новорождённого.",
            "why_it_matters": "Переход из рук в руки дезориентирует новорождённого. Топпончино сохраняет привычную поверхность, запах и опору, поэтому передача ощущается безопасно.",
            "how_to_present": "Положите ребёнка на подушечку и поднимайте вместе с ней. Гостям давайте держать топпончино, а не ребёнка напрямую.",
            "what_to_observe": "Быстрее ли ребёнок успокаивается при передаче? Продолжает ли спать, когда его кладут?",
            "common_mistakes": "Стирать слишком часто — часть пользы именно в знакомом запахе.",
            "materials_needed": "Топпончино (плоская плотная подушка для новорождённого)",
            "homemade_alternative": "Шьётся из хлопкового ватина и мягкого чехла — частый первый швейный проект.",
            "supervision_note": "Только для ношения и передачи, не для сна без присмотра.",
        },
    },
    {
        "id": 3, "age_min_months": 3, "age_max_months": 7, "area": "movement",
        "infant_focus": "grasping", "session_minutes": 10,
        "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Ring on a ribbon",
            "summary": "A wooden ring hung just within reach to invite batting, reaching and grasping.",
            "why_it_matters": "The move from accidental swiping to deliberate grasping is a major motor milestone. A ring that stays put lets the baby repeat the attempt until it works.",
            "how_to_present": "Hang it low enough to touch with an outstretched arm while lying on the back. Do not put it in the baby's hand — the reaching is the work.",
            "what_to_observe": "Batting or aiming? One hand or two? Do they bring it to the midline?",
            "common_mistakes": "Holding it against the hand so the baby 'succeeds'. That removes the whole exercise.",
            "materials_needed": "Wooden ring, ribbon, low stand or frame",
            "homemade_alternative": "A smooth wooden curtain ring on a length of grosgrain ribbon.",
            "supervision_note": "Ribbon must be short enough that it cannot loop around the neck. Supervise throughout.",
        },
        "ru": {
            "title": "Кольцо на ленте",
            "summary": "Деревянное кольцо, подвешенное на расстоянии вытянутой руки, чтобы малыш тянулся и хватал.",
            "why_it_matters": "Переход от случайных взмахов к осознанному захвату — важный моторный этап. Неподвижное кольцо позволяет повторять попытку, пока не получится.",
            "how_to_present": "Повесьте так, чтобы ребёнок доставал вытянутой рукой, лёжа на спине. Не вкладывайте кольцо в руку — работа именно в том, чтобы дотянуться.",
            "what_to_observe": "Машет или целится? Одной рукой или двумя? Подносит ли к средней линии тела?",
            "common_mistakes": "Подставлять кольцо в руку, чтобы «получилось». Это убирает саму задачу.",
            "materials_needed": "Деревянное кольцо, лента, низкая стойка или рамка",
            "homemade_alternative": "Гладкое деревянное кольцо для штор на репсовой ленте.",
            "supervision_note": "Лента должна быть достаточно короткой, чтобы не обернулась вокруг шеи. Только под присмотром.",
        },
    },
    {
        "id": 4, "age_min_months": 3, "age_max_months": 9, "area": "movement",
        "infant_focus": "gross_motor_infant", "session_minutes": 15,
        "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Movement mat with low mirror",
            "summary": "A firm flat mat with an unbreakable mirror at floor level.",
            "why_it_matters": "Unrestricted floor time is where rolling, pushing up and crawling are built. The mirror gives a reason to lift the head and hold it there.",
            "how_to_present": "Put the baby on their back on the mat, in view of the mirror, and let them move. Resist propping or repositioning.",
            "what_to_observe": "Head lifting, rolling attempts, pushing on forearms, interest in their own reflection.",
            "common_mistakes": "Using containers — bouncers, seats, walkers — which take away exactly the movement this builds.",
            "materials_needed": "Firm play mat, acrylic mirror mounted low and securely",
            "homemade_alternative": "A folded cotton quilt on the floor and an acrylic mirror tile screwed to a stud.",
            "supervision_note": "Mirror must be shatterproof acrylic and firmly fixed to the wall.",
        },
        "ru": {
            "title": "Мат для движения с низким зеркалом",
            "summary": "Плотный ровный мат и небьющееся зеркало на уровне пола.",
            "why_it_matters": "Свободное время на полу — это то, где формируются перевороты, отжимания на руках и ползание. Зеркало даёт повод поднять голову и удерживать её.",
            "how_to_present": "Положите ребёнка на спину на мат напротив зеркала и дайте двигаться. Не подпирайте и не переворачивайте его сами.",
            "what_to_observe": "Поднимает ли голову, пробует ли переворачиваться, опирается ли на предплечья, интересуется ли отражением.",
            "common_mistakes": "Использовать шезлонги, прыгунки и ходунки — они забирают именно то движение, которое здесь развивается.",
            "materials_needed": "Плотный коврик, акриловое зеркало, надёжно закреплённое низко",
            "homemade_alternative": "Сложенное хлопковое одеяло на полу и акриловая зеркальная плитка, прикрученная к стене.",
            "supervision_note": "Зеркало должно быть из небьющегося акрила и надёжно закреплено.",
        },
    },
    {
        "id": 5, "age_min_months": 6, "age_max_months": 12, "area": "sensorial",
        "infant_focus": "grasping", "session_minutes": 10,
        "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Object permanence box",
            "summary": "A ball is dropped into a hole, disappears, and reappears in a tray below.",
            "why_it_matters": "It makes an abstract idea concrete: things still exist when you cannot see them. The child proves it themselves, repeatedly.",
            "how_to_present": "Show it once, slowly, in silence: drop the ball, wait, retrieve it from the tray. Then hand it over and stop talking.",
            "what_to_observe": "Do they look for the ball in the tray before it appears? How many repetitions before they lose interest?",
            "common_mistakes": "Narrating each drop. The silence is what lets them notice the cause and effect.",
            "materials_needed": "Object permanence box with tray, one wooden ball",
            "homemade_alternative": "A shoebox with a ball-sized hole cut in the lid and one end open.",
            "supervision_note": "Ball must be too large to swallow — at least 4cm across.",
        },
        "ru": {
            "title": "Коробочка постоянства объекта",
            "summary": "Шарик опускают в отверстие, он исчезает и появляется в лотке снизу.",
            "why_it_matters": "Абстрактная идея становится осязаемой: предметы существуют, даже когда их не видно. Ребёнок доказывает это себе сам, снова и снова.",
            "how_to_present": "Покажите один раз, медленно и молча: опустите шарик, подождите, достаньте из лотка. Затем передайте ребёнку и молчите.",
            "what_to_observe": "Ищет ли шарик в лотке до того, как он появится? Сколько повторений до потери интереса?",
            "common_mistakes": "Комментировать каждое движение. Именно тишина позволяет заметить причину и следствие.",
            "materials_needed": "Коробочка постоянства объекта с лотком, деревянный шарик",
            "homemade_alternative": "Обувная коробка с отверстием по размеру шарика в крышке и открытым торцом.",
            "supervision_note": "Шарик должен быть слишком крупным, чтобы его проглотить — не меньше 4 см.",
        },
    },
    {
        "id": 6, "age_min_months": 6, "age_max_months": 14, "area": "sensorial",
        "infant_focus": "grasping", "session_minutes": 15,
        "mess_level": "low", "provenance": "own_words",
        "en": {
            "title": "Treasure basket",
            "summary": "A low basket of everyday natural objects for open-ended exploration while sitting.",
            "why_it_matters": "Real objects give the hand and mouth far more information than plastic toys: weight, temperature, grain, smell. This is sensorial work in its rawest form.",
            "how_to_present": "Place the basket within reach of a seated baby and sit back. Offer nothing, name nothing, let them choose.",
            "what_to_observe": "Which object do they return to? Do they mouth, turn, bang or transfer between hands?",
            "common_mistakes": "Handing objects over one at a time. Choosing is half the activity.",
            "materials_needed": "Low sturdy basket, 6-10 safe natural objects",
            "homemade_alternative": "This is the homemade version — a wooden spoon, pine cone, large shell, fabric scrap, metal whisk.",
            "supervision_note": "Constant supervision. Every object must be too large to swallow, with nothing that can splinter or come apart.",
        },
        "ru": {
            "title": "Корзина сокровищ",
            "summary": "Низкая корзина с бытовыми предметами из натуральных материалов для свободного исследования сидя.",
            "why_it_matters": "Настоящие предметы дают руке и рту гораздо больше информации, чем пластиковые игрушки: вес, температуру, фактуру, запах. Это сенсорная работа в самом чистом виде.",
            "how_to_present": "Поставьте корзину в пределах досягаемости сидящего малыша и отойдите. Ничего не предлагайте и не называйте — пусть выбирает сам.",
            "what_to_observe": "К какому предмету возвращается? Тянет в рот, вертит, стучит, перекладывает из руки в руку?",
            "common_mistakes": "Подавать предметы по одному. Выбор — это половина занятия.",
            "materials_needed": "Низкая устойчивая корзина, 6-10 безопасных натуральных предметов",
            "homemade_alternative": "Это и есть самодельный вариант: деревянная ложка, шишка, крупная ракушка, лоскут ткани, металлический венчик.",
            "supervision_note": "Только под постоянным присмотром. Все предметы должны быть слишком крупными, чтобы их проглотить, без осколков и мелких деталей.",
        },
    },
    # ---------------- 9-18 months -------------------------------------------
    {
        "id": 7, "age_min_months": 9, "age_max_months": 16, "area": "movement",
        "session_minutes": 20, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Weighted push trolley",
            "summary": "A sturdy weighted trolley to push while learning to walk.",
            "why_it_matters": "A child learning to walk needs something that resists, not something that rolls away. Weight gives the stability that makes the attempt possible.",
            "how_to_present": "Place it against a wall on a clear stretch of floor. Say nothing and stay near, but do not hold their hands.",
            "what_to_observe": "How far before sitting down? Do they change direction? Do they let go and stand?",
            "common_mistakes": "Holding both hands and walking them along — it teaches balance that depends on you.",
            "materials_needed": "Weighted wooden push trolley",
            "homemade_alternative": "A sturdy dining chair turned to push, or a laundry basket with a few books in it.",
            "supervision_note": "Clear the route of rugs and cables; a trolley on a rug tips.",
        },
        "ru": {
            "title": "Утяжелённая каталка",
            "summary": "Устойчивая тележка с грузом, которую малыш толкает, учась ходить.",
            "why_it_matters": "Ребёнку, который учится ходить, нужна опора, которая сопротивляется, а не укатывается. Вес даёт устойчивость, благодаря которой попытка вообще возможна.",
            "how_to_present": "Поставьте каталку у стены на свободном участке пола. Молчите и будьте рядом, но не держите за руки.",
            "what_to_observe": "Сколько проходит до того, как сядет? Меняет ли направление? Отпускает ли опору и стоит ли сам?",
            "common_mistakes": "Водить за обе руки — так формируется равновесие, зависящее от взрослого.",
            "materials_needed": "Деревянная каталка с утяжелением",
            "homemade_alternative": "Устойчивый стул, развёрнутый спинкой вперёд, или бельевая корзина с парой книг внутри.",
            "supervision_note": "Уберите с маршрута ковры и провода — на ковре каталка опрокидывается.",
        },
    },
    {
        "id": 8, "age_min_months": 10, "age_max_months": 18, "area": "sensorial",
        "session_minutes": 10, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Single-shape sorter",
            "summary": "A wooden box with one shape cut-out, before progressing to more.",
            "why_it_matters": "One shape means one problem. The child can succeed, then repeat, rather than guessing between options.",
            "how_to_present": "Show the shape going in once, slowly. Then place it in front of them and wait. Do not correct wrong attempts.",
            "what_to_observe": "Do they rotate the shape to fit? Do they keep trying after a failure, or hand it to you?",
            "common_mistakes": "Starting with a multi-shape cube. Too many variables at once, so nothing is learned cleanly.",
            "materials_needed": "Single-hole wooden shape sorter",
            "homemade_alternative": "A lidded box with one shape cut out and a matching wooden block.",
            "supervision_note": "Shapes must be too large to swallow.",
        },
        "ru": {
            "title": "Сортер с одной формой",
            "summary": "Деревянная коробка с одним отверстием — до перехода к нескольким формам.",
            "why_it_matters": "Одна форма — одна задача. Ребёнок может справиться и повторить, а не угадывать между вариантами.",
            "how_to_present": "Один раз медленно покажите, как форма входит в отверстие. Затем поставьте перед ребёнком и ждите. Не исправляйте неудачные попытки.",
            "what_to_observe": "Поворачивает ли форму, подбирая положение? Продолжает ли после неудачи или отдаёт вам?",
            "common_mistakes": "Начинать с куба на несколько форм. Слишком много переменных сразу — ничего не усваивается чисто.",
            "materials_needed": "Деревянный сортер с одним отверстием",
            "homemade_alternative": "Коробка с крышкой, в которой вырезано одно отверстие, и подходящий деревянный брусок.",
            "supervision_note": "Детали должны быть слишком крупными, чтобы их проглотить.",
        },
    },
    {
        "id": 9, "age_min_months": 10, "age_max_months": 24, "area": "language",
        "session_minutes": 5, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Naming real objects",
            "summary": "Naming everyday objects clearly and accurately during ordinary routines.",
            "why_it_matters": "Vocabulary is absorbed from what is actually said. At this age real objects teach language better than pictures, because the word attaches to something the child can hold.",
            "how_to_present": "Hold the object, make eye contact, say the word clearly and once. 'Spoon.' Then pause. Resist the sentence.",
            "what_to_observe": "Do they look at the named object? Attempt the sound? Which words come first?",
            "common_mistakes": "Diminutives and baby-talk ('spoonie'), which have to be unlearned later.",
            "materials_needed": "Nothing — everyday household objects",
            "homemade_alternative": "Free, and works during nappy changes, dressing and meals.",
        },
        "ru": {
            "title": "Называние реальных предметов",
            "summary": "Чёткое и точное называние привычных предметов в обычных бытовых ситуациях.",
            "why_it_matters": "Словарь усваивается из того, что реально произносится. В этом возрасте настоящие предметы учат языку лучше картинок, потому что слово привязывается к тому, что можно взять в руку.",
            "how_to_present": "Возьмите предмет, установите зрительный контакт, произнесите слово чётко и один раз. «Ложка». Затем пауза. Не разворачивайте в предложение.",
            "what_to_observe": "Смотрит ли на названный предмет? Пробует ли повторить звук? Какие слова появляются первыми?",
            "common_mistakes": "Уменьшительные формы и сюсюканье («ложечка») — потом придётся переучивать.",
            "materials_needed": "Ничего — обычные предметы быта",
            "homemade_alternative": "Бесплатно и работает во время переодевания, одевания и еды.",
        },
    },
    {
        "id": 10, "age_min_months": 10, "age_max_months": 18, "area": "movement",
        "session_minutes": 10, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Three-ring stacker",
            "summary": "Three graduated rings on a post.",
            "why_it_matters": "Threading a ring onto a post needs two hands doing different jobs at once — an early step towards real coordination.",
            "how_to_present": "Remove the rings one at a time, slowly, then replace them largest first. Hand it over without comment.",
            "what_to_observe": "Do they aim, or push? Do they mind the order? Do they take off before putting on?",
            "common_mistakes": "Reordering the rings for them. Wrong order is still a successful thread.",
            "materials_needed": "Wooden ring stacker, three rings",
            "homemade_alternative": "A kitchen roll holder with three sizes of bangle.",
            "supervision_note": "Rings must be too large to fit in the mouth.",
        },
        "ru": {
            "title": "Пирамидка из трёх колец",
            "summary": "Три кольца разного размера на стержне.",
            "why_it_matters": "Чтобы надеть кольцо на стержень, две руки должны одновременно делать разную работу — это ранний шаг к настоящей координации.",
            "how_to_present": "Медленно снимите кольца по одному, затем наденьте обратно, начиная с самого большого. Передайте ребёнку без комментариев.",
            "what_to_observe": "Целится или проталкивает? Важен ли ему порядок? Сначала снимает или сразу надевает?",
            "common_mistakes": "Перекладывать кольца в «правильном» порядке. Неверный порядок — это всё равно успешное надевание.",
            "materials_needed": "Деревянная пирамидка с тремя кольцами",
            "homemade_alternative": "Держатель для бумажных полотенец и три браслета разного размера.",
            "supervision_note": "Кольца должны быть слишком крупными, чтобы поместиться в рот.",
        },
    },
    # ---------------- 14-36 months ------------------------------------------
    {
        "id": 11, "age_min_months": 14, "age_max_months": 30, "area": "practical_life",
        "session_minutes": 15, "mess_level": "medium", "provenance": "own_words",
        "en": {
            "title": "Pouring water, jug to jug",
            "summary": "Pouring a small amount of water between two small pitchers on a tray.",
            "why_it_matters": "Real water, real consequences. Concentration comes from the task mattering — a spill the child can see and wipe up teaches more than a dry exercise.",
            "how_to_present": "Sit on their dominant side. Pour once, slowly, in silence, then return the water and offer the jug. Show where the cloth is.",
            "what_to_observe": "Do they watch the stream or the jug? Do they notice a spill? Do they reach for the cloth unprompted?",
            "common_mistakes": "Saying 'careful'. It transfers your anxiety and breaks their concentration.",
            "materials_needed": "Two small pitchers, a tray, a small cloth",
            "homemade_alternative": "Two small jugs or creamers from any kitchen shop. Start with 2cm of water.",
            "supervision_note": "Small amounts only, and never leave standing water unattended.",
        },
        "ru": {
            "title": "Переливание воды из кувшина в кувшин",
            "summary": "Переливание небольшого количества воды между двумя маленькими кувшинами на подносе.",
            "why_it_matters": "Настоящая вода — настоящие последствия. Концентрация рождается из значимости задачи: пролитая вода, которую видно и можно вытереть, учит больше, чем «сухое» упражнение.",
            "how_to_present": "Сядьте со стороны ведущей руки. Один раз медленно и молча перелейте, верните воду обратно и передайте кувшин. Покажите, где лежит тряпочка.",
            "what_to_observe": "Смотрит на струю или на кувшин? Замечает ли пролитое? Тянется ли за тряпочкой сам?",
            "common_mistakes": "Говорить «осторожно». Это передаёт вашу тревогу и сбивает концентрацию.",
            "materials_needed": "Два маленьких кувшина, поднос, небольшая тряпочка",
            "homemade_alternative": "Два маленьких кувшинчика или молочника из любого магазина посуды. Начните с 2 см воды.",
            "supervision_note": "Только небольшое количество воды, не оставляйте налитую воду без присмотра.",
        },
    },
    {
        "id": 12, "age_min_months": 18, "age_max_months": 36, "area": "practical_life",
        "session_minutes": 15, "mess_level": "medium", "provenance": "own_words",
        "en": {
            "title": "Spooning beans between bowls",
            "summary": "Transferring dried beans from one bowl to another with a spoon.",
            "why_it_matters": "Isolates the wrist rotation that later makes eating, writing and pouring possible, with a self-evident goal and no adult judgement needed.",
            "how_to_present": "Spoon three or four beans across, slowly, then return them and hand over the spoon.",
            "what_to_observe": "Does the wrist turn or the whole arm? Do they finish the transfer, or stop halfway?",
            "common_mistakes": "Beans that are too small too early. Start large — chickpeas before lentils.",
            "materials_needed": "Two small bowls, a spoon, dried beans, a tray",
            "homemade_alternative": "Entirely homemade already — any two bowls and a dessert spoon.",
            "supervision_note": "Choking and inhalation risk. Constant supervision, and not for children who still mouth objects.",
        },
        "ru": {
            "title": "Перекладывание фасоли ложкой",
            "summary": "Перекладывание сухой фасоли ложкой из одной миски в другую.",
            "why_it_matters": "Изолирует поворот запястья, который позже делает возможными еду, письмо и переливание. Цель очевидна сама по себе, оценка взрослого не нужна.",
            "how_to_present": "Медленно переложите три-четыре фасолины, верните обратно и передайте ложку.",
            "what_to_observe": "Работает запястье или вся рука? Доводит ли перекладывание до конца?",
            "common_mistakes": "Слишком мелкие крупы слишком рано. Начинайте с крупного — нут раньше чечевицы.",
            "materials_needed": "Две небольшие миски, ложка, сухая фасоль, поднос",
            "homemade_alternative": "Полностью самодельное занятие — любые две миски и десертная ложка.",
            "supervision_note": "Риск подавиться и вдохнуть. Только под постоянным присмотром и не для детей, которые ещё тянут всё в рот.",
        },
    },
    {
        "id": 13, "age_min_months": 18, "age_max_months": 48, "area": "practical_life",
        "session_minutes": 5, "mess_level": "low", "provenance": "own_words",
        "en": {
            "title": "Wiping up a spill",
            "summary": "A small sponge kept accessible so the child cleans up their own spills.",
            "why_it_matters": "Turns an accident into work rather than a telling-off. Restoring order is deeply satisfying at this age, and it is how care of the environment starts.",
            "how_to_present": "When something spills, say nothing about the spill. Fetch the sponge, wipe once, and hand it over.",
            "what_to_observe": "Do they go for the sponge before you? Do they wipe the whole area or one spot?",
            "common_mistakes": "Doing it faster yourself because you are in a hurry.",
            "materials_needed": "Small sponge, small basin, low shelf or hook",
            "homemade_alternative": "A cut-down kitchen sponge in a saucer at child height.",
        },
        "ru": {
            "title": "Вытереть пролитое",
            "summary": "Маленькая губка в доступном месте, чтобы ребёнок сам убирал за собой.",
            "why_it_matters": "Превращает случайность в работу, а не в замечание. Восстановление порядка в этом возрасте приносит глубокое удовлетворение — так начинается забота о среде.",
            "how_to_present": "Когда что-то пролилось, не комментируйте это. Принесите губку, один раз проведите ею и передайте ребёнку.",
            "what_to_observe": "Идёт ли за губкой раньше вас? Вытирает всю площадь или одно место?",
            "common_mistakes": "Делать самому, потому что так быстрее.",
            "materials_needed": "Небольшая губка, миска, низкая полка или крючок",
            "homemade_alternative": "Обрезанная кухонная губка в блюдце на уровне ребёнка.",
        },
    },
    {
        "id": 14, "age_min_months": 18, "age_max_months": 36, "area": "sensorial",
        "session_minutes": 10, "mess_level": "low", "provenance": "own_words",
        "en": {
            "title": "Sorting by colour",
            "summary": "Sorting objects into bowls by one quality only — colour.",
            "why_it_matters": "Classification starts with isolating a single difference. Everything else about the objects is kept identical so colour is the only thing that varies.",
            "how_to_present": "Place two of each colour, sort them slowly without speaking, mix them back and hand over.",
            "what_to_observe": "Do they sort by colour, or by something else entirely? Do they check their own work?",
            "common_mistakes": "Objects that differ in size and shape as well as colour — then it isn't a colour exercise.",
            "materials_needed": "Two or three small bowls, identical objects in two or three strong colours",
            "homemade_alternative": "Pom-poms, buttons or coloured pasta with matching bowls.",
            "supervision_note": "Small parts — supervise, and skip if the child still mouths objects.",
        },
        "ru": {
            "title": "Сортировка по цвету",
            "summary": "Раскладывание предметов по мискам по одному признаку — цвету.",
            "why_it_matters": "Классификация начинается с выделения одного различия. Все остальные свойства предметов остаются одинаковыми, чтобы менялся только цвет.",
            "how_to_present": "Положите по два предмета каждого цвета, медленно и молча разложите, перемешайте обратно и передайте ребёнку.",
            "what_to_observe": "Сортирует по цвету или по чему-то совсем другому? Проверяет ли себя?",
            "common_mistakes": "Предметы, которые отличаются ещё и размером и формой — тогда это уже не упражнение на цвет.",
            "materials_needed": "Две-три небольшие миски, одинаковые предметы двух-трёх ярких цветов",
            "homemade_alternative": "Помпоны, пуговицы или окрашенные макароны и подходящие миски.",
            "supervision_note": "Мелкие детали — только под присмотром, не давайте, если ребёнок ещё тянет предметы в рот.",
        },
    },
    # ---------------- 2-4 years ---------------------------------------------
    {
        "id": 15, "age_min_months": 24, "age_max_months": 48, "area": "practical_life",
        "session_minutes": 10, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Button dressing frame",
            "summary": "Two fabric panels joined by large buttons, mounted in a frame.",
            "why_it_matters": "Isolates the buttoning motion away from the child's own body, where they cannot see what their hands are doing.",
            "how_to_present": "Unbutton one button slowly, showing each step: push through, pull, release. Then hand it over.",
            "what_to_observe": "Which hand holds and which pushes? Do they persist through the first failure?",
            "common_mistakes": "Introducing several frames at once. One fastening at a time.",
            "materials_needed": "Button dressing frame",
            "homemade_alternative": "Two rectangles of felt with large buttons, stapled to a picture frame.",
        },
        "ru": {
            "title": "Рамка с пуговицами",
            "summary": "Две тканевые части, соединённые крупными пуговицами, закреплённые в рамке.",
            "why_it_matters": "Изолирует движение застёгивания и выносит его за пределы собственной одежды, где ребёнок не видит, что делают его руки.",
            "how_to_present": "Медленно расстегните одну пуговицу, показывая каждый шаг: продеть, потянуть, отпустить. Затем передайте рамку.",
            "what_to_observe": "Какая рука держит, а какая проталкивает? Продолжает ли после первой неудачи?",
            "common_mistakes": "Давать несколько рамок сразу. По одной застёжке за раз.",
            "materials_needed": "Рамка с пуговицами",
            "homemade_alternative": "Два прямоугольника фетра с крупными пуговицами, закреплённые на рамке для фото.",
        },
    },
    {
        "id": 16, "age_min_months": 30, "age_max_months": 60, "area": "sensorial",
        "session_minutes": 20, "mess_level": "none", "provenance": "montessori_pd",
        "en": {
            "title": "Pink tower",
            "summary": "Ten graduated pink cubes, stacked largest to smallest.",
            "why_it_matters": "Discrimination of size in three dimensions at once. The error is visible — a wrong cube makes the tower look wrong — so the material corrects the child, not the adult.",
            "how_to_present": "Carry each cube with two hands, one at a time, to a floor mat. Build from largest up, slowly, in silence. Then dismantle and invite them.",
            "what_to_observe": "Do they compare before placing? Do they notice their own error and fix it?",
            "common_mistakes": "Pointing out a misplaced cube. The whole design is that the child sees it themselves.",
            "materials_needed": "Pink tower (10 cubes), floor mat",
            "homemade_alternative": "Ten graduated cardboard boxes, painted a single colour so size is the only variable.",
        },
        "ru": {
            "title": "Розовая башня",
            "summary": "Десять розовых кубов разного размера, выстроенных от большего к меньшему.",
            "why_it_matters": "Различение размера сразу в трёх измерениях. Ошибка видна — неправильный куб делает башню кривой, поэтому ребёнка поправляет материал, а не взрослый.",
            "how_to_present": "Переносите кубы по одному двумя руками на коврик. Стройте от самого большого, медленно и молча. Затем разберите и пригласите ребёнка.",
            "what_to_observe": "Сравнивает ли кубы перед тем, как поставить? Замечает ли и исправляет ли свою ошибку?",
            "common_mistakes": "Указывать на неправильно поставленный куб. Весь смысл в том, чтобы ребёнок увидел это сам.",
            "materials_needed": "Розовая башня (10 кубов), коврик",
            "homemade_alternative": "Десять картонных коробок разного размера, окрашенных в один цвет, чтобы менялся только размер.",
        },
    },
    {
        "id": 17, "age_min_months": 30, "age_max_months": 60, "area": "sensorial",
        "session_minutes": 20, "mess_level": "none", "provenance": "montessori_pd",
        "en": {
            "title": "Knobbed cylinder block",
            "summary": "Cylinders of varying dimension removed from and replaced into matching sockets.",
            "why_it_matters": "The knob is held in the exact three-finger grip later used for a pencil. The child practises writing without knowing it.",
            "how_to_present": "Remove each cylinder by the knob, in order, and place them in a row. Mix them. Replace them, comparing before each. Then offer it.",
            "what_to_observe": "Are they using three fingers or a whole fist? Do they try the socket or compare by eye first?",
            "common_mistakes": "Offering more than one block at a time before the first is easy.",
            "materials_needed": "One knobbed cylinder block",
            "homemade_alternative": "Hard to make well — this is one worth buying, or borrowing from a Montessori group.",
        },
        "ru": {
            "title": "Блок цилиндров с ручками",
            "summary": "Цилиндры разного размера, которые вынимают из гнёзд и вставляют обратно.",
            "why_it_matters": "Ручку цилиндра берут точно тем же захватом тремя пальцами, каким потом держат карандаш. Ребёнок тренирует письмо, не подозревая об этом.",
            "how_to_present": "Вынимайте цилиндры за ручку по порядку и выкладывайте в ряд. Перемешайте. Вставляйте обратно, каждый раз сравнивая. Затем передайте ребёнку.",
            "what_to_observe": "Держит тремя пальцами или всей ладонью? Пробует наугад или сначала сравнивает глазами?",
            "common_mistakes": "Давать несколько блоков сразу, пока первый ещё не даётся легко.",
            "materials_needed": "Один блок цилиндров с ручками",
            "homemade_alternative": "Сложно сделать качественно — этот материал стоит купить или взять в монтессори-группе.",
        },
    },
    {
        "id": 18, "age_min_months": 36, "age_max_months": 54, "area": "language",
        "session_minutes": 10, "mess_level": "none", "provenance": "montessori_pd",
        "en": {
            "title": "Sandpaper letters",
            "summary": "Letters cut from sandpaper, traced with two fingers while saying the sound.",
            "why_it_matters": "Links the shape, the movement and the sound in one action. The hand learns the letter before the pencil is involved.",
            "how_to_present": "Trace with two fingers in writing direction and say the sound — not the letter name. Three letters at a time, contrasting in shape and sound.",
            "what_to_observe": "Do they trace in the right direction? Do they say the sound as they go?",
            "common_mistakes": "Teaching letter names ('em') rather than sounds ('mmm'), which makes blending words harder later.",
            "materials_needed": "Sandpaper letters, lower case",
            "homemade_alternative": "Letters cut from fine sandpaper and glued to stiff card.",
        },
        "ru": {
            "title": "Шершавые буквы",
            "summary": "Буквы из наждачной бумаги, которые обводят двумя пальцами, произнося звук.",
            "why_it_matters": "Связывает форму, движение и звук в одном действии. Рука запоминает букву раньше, чем в дело вступает карандаш.",
            "how_to_present": "Обводите двумя пальцами в направлении письма и произносите звук, а не название буквы. По три буквы за раз, контрастные по форме и звучанию.",
            "what_to_observe": "Обводит ли в правильном направлении? Произносит ли звук одновременно с движением?",
            "common_mistakes": "Учить названия букв («эм») вместо звуков («ммм») — потом труднее сливать буквы в слова.",
            "materials_needed": "Набор шершавых букв, строчные",
            "homemade_alternative": "Буквы, вырезанные из мелкой наждачной бумаги и наклеенные на плотный картон.",
        },
    },
    {
        "id": 19, "age_min_months": 36, "age_max_months": 72, "area": "practical_life",
        "session_minutes": 10, "mess_level": "medium", "provenance": "own_words",
        "en": {
            "title": "Caring for a plant",
            "summary": "Watering and wiping the leaves of a real plant kept at child height.",
            "why_it_matters": "A living thing that visibly depends on them. Responsibility becomes concrete rather than a lecture.",
            "how_to_present": "Fill the watering can to a marked line, water slowly at the base, wipe one leaf supporting it from underneath.",
            "what_to_observe": "Do they check the plant unprompted? Do they overwater? Do they handle the leaves gently?",
            "common_mistakes": "A delicate plant. Early over- and under-watering is guaranteed, so choose something forgiving.",
            "materials_needed": "Small watering can, a hardy houseplant at low height, a cloth",
            "homemade_alternative": "A spider plant or pothos in a heavy pot — both survive almost anything.",
            "supervision_note": "Check the plant is non-toxic before it goes at child height.",
        },
        "ru": {
            "title": "Уход за растением",
            "summary": "Полив и протирание листьев настоящего растения, стоящего на уровне ребёнка.",
            "why_it_matters": "Живое существо, которое явно зависит от него. Ответственность становится осязаемой, а не нравоучением.",
            "how_to_present": "Налейте воду в лейку до отметки, медленно полейте под корень, протрите один лист, придерживая его снизу.",
            "what_to_observe": "Подходит ли к растению без напоминания? Не переливает ли? Аккуратно ли обращается с листьями?",
            "common_mistakes": "Выбрать капризное растение. Перелив и недолив в начале неизбежны, поэтому нужен неприхотливый вид.",
            "materials_needed": "Маленькая лейка, неприхотливое растение на низкой подставке, тряпочка",
            "homemade_alternative": "Хлорофитум или эпипремнум в тяжёлом горшке — переживут почти всё.",
            "supervision_note": "Убедитесь, что растение неядовитое, прежде чем ставить его на уровень ребёнка.",
        },
    },
    {
        "id": 20, "age_min_months": 42, "age_max_months": 66, "area": "movement",
        "session_minutes": 15, "mess_level": "low", "provenance": "montessori_pd",
        "en": {
            "title": "Metal insets",
            "summary": "Tracing geometric insets and filling them with pencil lines.",
            "why_it_matters": "Builds the pencil control that handwriting needs, with a result the child finds worth doing for its own sake.",
            "how_to_present": "Trace the frame, then fill with even parallel lines in one direction, keeping inside the outline.",
            "what_to_observe": "Is the grip settling into three fingers? Are lines becoming more even? Do they stay inside the line?",
            "common_mistakes": "Commenting on neatness. The control develops through repetition, not correction.",
            "materials_needed": "Metal insets set, coloured pencils, plain paper",
            "homemade_alternative": "Stencils cut from stiff card, though the metal weight does help.",
        },
        "ru": {
            "title": "Металлические рамки-вкладыши",
            "summary": "Обведение геометрических вкладышей и заполнение их карандашными линиями.",
            "why_it_matters": "Развивает контроль карандаша, нужный для письма, и даёт результат, который ребёнку интересен сам по себе.",
            "how_to_present": "Обведите фигуру, затем заполните ровными параллельными линиями в одном направлении, не выходя за контур.",
            "what_to_observe": "Формируется ли захват тремя пальцами? Становятся ли линии ровнее? Не выходит ли за контур?",
            "common_mistakes": "Комментировать аккуратность. Контроль развивается повторением, а не исправлением.",
            "materials_needed": "Набор металлических рамок-вкладышей, цветные карандаши, бумага",
            "homemade_alternative": "Трафареты из плотного картона, хотя вес металла всё же помогает.",
        },
    },
    {
        "id": 21, "age_min_months": 48, "age_max_months": 72, "area": "practical_life",
        "session_minutes": 30, "mess_level": "high", "provenance": "own_words",
        "en": {
            "title": "Table washing",
            "summary": "A full ordered sequence: wet, soap, scrub, rinse, dry a small table.",
            "why_it_matters": "Many refined movements combined into one long purposeful task. This is where sustained concentration is really built.",
            "how_to_present": "Model the whole sequence once, slowly, in order, without shortcuts. Then leave them to it.",
            "what_to_observe": "Do they keep the order? How long do they stay with it? Do they repeat it?",
            "common_mistakes": "Stepping in to finish. A long task interrupted teaches that the work does not matter.",
            "materials_needed": "Small basin, apron, scrubbing brush, soap, sponge, drying cloth",
            "homemade_alternative": "All of it from the kitchen already; an apron on a low hook makes it a routine.",
            "supervision_note": "Warm, not hot, water. Supervise near buckets.",
        },
        "ru": {
            "title": "Мытьё стола",
            "summary": "Полная последовательность: намочить, намылить, потереть, ополоснуть, вытереть небольшой стол.",
            "why_it_matters": "Множество отточенных движений, соединённых в одну длинную осмысленную задачу. Именно здесь по-настоящему формируется устойчивая концентрация.",
            "how_to_present": "Один раз медленно покажите всю последовательность по порядку, без сокращений. Затем оставьте ребёнка одного.",
            "what_to_observe": "Соблюдает ли порядок? Сколько времени остаётся с задачей? Повторяет ли её?",
            "common_mistakes": "Вмешиваться, чтобы доделать. Прерванная длинная работа учит тому, что она неважна.",
            "materials_needed": "Небольшой таз, фартук, щётка, мыло, губка, полотенце",
            "homemade_alternative": "Всё уже есть на кухне; фартук на низком крючке превращает это в привычку.",
            "supervision_note": "Тёплая, не горячая вода. Присматривайте рядом с тазом.",
        },
    },
    {
        "id": 22, "age_min_months": 42, "age_max_months": 66, "area": "language",
        "session_minutes": 20, "mess_level": "none", "provenance": "montessori_pd",
        "en": {
            "title": "Moveable alphabet",
            "summary": "Building simple phonetic words from cut-out letters.",
            "why_it_matters": "Lets a child compose words long before their hand can write them. Thinking runs ahead of handwriting, instead of being held back by it.",
            "how_to_present": "Say a three-sound word slowly. Let them find each sound. Do not correct spelling — the point is hearing the sounds.",
            "what_to_observe": "Can they isolate the first sound? The last? Do they read back what they built?",
            "common_mistakes": "Correcting spelling. That comes much later; here, 'kat' is a success.",
            "materials_needed": "Moveable alphabet, small objects or picture cards for three-letter words",
            "homemade_alternative": "Letters printed on card — vowels one colour, consonants another.",
        },
        "ru": {
            "title": "Подвижный алфавит",
            "summary": "Составление простых фонетических слов из отдельных букв.",
            "why_it_matters": "Позволяет составлять слова задолго до того, как рука научится писать. Мысль опережает письмо, а не сдерживается им.",
            "how_to_present": "Медленно произнесите слово из трёх звуков. Дайте ребёнку найти каждый звук. Не исправляйте написание — задача в том, чтобы услышать звуки.",
            "what_to_observe": "Может ли выделить первый звук? Последний? Читает ли обратно то, что составил?",
            "common_mistakes": "Исправлять орфографию. Это гораздо позже; здесь «кот» через «а» — уже успех.",
            "materials_needed": "Подвижный алфавит, мелкие предметы или карточки для слов из трёх букв",
            "homemade_alternative": "Буквы, напечатанные на картоне: гласные одним цветом, согласные другим.",
        },
    },
    # ---------------- Mathematics (new area) ---------------------------------
    {
        "id": 23, "age_min_months": 48, "age_max_months": 72, "area": "mathematics",
        "session_minutes": 20, "mess_level": "none", "provenance": "montessori_pd",
        "en": {
            "title": "Number rods",
            "summary": "Ten rods of increasing length in alternating red and blue segments.",
            "why_it_matters": "Quantity becomes something you can hold and compare. 'Five' is a length before it is a symbol.",
            "how_to_present": "Lay the rods out from shortest to longest on a mat. Count the segments of the first three by touching each one.",
            "what_to_observe": "Do they count segments or just recite? Do they compare rods by holding them together?",
            "common_mistakes": "Rushing to the written numeral before the quantity is solid.",
            "materials_needed": "Number rods 1-10, long floor mat",
            "homemade_alternative": "Ten strips of card in 10cm increments, segments coloured alternately.",
        },
        "ru": {
            "title": "Числовые штанги",
            "summary": "Десять штанг возрастающей длины с чередующимися красными и синими отрезками.",
            "why_it_matters": "Количество становится тем, что можно взять в руки и сравнить. «Пять» — сначала длина, и только потом символ.",
            "how_to_present": "Разложите штанги на коврике от самой короткой к самой длинной. Посчитайте отрезки первых трёх, дотрагиваясь до каждого.",
            "what_to_observe": "Считает отрезки или просто произносит числа по памяти? Сравнивает ли штанги, прикладывая их друг к другу?",
            "common_mistakes": "Спешить к написанию цифр, пока не закрепилось само количество.",
            "materials_needed": "Числовые штанги 1-10, длинный коврик",
            "homemade_alternative": "Десять картонных полос с шагом 10 см, отрезки окрашены поочерёдно.",
        },
    },
    {
        "id": 24, "age_min_months": 36, "age_max_months": 60, "area": "mathematics",
        "session_minutes": 10, "mess_level": "low", "provenance": "own_words",
        "en": {
            "title": "Counting with real objects",
            "summary": "Counting a small set of real things by moving each one as it is counted.",
            "why_it_matters": "Reciting numbers is not counting. Moving one object per number is what builds one-to-one correspondence.",
            "how_to_present": "Move each item to a second pile as you say its number. Stop at three or four before going further.",
            "what_to_observe": "Does one number match one object? Do they recount the same item twice?",
            "common_mistakes": "Counting to twenty by rote and assuming the child understands quantity.",
            "materials_needed": "A small bowl of identical objects — shells, buttons, stones",
            "homemade_alternative": "Anything you have ten of. Identical objects work best, so only the count varies.",
            "supervision_note": "Small objects — supervise, and choose larger items for younger children.",
        },
        "ru": {
            "title": "Счёт на реальных предметах",
            "summary": "Пересчёт небольшого набора настоящих предметов с перекладыванием каждого.",
            "why_it_matters": "Произносить числа по порядку — это ещё не счёт. Именно перекладывание одного предмета на одно число формирует взаимно-однозначное соответствие.",
            "how_to_present": "Перекладывайте предмет во вторую кучку, называя его номер. Остановитесь на трёх-четырёх, прежде чем идти дальше.",
            "what_to_observe": "Одно число приходится на один предмет? Не пересчитывает ли один и тот же дважды?",
            "common_mistakes": "Считать до двадцати наизусть и думать, что ребёнок понимает количество.",
            "materials_needed": "Мисочка одинаковых предметов — ракушки, пуговицы, камешки",
            "homemade_alternative": "Всё, чего у вас есть десять штук. Лучше одинаковые предметы, чтобы менялось только количество.",
            "supervision_note": "Мелкие предметы — только под присмотром, для младших берите покрупнее.",
        },
    },
    # ---------------- Art & Music (new area) ---------------------------------
    {
        "id": 25, "age_min_months": 18, "age_max_months": 48, "area": "art_and_music",
        "session_minutes": 15, "mess_level": "high", "provenance": "own_words",
        "en": {
            "title": "Open-ended drawing",
            "summary": "Paper and a few good-quality pencils, always available, with no subject set.",
            "why_it_matters": "The value is in the mark-making, not the picture. A child asked to draw something specific stops exploring and starts performing.",
            "how_to_present": "Set out paper and pencils at a low table. Say nothing about what to draw.",
            "what_to_observe": "Do they repeat a mark? Do they use whole-arm or wrist movement? How long do they stay?",
            "common_mistakes": "Asking 'what is it?'. Describe what you see instead — 'you made many circles'.",
            "materials_needed": "Plain paper, chunky coloured pencils, low table",
            "homemade_alternative": "The back of used printer paper works perfectly.",
            "supervision_note": "Non-toxic materials, and supervise while the child still mouths things.",
        },
        "ru": {
            "title": "Свободное рисование",
            "summary": "Бумага и несколько качественных карандашей в постоянном доступе, без заданной темы.",
            "why_it_matters": "Ценность в самом следе, а не в картинке. Ребёнок, которому велели нарисовать что-то конкретное, перестаёт исследовать и начинает выступать.",
            "how_to_present": "Положите бумагу и карандаши на низкий стол. Ничего не говорите о том, что рисовать.",
            "what_to_observe": "Повторяет ли один и тот же штрих? Движется вся рука или запястье? Сколько времени остаётся за работой?",
            "common_mistakes": "Спрашивать «что это?». Лучше описать увиденное: «ты нарисовал много кругов».",
            "materials_needed": "Бумага, толстые цветные карандаши, низкий стол",
            "homemade_alternative": "Обратная сторона использованной бумаги для принтера подойдёт идеально.",
            "supervision_note": "Нетоксичные материалы; присматривайте, пока ребёнок тянет предметы в рот.",
        },
    },
    {
        "id": 26, "age_min_months": 12, "age_max_months": 48, "area": "art_and_music",
        "session_minutes": 10, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Real instruments, listened to",
            "summary": "A few real instruments, and music played deliberately rather than as background.",
            "why_it_matters": "Background music becomes noise the child learns to ignore. Music that is put on, listened to, and turned off is something they attend to.",
            "how_to_present": "Play one piece. Sit and listen with them. Turn it off when it ends. Offer an instrument separately.",
            "what_to_observe": "Do they still, or move? Do they seek the instrument out again?",
            "common_mistakes": "Music playing all day, and electronic toys that make sounds the child did not cause.",
            "materials_needed": "A real instrument or two — egg shaker, small drum, bells",
            "homemade_alternative": "A sealed jar of rice, or a wooden spoon on an upturned pot.",
            "supervision_note": "Homemade shakers must be sealed so they cannot open.",
        },
        "ru": {
            "title": "Настоящие инструменты и осознанное слушание",
            "summary": "Несколько настоящих инструментов и музыка, которую включают специально, а не фоном.",
            "why_it_matters": "Фоновая музыка становится шумом, который ребёнок учится не замечать. Музыка, которую включили, послушали и выключили, — это то, на что он обращает внимание.",
            "how_to_present": "Включите одно произведение. Сядьте и слушайте вместе. Выключите, когда оно закончится. Инструмент предложите отдельно.",
            "what_to_observe": "Замирает или начинает двигаться? Возвращается ли к инструменту сам?",
            "common_mistakes": "Музыка, играющая весь день, и электронные игрушки со звуками, которые ребёнок не вызывал.",
            "materials_needed": "Один-два настоящих инструмента — шейкер, маленький барабан, колокольчики",
            "homemade_alternative": "Плотно закрытая банка с рисом или деревянная ложка и перевёрнутая кастрюля.",
            "supervision_note": "Самодельные шейкеры должны быть герметично закрыты.",
        },
    },
    # ---------------- Grace & Courtesy (new area) ----------------------------
    {
        "id": 27, "age_min_months": 30, "age_max_months": 72, "area": "grace_and_courtesy",
        "session_minutes": 5, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "How to interrupt politely",
            "summary": "Teaching a child to place a hand on your arm and wait, instead of shouting over you.",
            "why_it_matters": "Gives the child a way to be heard that works. Most interrupting is not rudeness — it is having no other method.",
            "how_to_present": "Practise when nothing is happening: they place a hand on your arm, you place yours over it to show you know, then finish your sentence and turn to them.",
            "what_to_observe": "Do they use it unprompted? Can they wait a few seconds? Does the waiting time grow?",
            "common_mistakes": "Teaching it in the moment of frustration. Rehearse it calmly first, like any other lesson.",
            "materials_needed": "Nothing",
            "homemade_alternative": "Free — and it needs practising with another adult to be believable.",
        },
        "ru": {
            "title": "Как вежливо прервать взрослого",
            "summary": "Ребёнок кладёт руку вам на предплечье и ждёт, вместо того чтобы перекрикивать.",
            "why_it_matters": "Даёт ребёнку работающий способ быть услышанным. Чаще всего перебивание — не грубость, а отсутствие другого способа.",
            "how_to_present": "Отрабатывайте в спокойный момент: ребёнок кладёт руку вам на предплечье, вы накрываете её своей — значит, вы заметили, — договариваете фразу и поворачиваетесь к нему.",
            "what_to_observe": "Использует ли без напоминания? Может ли подождать несколько секунд? Растёт ли это время?",
            "common_mistakes": "Учить этому в момент раздражения. Сначала спокойно отрепетируйте, как любой другой урок.",
            "materials_needed": "Ничего",
            "homemade_alternative": "Бесплатно — но нужно потренироваться со вторым взрослым, чтобы выглядело правдоподобно.",
        },
    },
    {
        "id": 28, "age_min_months": 24, "age_max_months": 60, "area": "grace_and_courtesy",
        "session_minutes": 5, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Carrying and offering with care",
            "summary": "Carrying a tray or a glass with two hands, and offering food to someone else first.",
            "why_it_matters": "Care of objects and care of people are the same lesson. Carrying something breakable slowly is a real responsibility a small child can meet.",
            "how_to_present": "Carry the tray with two hands at waist height, walk slowly, set it down with both hands. Then offer it to someone before taking your own.",
            "what_to_observe": "Two hands or one? Do they slow down without being told? Do they offer before taking?",
            "common_mistakes": "Giving only unbreakable things. Real glass is what makes the care meaningful.",
            "materials_needed": "A small tray, a small glass, something to share",
            "homemade_alternative": "A small glass tumbler and any tray you own.",
            "supervision_note": "Use real but small glassware, and stay nearby the first several times.",
        },
        "ru": {
            "title": "Аккуратно нести и предлагать другим",
            "summary": "Нести поднос или стакан двумя руками и сначала предложить еду другому.",
            "why_it_matters": "Бережное отношение к вещам и к людям — один и тот же урок. Медленно нести что-то хрупкое — настоящая ответственность, посильная для малыша.",
            "how_to_present": "Несите поднос двумя руками на уровне пояса, идите медленно, поставьте обеими руками. Затем предложите другому, прежде чем взять себе.",
            "what_to_observe": "Двумя руками или одной? Замедляется ли сам, без напоминания? Предлагает ли раньше, чем берёт?",
            "common_mistakes": "Давать только небьющееся. Именно настоящее стекло делает аккуратность осмысленной.",
            "materials_needed": "Небольшой поднос, маленький стакан, что-то, чем можно поделиться",
            "homemade_alternative": "Маленький стеклянный стакан и любой поднос, который есть дома.",
            "supervision_note": "Используйте настоящую, но небольшую стеклянную посуду и будьте рядом первые несколько раз.",
        },
    },
    # ---------------- Culture & Nature (new area) ----------------------------
    {
        "id": 29, "age_min_months": 24, "age_max_months": 72, "area": "culture_and_nature",
        "session_minutes": 30, "mess_level": "medium", "provenance": "own_words",
        "en": {
            "title": "Nature tray from a walk",
            "summary": "Collecting a few natural objects on a walk and arranging them on a tray at home.",
            "why_it_matters": "Connects the outside world to the shelf. Sorting and revisiting what they found extends a walk into days of attention.",
            "how_to_present": "Take a small bag. Collect a few things, not everything. At home, arrange them on a tray at child height and name them once.",
            "what_to_observe": "What do they choose? Do they return to the tray? Do they start sorting by type?",
            "common_mistakes": "Collecting too much. Five things well looked at beat fifty in a heap.",
            "materials_needed": "Small bag, a tray, a low shelf",
            "homemade_alternative": "Entirely free — a baking tray works.",
            "supervision_note": "Check for berries, mushrooms and anything sharp before it comes home.",
        },
        "ru": {
            "title": "Поднос природы после прогулки",
            "summary": "Собрать на прогулке несколько природных предметов и разложить их дома на подносе.",
            "why_it_matters": "Связывает внешний мир с полкой дома. Разбор и возвращение к находкам растягивают прогулку на несколько дней внимания.",
            "how_to_present": "Возьмите маленький мешочек. Соберите несколько вещей, а не всё подряд. Дома разложите их на подносе на уровне ребёнка и один раз назовите.",
            "what_to_observe": "Что выбирает? Возвращается ли к подносу? Начинает ли раскладывать по видам?",
            "common_mistakes": "Собирать слишком много. Пять внимательно рассмотренных предметов лучше пятидесяти в куче.",
            "materials_needed": "Маленький мешочек, поднос, низкая полка",
            "homemade_alternative": "Совершенно бесплатно — подойдёт противень.",
            "supervision_note": "Проверьте находки на ягоды, грибы и острые предметы, прежде чем нести домой.",
        },
    },
    {
        "id": 30, "age_min_months": 36, "age_max_months": 72, "area": "culture_and_nature",
        "session_minutes": 10, "mess_level": "none", "provenance": "own_words",
        "en": {
            "title": "Today's weather and season",
            "summary": "Looking out of the window each morning and naming the weather and the season.",
            "why_it_matters": "Time is abstract; weather is not. A daily observation gives the child a concrete handle on the passing of days and seasons.",
            "how_to_present": "Look out together. Say what you see once — 'it is raining' — and let them add. Keep it to under a minute.",
            "what_to_observe": "Do they start announcing it before you? Do they notice change from yesterday?",
            "common_mistakes": "Turning it into a quiz. It is an observation, not a test.",
            "materials_needed": "A window",
            "homemade_alternative": "Free. A small set of picture cards can be added later if wanted.",
        },
        "ru": {
            "title": "Погода и время года сегодня",
            "summary": "Каждое утро посмотреть в окно и назвать погоду и время года.",
            "why_it_matters": "Время абстрактно, а погода — нет. Ежедневное наблюдение даёт ребёнку осязаемую опору для понимания смены дней и сезонов.",
            "how_to_present": "Посмотрите в окно вместе. Один раз скажите, что видите: «идёт дождь», — и дайте ребёнку добавить. Уложитесь в минуту.",
            "what_to_observe": "Начинает ли объявлять погоду раньше вас? Замечает ли перемену со вчерашнего дня?",
            "common_mistakes": "Превращать это в опрос. Это наблюдение, а не проверка.",
            "materials_needed": "Окно",
            "homemade_alternative": "Бесплатно. При желании позже можно добавить небольшой набор карточек.",
        },
    },
]

# --- Sensitive periods -------------------------------------------------------

SENSITIVE_PERIODS = [
    {
        "id": 1, "age_min_months": 0, "age_max_months": 48,
        "en": {
            "name": "Movement",
            "description": "An intense drive to refine physical control, from reaching and crawling through to precise, purposeful action.",
            "what_youll_notice": "Repeating the same physical action many times; resisting being carried or contained; climbing everything.",
            "how_to_support": "Floor space, minimal containers, and things heavy enough to be worth carrying.",
        },
        "ru": {
            "name": "Движение",
            "description": "Сильное стремление отточить владение телом — от хватания и ползания до точных осмысленных действий.",
            "what_youll_notice": "Многократное повторение одного движения; сопротивление, когда его несут или ограничивают; желание залезть на всё подряд.",
            "how_to_support": "Свободное место на полу, минимум шезлонгов и манежей, предметы, которые достаточно тяжелы, чтобы их стоило нести.",
        },
    },
    {
        "id": 2, "age_min_months": 0, "age_max_months": 72,
        "en": {
            "name": "Language",
            "description": "Effortless absorption of spoken and later written language, at its most intense in the first three years.",
            "what_youll_notice": "Watching mouths closely; sudden vocabulary jumps; asking the name of everything.",
            "how_to_support": "Real words, said clearly and once. Name what is actually in front of them.",
        },
        "ru": {
            "name": "Язык",
            "description": "Лёгкое, естественное усвоение устной, а затем и письменной речи, наиболее интенсивное в первые три года.",
            "what_youll_notice": "Пристально смотрит на губы говорящего; резкие скачки словаря; спрашивает название всего подряд.",
            "how_to_support": "Настоящие слова, произнесённые чётко и один раз. Называйте то, что действительно перед ним.",
        },
    },
    {
        "id": 3, "age_min_months": 12, "age_max_months": 48,
        "en": {
            "name": "Order",
            "description": "A strong need for consistency and predictability in routines, sequences and where things belong.",
            "what_youll_notice": "Distress when a routine changes or an object is in the wrong place; insisting on doing things in the same sequence.",
            "how_to_support": "Keep placements and routines stable. This is not stubbornness — it is how the world is being organised.",
        },
        "ru": {
            "name": "Порядок",
            "description": "Сильная потребность в постоянстве и предсказуемости распорядка, последовательностей и мест, где лежат вещи.",
            "what_youll_notice": "Расстраивается, когда меняется распорядок или предмет лежит не на месте; настаивает на одной и той же последовательности действий.",
            "how_to_support": "Сохраняйте постоянные места и режим. Это не упрямство, а способ упорядочить мир.",
        },
    },
    {
        "id": 4, "age_min_months": 12, "age_max_months": 30,
        "en": {
            "name": "Small objects",
            "description": "Fascination with tiny details and small items, refining the pincer grasp and close visual attention.",
            "what_youll_notice": "Spotting crumbs and insects you missed; picking at the smallest part of anything.",
            "how_to_support": "Safe small things to handle under supervision, and patience with the pace.",
        },
        "ru": {
            "name": "Мелкие предметы",
            "description": "Увлечённость мельчайшими деталями и маленькими предметами, оттачивающая пинцетный захват и внимание к деталям.",
            "what_youll_notice": "Замечает крошки и насекомых, которых вы не увидели; выковыривает самую мелкую деталь из чего угодно.",
            "how_to_support": "Безопасные мелкие предметы под присмотром и терпение к медленному темпу.",
        },
    },
    {
        "id": 5, "age_min_months": 30, "age_max_months": 72,
        "en": {
            "name": "Refinement of the senses",
            "description": "Heightened interest in comparing and classifying sensory qualities — size, colour, texture, sound, weight.",
            "what_youll_notice": "Sorting spontaneously; noticing small differences; lining things up by size.",
            "how_to_support": "Materials that vary in exactly one quality at a time.",
        },
        "ru": {
            "name": "Утончение чувств",
            "description": "Обострённый интерес к сравнению и классификации сенсорных качеств — размера, цвета, фактуры, звука, веса.",
            "what_youll_notice": "Раскладывает по группам без просьбы; замечает мелкие различия; выстраивает предметы по размеру.",
            "how_to_support": "Материалы, в которых за раз меняется ровно одно свойство.",
        },
    },
    {
        "id": 6, "age_min_months": 18, "age_max_months": 60,
        "en": {
            "name": "Refinement of movement",
            "description": "Interest in precise, purposeful, sequenced action rather than movement for its own sake.",
            "what_youll_notice": "Wanting to do real tasks slowly and properly; frustration at being helped.",
            "how_to_support": "Real tools, sized for their hands, and enough time to finish.",
        },
        "ru": {
            "name": "Утончение движений",
            "description": "Интерес к точным, осмысленным, последовательным действиям, а не к движению ради движения.",
            "what_youll_notice": "Хочет делать настоящие дела медленно и как следует; злится, когда ему помогают.",
            "how_to_support": "Настоящие инструменты по размеру руки и достаточно времени, чтобы закончить.",
        },
    },
    {
        "id": 7, "age_min_months": 18, "age_max_months": 60,
        "en": {
            "name": "Independence",
            "description": "A drive to do things for oneself — dressing, eating, cleaning up.",
            "what_youll_notice": "'Me do it.' Refusing help even when the task is genuinely hard.",
            "how_to_support": "Set the environment up so the answer can be yes: low hooks, small jugs, reachable shelves.",
        },
        "ru": {
            "name": "Самостоятельность",
            "description": "Стремление делать всё самому — одеваться, есть, убирать за собой.",
            "what_youll_notice": "«Я сам». Отказывается от помощи, даже когда задача действительно трудна.",
            "how_to_support": "Организуйте среду так, чтобы можно было сказать «да»: низкие крючки, маленькие кувшины, доступные полки.",
        },
    },
    {
        "id": 8, "age_min_months": 24, "age_max_months": 60,
        "en": {
            "name": "Coordination of movement",
            "description": "Growing ability to combine several refined movements into one purposeful task.",
            "what_youll_notice": "Sustaining longer sequences; carrying full containers without spilling.",
            "how_to_support": "Multi-step practical life work, uninterrupted.",
        },
        "ru": {
            "name": "Координация движений",
            "description": "Растущая способность соединять несколько отточенных движений в одну осмысленную задачу.",
            "what_youll_notice": "Удерживает более длинные последовательности; несёт полные ёмкости, не проливая.",
            "how_to_support": "Многошаговые бытовые дела, которые никто не прерывает.",
        },
    },
    {
        "id": 9, "age_min_months": 30, "age_max_months": 72,
        "en": {
            "name": "Grace and courtesy",
            "description": "Interest in the social forms of a community — greetings, turn-taking, polite requests.",
            "what_youll_notice": "Copying adult social behaviour closely; noticing when someone breaks a social rule.",
            "how_to_support": "Model it, and rehearse specific situations calmly rather than correcting in the moment.",
        },
        "ru": {
            "name": "Вежливость и хорошие манеры",
            "description": "Интерес к социальным формам сообщества — приветствиям, очерёдности, вежливым просьбам.",
            "what_youll_notice": "Точно копирует поведение взрослых; замечает, когда кто-то нарушает социальное правило.",
            "how_to_support": "Показывайте примером и спокойно проигрывайте конкретные ситуации, а не исправляйте в моменте.",
        },
    },
]


# --- Build -------------------------------------------------------------------

def _require(condition: bool, message: str) -> None:
    if not condition:
        raise ValueError(message)


def _validate() -> None:
    """Fail loudly rather than shipping a malformed library."""
    seen_ids = set()
    for a in ACTIVITIES:
        aid = a["id"]
        _require(aid not in seen_ids, f"duplicate activity id {aid}")
        seen_ids.add(aid)
        _require(a["area"] in AREAS, f"activity {aid}: unknown area {a['area']!r}")
        _require(a["mess_level"] in MESS_LEVELS, f"activity {aid}: bad mess_level")
        _require(a["provenance"] in PROVENANCE, f"activity {aid}: bad provenance")
        _require(a["age_min_months"] <= a["age_max_months"], f"activity {aid}: inverted age range")
        if a.get("infant_focus") is not None:
            _require(a["infant_focus"] in INFANT_FOCUS, f"activity {aid}: bad infant_focus")
        for loc in LOCALES:
            _require(loc in a, f"activity {aid}: missing locale {loc!r}")
            for field in ("title", "summary", "why_it_matters", "how_to_present",
                          "what_to_observe", "common_mistakes", "materials_needed",
                          "homemade_alternative"):
                _require(a[loc].get(field), f"activity {aid} [{loc}]: missing {field}")

    period_ids = set()
    for p in SENSITIVE_PERIODS:
        pid = p["id"]
        _require(pid not in period_ids, f"duplicate sensitive period id {pid}")
        period_ids.add(pid)
        for loc in LOCALES:
            _require(loc in p, f"period {pid}: missing locale {loc!r}")
            for field in ("name", "description", "what_youll_notice", "how_to_support"):
                _require(p[loc].get(field), f"period {pid} [{loc}]: missing {field}")


def build_seed() -> dict:
    _validate()

    activities = [
        {
            "id": a["id"],
            "ageMinMonths": a["age_min_months"],
            "ageMaxMonths": a["age_max_months"],
            "area": a["area"],
            "infantFocus": a.get("infant_focus"),
            "sessionMinutes": a["session_minutes"],
            "messLevel": a["mess_level"],
            "provenance": a["provenance"],
            "imageAsset": a.get("image_asset"),
            "imageCredit": a.get("image_credit"),
            "imageLicence": a.get("image_licence"),
        }
        for a in ACTIVITIES
    ]

    activity_texts = [
        {
            "activityId": a["id"],
            "locale": loc,
            "title": a[loc]["title"],
            "summary": a[loc]["summary"],
            "whyItMatters": a[loc]["why_it_matters"],
            "howToPresent": a[loc]["how_to_present"],
            "whatToObserve": a[loc]["what_to_observe"],
            "commonMistakes": a[loc]["common_mistakes"],
            "materialsNeeded": a[loc]["materials_needed"],
            "homemadeAlternative": a[loc]["homemade_alternative"],
            "supervisionNote": a[loc].get("supervision_note"),
        }
        for a in ACTIVITIES
        for loc in LOCALES
    ]

    periods = [
        {
            "id": p["id"],
            "ageMinMonths": p["age_min_months"],
            "ageMaxMonths": p["age_max_months"],
        }
        for p in SENSITIVE_PERIODS
    ]

    period_texts = [
        {
            "periodId": p["id"],
            "locale": loc,
            "name": p[loc]["name"],
            "description": p[loc]["description"],
            "whatYoullNotice": p[loc]["what_youll_notice"],
            "howToSupport": p[loc]["how_to_support"],
        }
        for p in SENSITIVE_PERIODS
        for loc in LOCALES
    ]

    return {
        "schemaVersion": 2,
        "activities": activities,
        "activityTexts": activity_texts,
        "sensitivePeriods": periods,
        "sensitivePeriodTexts": period_texts,
    }


def main() -> None:
    seed = build_seed()
    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(json.dumps(seed, indent=2, ensure_ascii=False), encoding="utf-8")

    by_area: dict[str, int] = {}
    for a in ACTIVITIES:
        by_area[a["area"]] = by_area.get(a["area"], 0) + 1

    print(f"Wrote {OUTPUT_PATH}")
    print(f"  {len(seed['activities'])} activities, {len(seed['activityTexts'])} texts "
          f"({len(LOCALES)} locales)")
    print(f"  {len(seed['sensitivePeriods'])} sensitive periods, "
          f"{len(seed['sensitivePeriodTexts'])} texts")
    print("  by area:")
    for area in AREAS:
        print(f"    {area:22} {by_area.get(area, 0)}")
    missing = [a for a in AREAS if a not in by_area]
    if missing:
        print(f"  WARNING: areas with no activities: {', '.join(missing)}")


if __name__ == "__main__":
    main()
