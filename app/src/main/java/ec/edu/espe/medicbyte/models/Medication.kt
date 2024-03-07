package ec.edu.espe.medicbyte.models

import android.graphics.Bitmap
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Medication(
    var id: Int? = null,
    var name: String? = null,
    var dosage: Double? = null,
    var unit: String? = null,
    var image: Bitmap? = null,
    var description: String? = null,
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null,
) {
    override fun toString(): String {
        return name!!
    }

    fun toJsonObject(): JSONObject {
        val medication = this
        val json = JSONObject()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        json.put("id", medication.id)
        json.put("name", medication.name)
        json.put("dosage", medication.dosage)
        json.put("unit", medication.unit)
        json.put("description", medication.description)
        json.put("created_at", medication.createdAt?.format(dateFormat))
        json.put("updated_at", medication.updatedAt?.format(dateFormat))

        return json
    }

    companion object {
        fun fromJsonObject(json: JSONObject): Medication {
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val id = json.getInt("id")
            val name = json.getString("name")
            val dosage = json.getDouble("dosage")
            val unit = json.getString("unit")
            val description = json.getString("description")
            val createdAt = LocalDateTime.parse(json.getString("created_at"), dateFormat)
            val updatedAt = if (!json.isNull("updated_at")) LocalDateTime.parse(json.getString("updated_at"), dateFormat) else null

            val image = if (!json.isNull("image")) {
                val imageBase64 = json.getString("image")
                val bytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else null

            return Medication(
                id = id,
                name = name,
                dosage = dosage,
                unit = unit,
                image = image,
                description = description,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
