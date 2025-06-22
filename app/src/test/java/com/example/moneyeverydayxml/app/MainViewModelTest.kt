package com.example.moneyeverydayxml.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneyeverydayxml.notification.NotificationPermissionManager
import com.example.moneyeverydayxml.notification.parser.NotificationParserTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MainViewModelTest : KoinTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockNotificationPermissionManager: NotificationPermissionManager

    @MockK
    private lateinit var mockNotificationParserTest: NotificationParserTest

    private lateinit var viewModel: MainViewModel
    private lateinit var context: Context

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        
        context = ApplicationProvider.getApplicationContext()
        
        // Настраиваем моки
        every { mockNotificationPermissionManager.isNotificationServiceEnabled() } returns false
        every { mockNotificationPermissionManager.requestNotificationPermission() } just Runs
        coEvery { mockNotificationParserTest.testNotificationFormats() } just Runs
        coEvery { mockNotificationParserTest.clearTestTransactions() } just Runs
        
        // Настраиваем Koin для тестов
        startKoin {
            modules(
                module {
                    single<NotificationPermissionManager> { mockNotificationPermissionManager }
                }
            )
        }
        
        // Создаем ViewModel
        viewModel = MainViewModel()
    }

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun `checkNotificationPermissions_whenServiceDisabled_shouldSetPermissionRequiredToTrue`() {
        // Given
        every { mockNotificationPermissionManager.isNotificationServiceEnabled() } returns false

        // When
        viewModel.checkNotificationPermissions()

        // Then
        val result = viewModel.notificationPermissionRequired.getOrAwaitValue()
        assertTrue(result)
    }

    @Test
    fun `checkNotificationPermissions_whenServiceEnabled_shouldSetPermissionRequiredToFalse`() {
        // Given
        every { mockNotificationPermissionManager.isNotificationServiceEnabled() } returns true

        // When
        viewModel.checkNotificationPermissions()

        // Then
        val result = viewModel.notificationPermissionRequired.getOrAwaitValue()
        assertFalse(result)
    }

    @Test
    fun `requestNotificationPermission_shouldCallPermissionManager`() {
        // When
        viewModel.requestNotificationPermission()

        // Then
        verify { mockNotificationPermissionManager.requestNotificationPermission() }
    }

    @Test
    fun `runNotificationTests_whenSuccessful_shouldPostSuccessResult`() = runTest {
        // Given
        coEvery { mockNotificationParserTest.testNotificationFormats() } just Runs

        // When
        viewModel.runNotificationTests()

        // Then
        val result = viewModel.testNotificationResult.getOrAwaitValue()
        assertTrue(result is MainViewModel.TestNotificationResult.Success)
        assertEquals("Тесты уведомлений выполнены! Проверьте логи", (result as MainViewModel.TestNotificationResult.Success).message)
    }

    @Test
    fun `runNotificationTests_whenException_shouldPostErrorResult`() = runTest {
        // Given
        coEvery { mockNotificationParserTest.testNotificationFormats() } throws RuntimeException("Test error")

        // When
        viewModel.runNotificationTests()

        // Then
        val result = viewModel.testNotificationResult.getOrAwaitValue()
        assertTrue(result is MainViewModel.TestNotificationResult.Error)
        assertTrue((result as MainViewModel.TestNotificationResult.Error).message.contains("Test error"))
    }

    @Test
    fun `showTestTransactionsInfo_shouldPostInfoResult`() {
        // When
        viewModel.showTestTransactionsInfo()

        // Then
        val result = viewModel.testNotificationResult.getOrAwaitValue()
        assertTrue(result is MainViewModel.TestNotificationResult.Info)
        assertTrue((result as MainViewModel.TestNotificationResult.Info).message.contains("Тестовые транзакции сохранены"))
    }

    @Test
    fun `clearTestTransactions_whenSuccessful_shouldPostInfoResult`() = runTest {
        // Given
        coEvery { mockNotificationParserTest.clearTestTransactions() } just Runs

        // When
        viewModel.clearTestTransactions()

        // Then
        val result = viewModel.testNotificationResult.getOrAwaitValue()
        assertTrue(result is MainViewModel.TestNotificationResult.Info)
        assertTrue((result as MainViewModel.TestNotificationResult.Info).message.contains("помечены для удаления"))
    }

    @Test
    fun `clearTestTransactions_whenException_shouldPostErrorResult`() = runTest {
        // Given
        coEvery { mockNotificationParserTest.clearTestTransactions() } throws RuntimeException("Clear error")

        // When
        viewModel.clearTestTransactions()

        // Then
        val result = viewModel.testNotificationResult.getOrAwaitValue()
        assertTrue(result is MainViewModel.TestNotificationResult.Error)
        assertTrue((result as MainViewModel.TestNotificationResult.Error).message.contains("Clear error"))
    }

    @Test
    fun `updateCalculatorData_shouldPostTrue`() {
        // When
        viewModel.updateCalculatorData()

        // Then
        val result = viewModel.calculatorDataUpdated.getOrAwaitValue()
        assertTrue(result)
    }

    @Test
    fun `isDebugMode_withDebugContext_shouldReturnTrue`() {
        // Given
        val debugContext = mockk<Context> {
            every { applicationInfo } returns mockk {
                every { flags } returns android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
            }
        }

        // When
        val result = viewModel.isDebugMode(debugContext)

        // Then
        assertTrue(result)
    }

    @Test
    fun `isDebugMode_withReleaseContext_shouldReturnFalse`() {
        // Given
        val releaseContext = mockk<Context> {
            every { applicationInfo } returns mockk {
                every { flags } returns 0
            }
        }

        // When
        val result = viewModel.isDebugMode(releaseContext)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isDebugMode_withException_shouldReturnFalse`() {
        // Given
        val exceptionContext = mockk<Context> {
            every { applicationInfo } throws RuntimeException("Test exception")
        }

        // When
        val result = viewModel.isDebugMode(exceptionContext)

        // Then
        assertFalse(result)
    }
}

/**
 * Расширение для получения значения из LiveData в тестах
 */
fun <T> LiveData<T>.getOrAwaitValue(): T {
    var data: T? = null
    val latch = java.util.concurrent.CountDownLatch(1)
    val observer = androidx.lifecycle.Observer<T> { o ->
        data = o
        latch.countDown()
    }
    this.observeForever(observer)
    latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
    this.removeObserver(observer)
    return data!!
} 