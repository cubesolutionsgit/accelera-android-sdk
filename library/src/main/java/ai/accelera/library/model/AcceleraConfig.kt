package ai.accelera.library.model

/**
 * Класс с конфигурацией работы Accelera
 *
 * @param token токен авторизации запроса
 * @param url адрес подключения к серверу
 * @param userId идентификатор пользователя
 */
data class AcceleraConfig(val token: String, val url: String, val userId: String)