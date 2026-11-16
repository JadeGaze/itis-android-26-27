# ItisAndroid — учебный проект по Android-разработке

Одномодульное Compose-приложение для занятий: два feature-модуля в одном пакете
(фильмы и погода), дизайн-система, навигация, ViewModel + StateFlow, Room и Retrofit.

**Без DI-фреймворков и без мультимодульности** — намеренно, чтобы на занятии
было видно, как всё связано руками.

## Стек

| Слой | Технологии |
| --- | --- |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Состояние | ViewModel (`AndroidViewModel`), `StateFlow` + `collectAsState` |
| База данных | Room (KSP), Flow-запросы, одна база `itis_db` на приложение |
| Сеть | Retrofit + Gson, OpenWeatherMap API |
| Асинхронность | Kotlin Coroutines, `viewModelScope`, `flatMapLatest` |

## Навигация приложения

Нижняя навигация (4 вкладки):

1. **Movies** — CRUD-пример на Room: список, детали, добавление.
   Здесь же демонстрация «плохого» экрана, который теряет состояние при повороте.
2. **Weather** — главный экран погоды: поиск города, текущая погода,
   почасовой и 5-дневный прогноз.
3. **Cities** — сохранённые города (чтение/удаление строк из Room).
4. **UI Kit** — витрина дизайн-системы: цвета, типографика, кнопки,
   карточки, состояния, отступы.

Экран деталей дня (`weather/day/{date}`) открывается из списка прогноза
и живёт вне нижней навигации.

## Структура пакетов

```
com.itis.itisandroid
├── MainActivity.kt              // точка входа + корневая навигация
├── data/local/AppDatabase.kt    // единая Room-база (movies + weather)
├── data/local/Converters.kt     // TypeConverters: Date, List<String>, enum
├── movie/
│   ├── data/                    // репозиторий, DAO, Entity
│   └── presentation/            // модели, ViewModel, экраны, маршруты
├── weather/
│   ├── data/                    // Retrofit, OkHttp-интерсепторы, DTO, кэш
│   └── presentation/            // модели, ViewModel, экраны, компоненты
└── ui/
    ├── theme/                   // Color, Type, Shape, Dimens, Theme
    ├── components/              // общие компоненты дизайн-системы
    └── showcase/                // экран UI Kit
```

Правило слоёв: `presentation` не знает про Room и Retrofit; `data` не знает
про Compose. Репозиторий — единственное место маппинга DTO/Entity → доменная модель.

## Дизайн-система

Все токены собраны в `ui/theme`:

- `Color.kt` — палитра и семантические пары светлой/тёмной темы;
- `Type.kt` — полная шкала Material 3 (display / headline / title / body / label);
- `Shape.kt` — единые скругления;
- `Dimens.kt` — сетка отступов 4/8/16/24/32 dp;
- `Theme.kt` — `MaterialTheme` + переключатель dynamic color (по умолчанию выключен,
  чтобы палитра бренда была видна на любом устройстве).

Общие компоненты (`ui/components`): `AppTopBar`, `AppBottomBar`, `AppCard`,
`PrimaryButton` / `SecondaryButton` / `DangerButton`, `AppSearchField`,
`SectionHeader`, `StatTile`, `LoadingView` / `ErrorView` / `EmptyView`.

В экранах не должно быть «магических» цветов, размеров шрифта и отступов —
всё берётся из темы и компонентов.

## Погода: как устроена работа с сетью и БД

```
UI → WeatherViewModel → WeatherRepository ──Retrofit──> OpenWeatherMap
                              │
                              └── Room (кеш слотов + сохранённые города)
UI ← StateFlow ← ViewModel ← Flow из Room
```

1. Пользователь ищет город → `WeatherRepository.refreshForecast()` идёт в сеть.
2. Успешный ответ сохраняется в таблицу `weather_forecast_slots`.
3. Экран подписан на `observeForecast()` — данные приходят **из базы**,
   поэтому при отсутствии сети показывается последний успешный прогноз.
4. Город добавляется в `saved_cities`; удаление города удаляет и его кеш
   одной транзакцией.

