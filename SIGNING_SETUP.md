# Настройка подписи приложения

## Обзор

Данное руководство описывает процесс настройки подписи Android-приложения для релизных сборок.

## Структура файлов

### keys.properties
Файл с конфиденциальными данными для подписи приложения. **НЕ ДОЛЖЕН** попадать в репозиторий.

```properties
storePassword=your_keystore_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

**Важно:** Не используйте кавычки вокруг значений!

### keys.properties.example
Пример файла конфигурации для других разработчиков.

## Настройка

### 1. Создание keystore

Если у вас еще нет keystore файла, создайте его:

```bash
keytool -genkey -v -keystore keys.jks -keyalg RSA -keysize 2048 -validity 10000 -alias your_key_alias
```

### 2. Настройка файла keys.properties

1. Скопируйте `keys.properties.example` в `keys.properties`
2. Заполните реальными значениями **БЕЗ КАВЫЧЕК**:
   - `storePassword` - пароль keystore файла
   - `keyAlias` - алиас ключа
   - `keyPassword` - пароль ключа

### 3. Размещение keystore файла

В текущей конфигурации keystore должен находиться по пути:
```
/Users/sergeyboykov/Yandex.Disk.localized/Develop/Keys/keys.jks
```

Для изменения пути отредактируйте `app/build.gradle.kts`:

```kotlin
val keystorePath = when {
    file("path/to/your/keys.jks").exists() -> "path/to/your/keys.jks"
    else -> "path/to/your/keys.jks"
}
```

## Конфигурация в build.gradle.kts

```kotlin
val keysFile = rootProject.file("keys.properties")
val properties = Properties()

// Загружаем свойства из файла keys.properties
if (keysFile.exists()) {
    properties.load(keysFile.inputStream())
}

android {
    signingConfigs {
        create("release") {
            val keystorePath = when {
                file("/Users/sergeyboykov/Yandex.Disk.localized/Develop/Keys/keys.jks").exists() -> "/Users/sergeyboykov/Yandex.Disk.localized/Develop/Keys/keys.jks"
                else -> "/Users/sergeyboykov/Yandex.Disk.localized/Develop/Keys/keys.jks"
            }

            storeFile = file(keystorePath)
            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## Безопасность

### Важные моменты:

1. **НЕ коммитьте** `keys.properties` в репозиторий
2. **НЕ коммитьте** keystore файл в репозиторий
3. Храните keystore в безопасном месте
4. Регулярно делайте резервные копии keystore
5. **НЕ используйте кавычки** в значениях `keys.properties`

### .gitignore

Убедитесь, что в `.gitignore` добавлены:

```
keys.properties
*.jks
*.keystore
```

## Сборка релизной версии

### Локальная сборка

```bash
./gradlew assembleRelease
```

### Проверка конфигурации

```bash
./check-signing.sh
```

### Проверка подписи

```bash
# Используя apksigner (рекомендуется)
~/Library/Android/sdk/build-tools/35.0.0/apksigner verify --verbose app-release.apk

# Используя jarsigner (устаревший способ)
jarsigner -verify -verbose -certs app-release.apk
```

### Установка на устройство

```bash
adb install app-release.apk
```

## Устранение неполадок

### Ошибка "keystore password was incorrect"

1. Проверьте правильность пароля в `keys.properties`
2. **Убедитесь, что значения НЕ заключены в кавычки**
3. Убедитесь, что keystore файл не поврежден
4. Проверьте путь к keystore файлу

### Ошибка "Keystore file not found"

1. Проверьте путь к keystore файлу в `build.gradle.kts`
2. Убедитесь, что файл существует по указанному пути
3. Проверьте права доступа к файлу

### Ошибка "Key alias not found"

1. Проверьте правильность `keyAlias` в `keys.properties`
2. Убедитесь, что ключ с таким алиасом существует в keystore

### Ошибка "jar is unsigned" при проверке jarsigner

Это нормально для современных APK, подписанных с помощью APK Signature Scheme v2/v3. Используйте `apksigner` для проверки:

```bash
~/Library/Android/sdk/build-tools/35.0.0/apksigner verify --verbose app-release.apk
```

## Полезные команды

### Просмотр содержимого keystore

```bash
keytool -list -v -keystore keys.jks
```

### Изменение пароля keystore

```bash
keytool -storepasswd -keystore keys.jks
```

### Изменение пароля ключа

```bash
keytool -keypasswd -alias your_key_alias -keystore keys.jks
```

### Проверка подписи APK

```bash
# Современный способ
~/Library/Android/sdk/build-tools/35.0.0/apksigner verify --verbose app-release.apk

# Устаревший способ (может не работать с v2/v3 подписью)
jarsigner -verify -verbose -certs app-release.apk
```

## Статус подписи

После успешной сборки APK должен быть подписан с использованием:
- ✅ APK Signature Scheme v2 (основной)
- ❌ APK Signature Scheme v1 (JAR signing) - устаревший
- ❌ APK Signature Scheme v3 - не используется
- ❌ APK Signature Scheme v4 - не используется

Это нормально для современных Android приложений. 