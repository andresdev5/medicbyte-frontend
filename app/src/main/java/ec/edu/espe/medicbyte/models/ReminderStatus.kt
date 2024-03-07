package ec.edu.espe.medicbyte.models

enum class ReminderStatus(private val label: String) {
    ACTIVE("Activo"),
    COMPLETED("Completado");

    fun toDisplayString(): String {
        return this.label
    }
}