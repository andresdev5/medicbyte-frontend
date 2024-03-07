package ec.edu.espe.medicbyte.models

import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Reminder(
    var id: Int? = null,
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
    var intervalValue: Int? = null,
    var intervalUnit: IntervalUnit? = null,
    var intervalCount: Int? = null,
    var maxIntervals: Int? = null,
    var notes: String? = null,
    var status: ReminderStatus? = null,
    var medication: Medication? = null,
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null,
) {
    fun toJsonObject(): JSONObject {
        val reminder = this
        val json = JSONObject()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        json.put("id", reminder.id)
        json.put("start_date", reminder.startDate?.format(dateFormat))
        json.put("end_date", reminder.endDate?.format(dateFormat))
        json.put("interval_value", reminder.intervalValue)
        json.put("interval_unit", reminder.intervalUnit?.name)
        json.put("interval_count", reminder.intervalCount)
        json.put("max_intervals", reminder.maxIntervals)
        json.put("notes", reminder.notes)
        json.put("status", reminder.status?.name)
        json.put("medication", reminder.medication?.toJsonObject())

        return json
    }

    companion object {
        fun fromJsonObject(json: JSONObject): Reminder {
            val id = json.getInt("id")
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val startDate = LocalDateTime.parse(json.getString("start_date"), dateFormat)
            val endDate = LocalDateTime.parse(json.getString("end_date"), dateFormat)
            val intervalValue = json.getInt("interval_value")
            val intervalUnit = IntervalUnit.valueOf(json.getString("interval_unit"))
            val intervalCount = json.getInt("interval_count")
            val maxIntervals = json.getInt("max_intervals")
            val notes = json.getString("notes")
            val status = ReminderStatus.valueOf(json.getString("status"))
            val medication = if (!json.isNull("medication")) {
                Medication.fromJsonObject(json.getJSONObject("medication"))
            } else null
            val createdAt = LocalDateTime.parse(json.getString("created_at"), dateFormat)
            val updatedAt = if (!json.isNull("updated_at")) LocalDateTime.parse(json.getString("updated_at"), dateFormat) else null

            return Reminder(
                id = id,
                startDate = startDate,
                endDate = endDate,
                intervalValue = intervalValue,
                intervalUnit = intervalUnit,
                intervalCount = intervalCount,
                maxIntervals = maxIntervals,
                notes = notes,
                status = status,
                medication = medication,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}