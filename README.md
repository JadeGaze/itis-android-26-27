# ItisAndroid — Android learning project

A single-module Compose app for the course: two feature areas inside one package
(movies and weather), plus a design system, navigation, ViewModel + StateFlow,
Room and Retrofit.

**No DI framework and no multi-module setup** — on purpose, so that during a
lesson you can see how everything is wired by hand.

## Stack

| Layer | Technologies |
| --- | --- |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| State | ViewModel (`AndroidViewModel`), `StateFlow` + `collectAsState` |
| Database | Room (KSP), Flow queries, one `itis_db` database per app |
| Network | Retrofit + OkHttp + Gson, OpenWeatherMap API |
| Async | Kotlin Coroutines, `viewModelScope`, `flatMapLatest` |

## App navigation

Bottom navigation (4 tabs):

1. **Movies** — a CRUD example on Room: list, details, add.
   It also demonstrates a "bad" screen that loses its state on rotation.
2. **Weather** — the main weather screen: city search, current weather,
   hourly and 5-day forecast.
3. **Cities** — saved cities (reading and deleting rows from Room).
4. **UI Kit** — a design system showcase: colors, typography, buttons,
   cards, state views, spacing.

The day details screen (`weather/day/{date}`) opens from the forecast list
and lives outside the bottom navigation.

## Package structure

```
com.itis.itisandroid
├── MainActivity.kt              // entry point + root navigation
├── data/local/AppDatabase.kt    // the single Room database (movies + weather)
├── data/local/Converters.kt     // TypeConverters: Date, List<String>, enum
├── movie/
│   ├── data/                    // repository, DAO, entity
│   └── presentation/            // models, ViewModel, screens, routes
├── weather/
│   ├── data/                    // Retrofit, OkHttp interceptors, DTOs, cache
│   └── presentation/            // models, ViewModel, screens, components
└── ui/
    ├── theme/                   // Color, Type, Shape, Dimens, Theme
    ├── components/              // shared design-system components
    └── showcase/                // UI Kit screen
```

Layer rule: `presentation` knows nothing about Room or Retrofit; `data` knows
nothing about Compose. The repository is the only place that maps
DTO/Entity → domain model.

## Design system

All tokens live in `ui/theme`:

- `Color.kt` — the palette and semantic light/dark theme pairs;
- `Type.kt` — the full Material 3 scale (display / headline / title / body / label);
- `Shape.kt` — shared corner radii;
- `Dimens.kt` — the 4/8/16/24/32 dp spacing grid;
- `Theme.kt` — `MaterialTheme` + a dynamic color switch (off by default so the
  brand palette looks the same on every device).

Shared components (`ui/components`): `AppTopBar`, `AppBottomBar`, `AppCard`,
`PrimaryButton` / `SecondaryButton` / `DangerButton`, `AppSearchField`,
`SectionHeader`, `StatTile`, `LoadingView` / `ErrorView` / `EmptyView`.

Screens must not contain "magic" colors, font sizes or paddings — everything
comes from the theme and the components.

## Weather: how networking and storage work

```
UI → WeatherViewModel → WeatherRepository ──Retrofit──> OpenWeatherMap
                              │
                              └── Room (slot cache + saved cities)
UI ← StateFlow ← ViewModel ← Flow from Room
```

1. The user searches for a city → `WeatherRepository.refreshForecast()` goes online.
2. A successful response is saved into the `weather_forecast_slots` table.
3. The screen subscribes to `observeForecast()` — data arrives **from the database**,
   so without a network the last successful forecast is still shown.
4. The city is added to `saved_cities`; deleting a city also deletes its cache
   in a single transaction.

3-hour slots are aggregated into days in the repository (min/max temperature,
average humidity, maximum wind).

## Previews and mocks

Previews live in the debug source set: `app/src/debug/java/com/itis/itisandroid/preview/`.

- `MoviePreviewData.kt` / `WeatherPreviewData.kt` — mocks for every state;
- `MovieScreenPreviews.kt` — list (content/empty/dark theme), details, add screen (filled/empty), the "bad" screen;
- `WeatherScreenPreviews.kt` — main screen (content, search, loading, error without data, cache + error, empty, dark theme), day details (with/without data), cities (list/empty);
- `DesignSystemPreview.kt` — the UI Kit screen in light and dark themes.

Open them in Android Studio through the **Preview** window. Previews and mocks
never get into a release build.

## Running the app

1. Open the project in Android Studio (JDK 17, Gradle Wrapper 8.13).
2. Get a free OpenWeatherMap key: https://openweathermap.org/api
3. Add the key to `local.properties` (this file is in `.gitignore` and never
   reaches the repository):

   ```properties
   openweather.api.key=your_key_here
   openweather.base.url=https://api.openweathermap.org/data/2.5/
   ```

   Gradle reads these values and generates `BuildConfig.OPENWEATHER_API_KEY` and
   `BuildConfig.OPENWEATHER_BASE_URL`; the code accesses them through
   `weather/data/OpenWeatherConfig.kt`.

4. Run the app. Without a key the weather screen shows a friendly error
   (HTTP 401) instead of crashing.

## Lesson topics

1. **Compose basics**: declarative UI, recomposition, `Modifier`, lists (`LazyColumn`).
2. **Theme and typography**: design tokens, `MaterialTheme`, light/dark theme,
   and why colors and sizes must not be hardcoded.
3. **Design system**: why shared components exist and how to reuse
   `AppCard`, `PrimaryButton`, `StatTile`.
4. **Navigation**: `NavHost`, route arguments, bottom navigation, deep links.
5. **State**: ViewModel, `StateFlow`, `collectAsState`, and why `remember`
   does not survive rotation (the `BadAddMovieScreen` screen).
6. **Room**: Entity / DAO / Database, Flow queries, transactions, cache,
   `TypeConverters` (`Converters.kt`: `Date`, `List<String>`, enum).
7. **Network**: the Retrofit interface, DTOs and Gson, `suspend` functions,
   error handling, OkHttp interceptors (`OpenWeatherInterceptors.kt`: API key,
   logging).
8. **Offline-first**: the network as a source of updates, the database as the
   source of truth for the UI.
