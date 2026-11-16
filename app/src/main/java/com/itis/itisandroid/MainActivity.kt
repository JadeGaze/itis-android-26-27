package com.itis.itisandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.itis.itisandroid.movie.presentation.navigation.Destinations
import com.itis.itisandroid.movie.presentation.screen.AddMovieScreen
import com.itis.itisandroid.movie.presentation.screen.BadAddMovieScreen
import com.itis.itisandroid.movie.presentation.screen.DetailsScreen
import com.itis.itisandroid.movie.presentation.screen.ListScreen
import com.itis.itisandroid.movie.presentation.viewmodel.MovieViewModel
import com.itis.itisandroid.ui.components.AppBottomBar
import com.itis.itisandroid.ui.components.AppBottomBarItem
import com.itis.itisandroid.ui.showcase.DesignSystemScreen
import com.itis.itisandroid.ui.theme.ItisAndroidTheme
import com.itis.itisandroid.weather.presentation.navigation.WeatherDestinations
import com.itis.itisandroid.weather.presentation.screen.WeatherCitiesScreen
import com.itis.itisandroid.weather.presentation.screen.WeatherDayScreen
import com.itis.itisandroid.weather.presentation.screen.WeatherHomeScreen
import com.itis.itisandroid.weather.presentation.viewmodel.WeatherViewModel

/** Route of the design-system showcase screen. */
private const val UI_KIT_ROUTE = "ui_kit"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItisAndroidTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * Root app navigation.
 *
 * Both ViewModels are created here, at the NavHost level (MovieViewModel
 * and WeatherViewModel). They live as long as the Activity, so state
 * survives screen transitions and configuration changes.
 * No DI: the default ViewModel factory passes Application into
 * AndroidViewModel automatically.
 *
 * The bottom bar is visible only on top-level screens (movie list,
 * weather, cities, UI Kit). On details screens it is hidden.
 */
@Composable
fun AppNavigation(
    movieViewModel: MovieViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val movieState by movieViewModel.uiState.collectAsState()
    val weatherState by weatherViewModel.uiState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val topLevelItems = listOf(
        AppBottomBarItem(
            route = Destinations.LIST_SCREEN_DESTINATION,
            label = stringResource(R.string.nav_movies),
            icon = Icons.Default.PlayArrow,
        ),
        AppBottomBarItem(
            route = WeatherDestinations.HOME,
            label = stringResource(R.string.nav_weather),
            icon = Icons.Default.Place,
        ),
        AppBottomBarItem(
            route = WeatherDestinations.CITIES,
            label = stringResource(R.string.nav_cities),
            icon = Icons.Default.Star,
        ),
        AppBottomBarItem(
            route = UI_KIT_ROUTE,
            label = stringResource(R.string.nav_ui_kit),
            icon = Icons.Default.Info,
        ),
    )

    val showBottomBar = currentRoute in topLevelItems.map { it.route }

    // Navigate to a top-level screen: do not stack back-stack entries,
    // save the tab state and restore it on return.
    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val onTabSelected: (AppBottomBarItem) -> Unit = { item ->
        navigateToTab(item.route)
    }

    Scaffold(
        // Top insets are handled by the screens' own Scaffolds,
        // so the root Scaffold must not add its own.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    items = topLevelItems,
                    currentRoute = currentRoute,
                    onItemSelected = onTabSelected,
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.LIST_SCREEN_DESTINATION,
            modifier = Modifier.padding(innerPadding),
        ) {
            /* ================= MOVIES ================= */

            composable(
                route = Destinations.LIST_SCREEN_DESTINATION,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "https://www.example.com/androidItis/${Destinations.LIST_SCREEN_DESTINATION}"
                    }
                ),
            ) {
                ListScreen(
                    items = movieState.movies,
                    onItemClicked = { movie ->
                        navController.navigate(route = "list/${movie.id}")
                    },
                    onAddMovieClicked = {
                        navController.navigate(route = Destinations.ADD_MOVIE_SCREEN_DESTINATION)
                    },
                )
            }

            composable(
                route = Destinations.DETAILS_SCREEN_DESTINATION,
                arguments = listOf(navArgument(name = Destinations.itemId) {
                    type = NavType.StringType
                }),
            ) { entry ->
                val itemId = entry.arguments?.getString(Destinations.itemId)
                val movie by movieViewModel
                    .observeMovieById(itemId.orEmpty())
                    .collectAsState(initial = null)

                val currentMovie = movie
                if (currentMovie != null && itemId != null) {
                    DetailsScreen(
                        movie = currentMovie,
                        onBackClicked = { navController.popBackStack() },
                        onDeleteClicked = {
                            movieViewModel.deleteMovie(itemId)
                            navController.popBackStack()
                        },
                    )
                }
            }

            composable(route = Destinations.BAD_ADD_MOVIE_SCREEN_DESTINATION) {
                BadAddMovieScreen(
                    onAddMovie = { navController.popBackStack() },
                    onBackClicked = { navController.popBackStack() },
                )
            }

            composable(route = Destinations.ADD_MOVIE_SCREEN_DESTINATION) {
                AddMovieScreen(
                    uiState = movieState,
                    onNameChange = movieViewModel::updateNewMovieName,
                    onDescriptionChange = movieViewModel::updateNewMovieDescription,
                    onAddMovie = {
                        movieViewModel.addMovie()
                        navController.popBackStack()
                    },
                    onBackClicked = { navController.popBackStack() },
                )
            }

            /* ================= WEATHER ================= */

            composable(route = WeatherDestinations.HOME) {
                WeatherHomeScreen(
                    uiState = weatherState,
                    onSearchQueryChange = weatherViewModel::onSearchQueryChange,
                    onSearch = weatherViewModel::search,
                    onRefresh = weatherViewModel::refresh,
                    onCitySelected = weatherViewModel::selectCity,
                    onDayClicked = { day ->
                        navController.navigate(WeatherDestinations.day(day.dateKey))
                    },
                    onOpenCities = {
                        navigateToTab(WeatherDestinations.CITIES)
                    },
                    onErrorDismissed = weatherViewModel::clearError,
                )
            }

            composable(route = WeatherDestinations.CITIES) {
                WeatherCitiesScreen(
                    uiState = weatherState,
                    onCitySelected = { city ->
                        weatherViewModel.selectCity(city)
                        navigateToTab(WeatherDestinations.HOME)
                    },
                    onCityRemoved = weatherViewModel::removeCity,
                    onAddCity = {
                        navigateToTab(WeatherDestinations.HOME)
                    },
                )
            }

            composable(
                route = WeatherDestinations.DAY,
                arguments = listOf(navArgument(name = WeatherDestinations.ARG_DATE) {
                    type = NavType.StringType
                }),
            ) { entry ->
                WeatherDayScreen(
                    uiState = weatherState,
                    dateKey = entry.arguments
                        ?.getString(WeatherDestinations.ARG_DATE)
                        .orEmpty(),
                    onBackClicked = { navController.popBackStack() },
                )
            }

            /* ================= UI KIT ================= */

            composable(route = UI_KIT_ROUTE) {
                DesignSystemScreen()
            }
        }
    }
}
