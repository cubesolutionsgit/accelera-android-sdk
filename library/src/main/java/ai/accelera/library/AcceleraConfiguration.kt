package ai.accelera.library

/**
 * Класс с конфигурацией работы Accelera
 *
 * @param token токен авторизации запроса
 * @param url адрес подключения к серверу
 * @param userIdInApp идентификатор пользователя для inApp юзера
 */
data class AcceleraConfiguration(
    val token: String,
    val url: String,
    val userIdInApp: String?,
)