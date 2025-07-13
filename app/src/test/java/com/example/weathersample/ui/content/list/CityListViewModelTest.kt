package com.example.weathersample.ui.content.list

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.weathersample.data.list.CityListRepository
import com.example.weathersample.data.list.CitySearchResponseItem
import com.example.weathersample.ui.content.UiState
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class CityListViewModelTest {

    private lateinit var viewModel: CityListViewModel
    private lateinit var repository: CityListRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        savedStateHandle = SavedStateHandle()
        viewModel = CityListViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCityQuery initial state`() = runTest {
        assertEquals("", viewModel.cityQuery.value)
    }

    @Test
    fun `getCityQuery state update on query change`() = runTest {
        viewModel.onAction(CityListUiAction.OnQueryChange("Berlin"))
        assertEquals("Berlin", viewModel.cityQuery.value)
    }

    @Test
    fun `getEventFlow no initial events`() = runTest {
        viewModel.eventFlow.test {
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getEventFlow emits city selected event`() = runTest {
        viewModel.eventFlow.test {
            viewModel.onAction(CityListUiAction.OnCitySelected("London"))
            assertEquals(CityListViewModel.CityListUiEvent.OnCitySelected("London"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCityList initial state`() = runTest {
        assertEquals(UiState.Idle, viewModel.cityList.value)
    }

    @Test
    fun `getCityList loading state on non blank query`() = runTest {
        coEvery { repository.searchForCity(any()) } returns Response.success(emptyList())
        viewModel.onAction(CityListUiAction.OnQueryChange("Mumbai"))
        viewModel.cityList.test {
            awaitItem()
            assertEquals(UiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repository.searchForCity("Mumbai") }
    }

    @Test
    fun `getCityList success state on successful repository response`() = runTest {
        val result = listOf(CitySearchResponseItem("Paris"))
        coEvery { repository.searchForCity(any()) } returns Response.success(result)
        viewModel.onAction(CityListUiAction.OnQueryChange("Paris"))
        viewModel.cityList.test {
            awaitItem()
            awaitItem()
            assertEquals(UiState.Success(result), awaitItem())
        }
        coVerify { repository.searchForCity("Paris") }
    }

    @Test
    fun `getCityList error state on repository error response`() = runTest {
        coEvery { repository.searchForCity(any()) } returns Response.error(400, "Bad Request".toResponseBody())
        viewModel.onAction(CityListUiAction.OnQueryChange("Berlin"))
        viewModel.cityList.test {
            awaitItem()
            awaitItem()
            assertIs<UiState.Error>(awaitItem())
        }
        coVerify { repository.searchForCity("Berlin") }
    }

    @Test
    fun `getCityList error state on successful response with null body`() = runTest {
        coEvery { repository.searchForCity("Tokyo") } returns Response.success(null)
        viewModel.onAction(CityListUiAction.OnQueryChange("Tokyo"))
        viewModel.cityList.test {
            awaitItem()
            awaitItem()
            assertIs<UiState.Error>(awaitItem())
        }
        coVerify { repository.searchForCity("Tokyo") }
    }

    @Test
    fun `getCityList idle state on blank query`() = runTest {
        viewModel.cityList.test {
            viewModel.onAction(CityListUiAction.OnQueryChange(" "))
            advanceTimeBy(400)
            assertEquals(UiState.Idle, awaitItem())
        }
    }

    @Test
    fun `getCityList debounce effect on rapid query changes`() = runTest {
        coEvery { repository.searchForCity(any()) } returns Response.success(emptyList())
        viewModel.onAction(CityListUiAction.OnQueryChange("a"))
        viewModel.onAction(CityListUiAction.OnQueryChange("ab"))
        viewModel.onAction(CityListUiAction.OnQueryChange("abc"))
        viewModel.cityList.test {
            advanceTimeBy(400)
            cancelAndConsumeRemainingEvents()
        }
        coVerify(exactly = 1) { repository.searchForCity("abc") }
    }

    @Test
    fun `getCityList distinctUntilChanged effect on identical query changes`() = runTest {
        coEvery { repository.searchForCity(any()) } returns Response.success(emptyList())
        viewModel.onAction(CityListUiAction.OnQueryChange("Delhi"))
        viewModel.onAction(CityListUiAction.OnQueryChange("Delhi"))
        viewModel.cityList.test {
            advanceTimeBy(400)
            cancelAndConsumeRemainingEvents()
        }
        coVerify(exactly = 1) { repository.searchForCity("Delhi") }
    }

    @Test
    fun `getCityList flatMapLatest cancels previous searches`() = runTest {
        val firstResponse = CompletableDeferred<Response<List<CitySearchResponseItem>>>()
        val secondResponse = Response.success(listOf(CitySearchResponseItem("New")))

        coEvery { repository.searchForCity("A") } coAnswers { firstResponse.await() }
        coEvery { repository.searchForCity("B") } returns secondResponse
        viewModel.cityList.test {
            viewModel.onAction(CityListUiAction.OnQueryChange("A"))
            advanceTimeBy(100)
            viewModel.onAction(CityListUiAction.OnQueryChange("B"))
            advanceTimeBy(400)
            awaitItem()
            awaitItem()
            assertEquals(UiState.Success(secondResponse.body()!!), awaitItem())
        }
        coVerify(exactly = 1) { repository.searchForCity(any()) }
    }

    @Test
    fun `getCityList state preservation during configuration changes`() = runTest {
        savedStateHandle["city_query"] = "SavedCity"
        val newViewModel = CityListViewModel(repository, savedStateHandle)
        assertEquals("SavedCity", newViewModel.cityQuery.value)
    }

    @Test
    fun `onAction OnQueryChange updates cityQuery`() = runTest {
        viewModel.onAction(CityListUiAction.OnQueryChange("Kolkata"))
        assertEquals("Kolkata", viewModel.cityQuery.value)
    }

    @Test
    fun `onAction OnCitySelected sends event`() = runTest {
        viewModel.eventFlow.test {
            viewModel.onAction(CityListUiAction.OnCitySelected("Chennai"))
            assertEquals(CityListViewModel.CityListUiEvent.OnCitySelected("Chennai"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}