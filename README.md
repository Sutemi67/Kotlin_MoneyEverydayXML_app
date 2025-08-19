## Money Everyday (XML)

Небольшое офлайн‑приложение для учёта личных финансов. Позволяет:

- автоматически добавлять транзакции из уведомлений банков по пользовательским шаблонам;
- вручную заносить доходы и расходы;
- видеть историю операций и текущую сумму;
- считать «в день» на основе даты последнего сброса;
- переключать светлую/тёмную тему.

![](https://drive.google.com/uc?export=view&id=1EfWEugJRi92VB9_lomgX_YX9suXmYYAw) ![](https://drive.google.com/uc?export=view&id=1ZUV9De-JYLErtiooaAw3Wk6CItY3LS7P)

### Основные возможности

- Автопарсинг уведомлений: `NotificationListenerService` перехватывает уведомления, `CustomNotificationParser` ищет сумму по regex и определяет знак по шаблону (доход/расход).
- Пользовательские шаблоны: вкладка «Шаблоны» — ключевые слова через запятую и тип транзакции. Активные шаблоны хранятся в БД и применяются ко всем новым уведомлениям. Подробнее см. `PATTERNS_README.md`.
- Калькулятор: быстрый ручной ввод «+»/«−», расчёт суммы «в день» от даты последнего сброса.
- История: список операций с удалением, автопрокрутка к новым элементам.
- Сброс статистики: обнуляет сумму, сохраняет запись о сбросе и фиксирует дату сброса.
- Темы: переключение в меню `Toolbar`.

### Технологический стек

- Язык: Kotlin
- Архитектура: MVVM
- DI: Koin (`app/di` — `appModule`, `dataModule`, `notificationModule`)
- БД: Room (Entity/DAO/Migration), `Flow` для подписок
- Асинхронность: Kotlin Coroutines (`viewModelScope`), `LiveData`
- UI: ViewBinding, Material Components, AppCompat, ConstraintLayout
- Сервисы: `NotificationListenerService` (прослушивание уведомлений)
- Аналитика: Firebase Analytics,
- Min/Target/Compile SDK: 28/34/35

### Архитектура и поток данных

1. Уведомление → `NotificationListenerService` → извлечение `title/text` → `CustomNotificationParser.parseNotification`.
2. Парсер подбирает подходящий шаблон (`NotificationPatternDao.getAllPatterns()`), извлекает сумму по regex и задаёт знак как доход/расход.
3. В случае успеха: через `RepositoryInterface.addTransactionAndUpdateSummary` сохраняется операция и обновляется сумма. Отправляется broadcast `ACTION_TRANSACTION_ADDED`.
4. `MainActivity` принимает broadcast и триггерит обновление экранов «История»/«Калькулятор» через `MainViewModel`.

### Структура проекта (основные директории)

- `app/src/main/java/com/example/moneyeverydayxml/app` — точка входа (`App.kt` c Koin), `MainActivity`, навигация вкладок, тема (`ThemeManager`), `MainViewModel`.
- `core/domain` — модели (`MainData`, `NotificationPattern`, `Transaction`), `RepositoryInterface`.
- `core/data` — `Repository` (работа с Room, агрегирование бизнес‑логики, транзакции БД).
- `history/data` — Room: `Database`, `DatabaseDao`, `NotificationPatternDao`, `TransactionEntity`, конвертеры.
- `history/ui` — экран истории: `HistoryFragment`, `HistoryViewModel`, адаптеры RV.
- `calculator/ui` — калькулятор: `CalculatorFragment`, `CalculatorViewModel`.
- `notification` — `NotificationListenerService`, `NotificationPermissionManager`.
- `notification/parser` — `CustomNotificationParser` (ключевые слова, regex извлечения сумм, знак).
- `patterns/ui` — экран шаблонов: `PatternsFragment`, `PatternsViewModel`, `PatternsAdapter`.

Ресурсы UI расположены в `app/src/main/res` (layouts, drawables, menu, темы, строки).

### Разрешения и требования

В `AndroidManifest.xml`:

- `android.permission.BIND_NOTIFICATION_LISTENER_SERVICE` (служба уведомлений)
- `android.permission.POST_NOTIFICATIONS`
- `android.permission.INTERNET`

При первом запуске приложению нужно вручную выдать доступ к чтению уведомлений (снэкбар предложит перейти в настройки).