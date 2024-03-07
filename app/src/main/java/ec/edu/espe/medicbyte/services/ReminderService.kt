package ec.edu.espe.medicbyte.services

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ec.edu.espe.medicbyte.models.Reminder
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class ReminderService(private val context: Context) {
    private val url = "http://10.0.2.2/api/reminders"
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    companion object {
        private var instance: ReminderService? = null

        fun getInstance(context: Context): ReminderService {
            if (instance == null) {
                instance = ReminderService(context)
            }

            return instance!!
        }
    }

    suspend fun getAll() = suspendCoroutine<List<Reminder>> { cont ->
        val request = StringRequest(Request.Method.GET, url, { response ->
            val reminders = mutableListOf<Reminder>()
            val items = JSONArray(response)

            for (i in 0 until items.length()) {
                val json = items.getJSONObject(i)
                reminders.add(Reminder.fromJsonObject(json))

                Log.d("ReminderService", "Reminder: ${reminders[i]}")
            }

            cont.resume(reminders)
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

    suspend fun getById(id: Int) = suspendCoroutine<Reminder> { cont ->
        val request = JsonObjectRequest(Request.Method.GET, "$url/$id", null, { response ->
            cont.resume(Reminder.fromJsonObject(response))
        }, { error ->
            cont.resumeWithException(error)
        })

        requestQueue.add(request)
    }

    suspend fun save(reminder: Reminder) = suspendCoroutine<Boolean> { cont ->
        Log.d("ReminderService", "Saving reminder: $reminder")

        val request = JsonObjectRequest(Request.Method.POST, url, reminder.toJsonObject(), { response ->
            Log.d("ReminderService", "Reminder saved: $response")
            cont.resume(true)
        }, { error ->
            val responseData = String(error.networkResponse.data, Charsets.UTF_8)
            Log.e("ReminderService", "Error response from server: $responseData")

            cont.resumeWithException(error)
        })

        requestQueue.add(request)
    }
}