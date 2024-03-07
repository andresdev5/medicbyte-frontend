package ec.edu.espe.medicbyte.models

enum class IntervalUnit(private val label: String) {
    MINUTES("minutos"),
    HOURS("horas"),
    DAYS("días"),
    WEEKS("semanas");

    fun toDisplayString(): String {
        return this.label
    }

    companion object {
        fun fromString(value: String): IntervalUnit {
            return when (value) {
                "MINUTOS" -> MINUTES
                "HORAS" -> HOURS
                "DIAS" -> DAYS
                "SEMANAS" -> WEEKS
                else -> throw IllegalArgumentException("Invalid value")
            }
        }
    }
}