package ec.edu.espe.medicbyte.services

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ec.edu.espe.medicbyte.models.Medication
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MedicationService(private val context: Context) {
    private val url = "http://10.0.2.2/api/medications"
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    companion object {
        private var instance: MedicationService? = null

        fun getInstance(context: Context): MedicationService {
            if (instance == null) {
                instance = MedicationService(context)
            }

            return instance!!
        }
    }

    suspend fun getAll() = suspendCoroutine<List<Medication>> { cont ->
        val request = StringRequest(Request.Method.GET, url, { response ->
            val medications = mutableListOf<Medication>()
            val items = JSONArray(response)

            for (i in 0 until items.length()) {
                val json = items.getJSONObject(i)
                medications.add(Medication.fromJsonObject(json))

                Log.d("MedicationService", "Medication: ${medications[i]}")
            }

            cont.resume(medications)
        }, { error ->
            cont.resumeWithException(error)
        })

        request.retryPolicy = DefaultRetryPolicy(
            5000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)
    }
}