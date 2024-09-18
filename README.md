# Accelara Library (Android native)

Библиотека Accelara SDK позволяет получать пуши и отправлять ивенты события в систему Accelara

Установка
===

**Шаг 1.** Добавить JitPack репозиторий в ваш build gradle файл:

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**Шаг 2.** Добавить зависимость

```gradle
dependencies {
    implementation 'com.github.cubesolutionsgit:accelera-android-sdk:0.3.19'
}
```

Проверить последние версии либы можно на [JitPack](https://jitpack.io/#cubesolutionsgit/accelera-android-sdk)

# InApp Library (Android native)

Библиотека InApp SDK является отдельной частью общей библиотеки Accelara

## Конфигурация

Для работы библиотеки, нужно внедрить зависимость модуля inapplib:
```groovy
dependencies {
    implementation project(':inapplib')
}
```

## Использование

Корневой класс для работы библиотеки это **AcceleraLib**
Этот класс требует класс конфиг **AcceleraConfig**, в котором содержаться данные 

```kotlin
val accelera = AcceleraLib(
    config = AcceleraConfig(
        token = TEST_TOKEN,
        url = TEST_URL,
        userId = TEST_USER_ID,
    )
)
```
где: 
- token - это токен авторизации запроса (формируется при настройке адаптера Accelera HTTP inApp Adapter), 
- url - адрес подключения к сервису Accelera HTTP inApp Adapter
- userId - идентификатор пользователя (тот самый ID участника, в рамках которого передаются события)

Чтобы получать даннные из библиотеки, необходимо использовать интерфейс **AcceleraDelegate** 
через метод в классе **AcceleraLib** **delegate**

```kotlin
accelera?.delegate = WeakReference(this)
```

Чтобы начать загрузку баннера (проверку доступности для клиента), нужно вызвать метод **loadBanner** в классе **AcceleraLib** 
```kotlin
accelera?.loadBanner(context = this)
```

При успешной загрузки баннера мы получаем колбэк в интерфейсе **AcceleraDelegate** в методе **bannerViewReady**
Параметр bannerView - это view для отображения баннера
Параметр type - это тип отображаемого баннера
```kotlin
override fun bannerViewReady(bannerView: View, type: AcceleraBannerType) {
    // Теперь можно отобразить пришедший view из библиотеки в любом месте приложения
}
```

Если баннера нет, мы получаем колбэк в интерфейсе **AcceleraDelegate** в методе **noBannerView**
```kotlin
override fun noBannerView() {
    
}
```

## События баннера
Если баннер был закрыт, мы получаем колбэк в интерфейсе **AcceleraDelegate** в методе **bannerViewClosed**
```kotlin
override fun bannerViewClosed(): Boolean {
    return true
}
```

Если на баннер нажали, мы получаем колбэк в интерфейсе **AcceleraDelegate** в методе **bannerViewAction**
параметр action - это данные вовзращаемые из кнопки
```kotlin
override fun bannerViewAction(action: String): Boolean {
    return true
}
```
