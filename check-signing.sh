#!/bin/bash

# Скрипт для проверки конфигурации подписи приложения

echo "🔍 Проверка конфигурации подписи приложения..."
echo ""

# Проверяем наличие файла keys.properties
if [ -f "keys.properties" ]; then
    echo "✅ Файл keys.properties найден"
else
    echo "❌ Файл keys.properties НЕ найден"
    echo "   Создайте файл keys.properties на основе keys.properties.example"
    exit 1
fi

# Проверяем наличие keystore файла
KEYSTORE_PATH="/Users/sergeyboykov/Yandex.Disk.localized/Develop/Keys/keys.jks"
if [ -f "$KEYSTORE_PATH" ]; then
    echo "✅ Keystore файл найден: $KEYSTORE_PATH"
else
    echo "❌ Keystore файл НЕ найден: $KEYSTORE_PATH"
    echo "   Убедитесь, что файл существует по указанному пути"
    exit 1
fi

# Проверяем содержимое keys.properties
echo ""
echo "📋 Содержимое keys.properties:"
echo "----------------------------------------"
cat keys.properties | sed 's/=.*/=***/'  # Скрываем значения для безопасности
echo "----------------------------------------"

# Проверяем, что все необходимые свойства присутствуют
echo ""
echo "🔧 Проверка свойств в keys.properties..."

if grep -q "storePassword" keys.properties; then
    echo "✅ storePassword найден"
else
    echo "❌ storePassword НЕ найден"
fi

if grep -q "keyAlias" keys.properties; then
    echo "✅ keyAlias найден"
else
    echo "❌ keyAlias НЕ найден"
fi

if grep -q "keyPassword" keys.properties; then
    echo "✅ keyPassword найден"
else
    echo "❌ keyPassword НЕ найден"
fi

# Проверяем, что файл keys.properties в .gitignore
echo ""
echo "🔒 Проверка безопасности..."
if grep -q "keys.properties" .gitignore; then
    echo "✅ keys.properties добавлен в .gitignore"
else
    echo "❌ keys.properties НЕ добавлен в .gitignore"
fi

if grep -q "*.jks" .gitignore; then
    echo "✅ *.jks добавлен в .gitignore"
else
    echo "❌ *.jks НЕ добавлен в .gitignore"
fi

echo ""
echo "🎯 Проверка завершена!"
echo ""
echo "Для сборки релизной версии выполните:"
echo "   ./gradlew assembleRelease"
echo ""
echo "Для установки на устройство:"
echo "   adb install app/build/outputs/apk/release/app-release.apk" 