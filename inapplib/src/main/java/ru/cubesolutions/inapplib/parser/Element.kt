package ru.cubesolutions.inapplib.parser

data class Element(val name: String, val attributes: Map<String, String> = emptyMap()) {
    var children: List<Element> = emptyList()
    var text: String? = null
}
