# Руководство по тестированию проекта MoneyEverydayXML

## Обзор

Этот документ объясняет принципы тестирования в Android-приложении и показывает примеры тестов для проекта MoneyEverydayXML.

## Типы тестов

### 1. Unit тесты (Модульные тесты)
- **Расположение**: `app/src/test/`
- **Назначение**: Тестирование отдельных компонентов (классов, функций)
- **Зависимости**: JUnit, MockK, Coroutines Test
- **Время выполнения**: Быстро (миллисекунды)

### 2. Integration тесты (Интеграционные тесты)
- **Расположение**: `app/src/androidTest/`
- **Назначение**: Тестирование взаимодействия между компонентами
- **Зависимости**: Espresso, AndroidX Test
- **Время выполнения**: Средне (секунды)

## Примеры тестов

### Тест для ViewModel (CalculatorViewModelTest.kt)

```kotlin
@Test
fun `increaseAction should add amount to summary and save transaction`() = runTest {
    // Arrange (Подготовка)
    val initialAmount = BigDecimal("100.00")
    val increaseAmount = BigDecimal("50.00")
    val expectedFinalAmount = BigDecimal("150.00")
    
    val mockMainData = MainData(
        id = 0,
        dateOfClear = 0L,
        summaryAmount = initialAmount
    )
    
    coEvery { mockRepository.loadMainData() } returns mockMainData
    coEvery { mockRepository.addTransactionAndUpdateSummary(any()) } returns Unit
    
    // Act (Действие)
    viewModel.refreshData()
    testDispatcher.scheduler.advanceUntilIdle()
    
    viewModel.increaseAction(increaseAmount)
    testDispatcher.scheduler.advanceUntilIdle()
    
    // Assert (Проверка)
    assertEquals(expectedFinalAmount.toString(), viewModel.sumAmount.value)
    assertEquals("150.00", viewModel.byDay.value)
}
```

### Тест для модели данных (TransactionTest.kt)

```kotlin
@Test
fun `Transaction should be created with correct properties`() {
    // Arrange
    val id = 1L
    val time = 1640995200000L
    val date = "01 янв, суббота, 00:00"
    val count = "100.50"
    val description = "Тестовая транзакция"

    // Act
    val transaction = Transaction(
        id = id,
        time = time,
        date = date,
        count = count,
        description = description
    )

    // Assert
    assertEquals(id, transaction.id)
    assertEquals(time, transaction.time)
    assertEquals(date, transaction.date)
    assertEquals(count, transaction.count)
    assertEquals(description, transaction.description)
}
```

## Структура теста (AAA Pattern)

### 1. Arrange (Подготовка)
- Создание тестовых данных
- Настройка моков (заглушек)
- Инициализация объектов

### 2. Act (Действие)
- Выполнение тестируемого метода
- Вызов функции, которую тестируем

### 3. Assert (Проверка)
- Проверка ожидаемых результатов
- Сравнение с ожидаемыми значениями

## Ключевые концепции

### Моки (MockK)
```kotlin
// Создание мока
private lateinit var mockRepository: RepositoryInterface
mockRepository = mockk()

// Настройка поведения мока
coEvery { mockRepository.loadMainData() } returns mockMainData
coEvery { mockRepository.addTransactionAndUpdateSummary(any()) } returns Unit
```

### Тестирование корутин
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun testCoroutine() = runTest {
        // Тест корутины
        testDispatcher.scheduler.advanceUntilIdle()
    }
}
```

### Тестирование LiveData
```kotlin
@get:Rule
val instantTaskExecutorRule = InstantTaskExecutorRule()

@Test
fun testLiveData() {
    assertEquals("expected_value", viewModel.someLiveData.value)
}
```

## Запуск тестов

### Запуск всех unit тестов
```bash
./gradlew test
```

### Запуск тестов конкретного класса
```bash
./gradlew test --tests CalculatorViewModelTest
```

### Запуск интеграционных тестов
```bash
./gradlew connectedAndroidTest
```

## Лучшие практики

1. **Именование тестов**: Используйте описательные имена в формате `should_do_something_when_condition`
2. **Изоляция**: Каждый тест должен быть независимым
3. **Один тест - одна проверка**: Тестируйте одну функциональность за раз
4. **Моки**: Используйте моки для внешних зависимостей
5. **Корутины**: Всегда используйте `runTest` для тестирования корутин

## Покрытие тестами

Для проверки покрытия кода тестами:
```bash
./gradlew testDebugUnitTestCoverage
```

Результаты будут в: `app/build/reports/coverage/debug/index.html`

## Следующие шаги

1. Добавить тесты для всех ViewModels
2. Создать тесты для Repository
3. Добавить интеграционные тесты для UI
4. Настроить CI/CD для автоматического запуска тестов 