3-часовые слоты агрегируются в дни уже в репозитории (min/max температура,
средняя влажность, максимальный ветер).

## Превью и моки

Превью лежат в debug-наборе исходников: `app/src/debug/java/com/itis/itisandroid/preview/`.

- `MoviePreviewData.kt` / `WeatherPreviewData.kt` — моки для всех состояний;
- `MovieScreenPreviews.kt` — список (контент/пусто/тёмная тема), детали, добавление (заполненное/пустое), «плохой» экран;
- `WeatherScreenPreviews.kt` — главный экран (контент, поиск, загрузка, ошибка без данных, кеш + ошибка, пусто, тёмная тема), детали дня (есть/нет данных), города (список/пусто);
- `DesignSystemPreview.kt` — экран UI Kit в светлой и тёмной теме.

Открываются в Android Studio через окно **Preview**. В release-сборку превью и моки не попадают.

## Запуск

1. Открыть проект в Android Studio (JDK 17, Gradle Wrapper 8.13).
2. Получить бесплатный ключ OpenWeatherMap: https://openweathermap.org/api
3. Добавить ключ в `local.properties` (файл в `.gitignore`, в репозиторий не попадает):

   ```properties
   openweather.api.key=ваш_ключ
   openweather.base.url=https://api.openweathermap.org/data/2.5/
   ```

   Gradle читает эти значения и генерирует `BuildConfig.OPENWEATHER_API_KEY` и
   `BuildConfig.OPENWEATHER_BASE_URL`; код обращается к ним через
   `weather/data/OpenWeatherConfig.kt`.

4. Запустить приложение. Без ключа экран погоды покажет понятную ошибку
   (HTTP 401), а не упадёт.

## Темы для занятия

1. **Compose-основы**: декларативный UI, recomposition, `Modifier`, списки (`LazyColumn`).
2. **Тема и типографика**: design tokens, `MaterialTheme`, светлая/тёмная тема,
   почему нельзя хардкодить цвета и размеры.
3. **Дизайн-система**: зачем общие компоненты, как переиспользовать
   `AppCard`, `PrimaryButton`, `StatTile`.
4. **Навигация**: `NavHost`, аргументы маршрутов, нижняя навигация, deep link.
5. **Состояние**: ViewModel, `StateFlow`, `collectAsState`, почему `remember`
   не переживает поворот (экран `BadAddMovieScreen`).
6. **Room**: Entity / DAO / Database, Flow-запросы, транзакции, кеш,
   `TypeConverters` (`Converters.kt`: `Date`, `List<String>`, enum).
7. **Сеть**: Retrofit-интерфейс, DTO и Gson, `suspend`-функции, обработка
   ошибок, OkHttp-интерсепторы (`OpenWeatherInterceptors.kt`: ключ, логирование).
8. **Offline-first**: сеть как источник обновлений, база как источник истины для UI.

## Возможные упражнения

- Добавить экран настроек единиц измерения (°C / °F) и сохранить выбор в DataStore.
- Показать влажность/ветер на экране деталей дня графиком.
- Сделать pull-to-refresh на главном экране погоды.
- Добавить поиск города по геолокации (`FusedLocationProvider`).
- Задействовать готовые конвертеры из `Converters.kt`: сохранить жанры фильма
  как `List<String>` и единицу измерения города как `TemperatureUnit`.
- Написать network interceptor, который повторяет запрос один раз при `IOException`
  или ответе `5xx` (retry), и сравнить его с application-интерсепторами.
- Перевести экраны фильмов на компоненты дизайн-системы и вынести строки в `strings.xml`.

## Ограничения учебного проекта

- `fallbackToDestructiveMigration()` — при смене схемы база пересоздаётся.
  В продакшене нужны `Migration`.
- API-ключ берётся из `local.properties` и попадает в `BuildConfig`, то есть
  всё равно компилируется в APK. В реальном проекте ключ держат на бэкенде.
- Эмодзи вместо картинок погоды, чтобы не подключать ассеты и Coil.
- DI и мультимодульность не используются сознательно.
