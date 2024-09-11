package ai.accelera.library

/**
 * Класс с конфигурацией работы Accelera
 *
 * @param token токен авторизации запроса
 * @param url адрес подключения к серверу
 * @param userId идентификатор пользователя
 */
data class AcceleraConfiguration(
    val token: String,
    val url: String,
    val userId: String?,
)