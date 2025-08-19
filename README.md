## Money Everyday (XML)

Небольшое офлайн‑приложение для учёта личных финансов. Позволяет:

- автоматически добавлять транзакции из уведомлений банков по пользовательским шаблонам;
- вручную заносить доходы и расходы;
- видеть историю операций и текущую сумму;
- считать «в день» на основе даты последнего сброса;
- переключать светлую/тёмную тему.

<div style="display: flex; gap: 10px; flex-wrap: wrap;">
<img width="200" style="height: auto" alt="Screenshot_Money Everyday XML_2025-08-19_12" src="https://github.com/user-attachments/assets/6de16afd-e58a-49a9-9b70-666ed6008b9c" />
<img width="200" style="height: auto" alt="Screenshot_Money Everyday XML_2025-08-19_13" src="https://github.com/user-attachments/assets/bfce84cd-26ec-480e-9686-ad7478744829" />
<img width="200" style="height: auto" alt="Screenshot_Money Everyday XML_2025-08-19_15" src="https://github.com/user-attachments/assets/4ab53ebe-e44b-4ce5-8ea4-820995bc24a2" />
<img width="200" style="height: auto" alt="Screenshot_Money Everyday XML_2025-08-19_16" src="https://github.com/user-attachments/assets/7a3baeba-60a6-4974-85f1-72bfc6a10922" />
</div>


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
