package ec.edu.espe.medicbyte.ui.reminders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ec.edu.espe.medicbyte.R
import ec.edu.espe.medicbyte.adapters.RemindersListAdapter
import ec.edu.espe.medicbyte.databinding.FragmentRemindersBinding
import ec.edu.espe.medicbyte.models.IntervalUnit
import ec.edu.espe.medicbyte.models.Medication
import ec.edu.espe.medicbyte.models.Reminder
import ec.edu.espe.medicbyte.services.ReminderService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RemindersFragment : Fragment() {
    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val remindersViewModel =
            ViewModelProvider(this).get(RemindersViewModel::class.java)

        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadReminders()

        return root
    }

    private fun loadReminders() {
        lifecycleScope.launch {
            Log.d("RemindersFragment", "retrieve reminders...")

            try {
                val reminders =
                    activity?.let { ReminderService.getInstance(it.applicationContext).getAll() }

                Log.d("RemindersFragment", "reminders: $reminders")

                val layoutManager = LinearLayoutManager(activity)
                val adapter = reminders?.let { RemindersListAdapter(it) }
                binding.remindersListRecyclerView.layoutManager = layoutManager
                binding.remindersListRecyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}