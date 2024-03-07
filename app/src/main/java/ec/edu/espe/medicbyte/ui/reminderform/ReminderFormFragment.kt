package ec.edu.espe.medicbyte.ui.reminderform

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import ec.edu.espe.medicbyte.databinding.FragmentReminderFormBinding
import ec.edu.espe.medicbyte.models.IntervalUnit
import ec.edu.espe.medicbyte.models.Medication
import ec.edu.espe.medicbyte.models.Reminder
import ec.edu.espe.medicbyte.services.MedicationService
import ec.edu.espe.medicbyte.services.ReminderService
import ec.edu.espe.medicbyte.utils.NotificationUtils
import ec.edu.espe.medicbyte.utils.NotificationWorker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class ReminderFormFragment : Fragment() {
    private var _binding: FragmentReminderFormBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReminderFormBinding.inflate(inflater, container, false)

        binding.etReminderStartDate.setOnFocusChangeListener() { _, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog(binding.etReminderStartDate)
            }
        }

        binding.etReminderEndDate.setOnFocusChangeListener() { _, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog(binding.etReminderEndDate)
            }
        }

        binding.etReminderIntervalValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (binding.etReminderIntervalValue.text.toString().trim().isEmpty()) {
                    binding.etReminderIntervalValue.setText("1")
                } else if (binding.etReminderIntervalValue.text.toString().toInt() < 1) {
                    binding.etReminderIntervalValue.setText("1")
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            try {
                saveReminder()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "No se pudo guardar el recordatorio", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            Log.d("RemindersFragment", "retrieve reminders...")

            try {
                val medications =
                    activity?.let { MedicationService.getInstance(it.applicationContext).getAll() }

                if (medications != null) {
                    val items = medications?.toTypedArray()

                    val adapter = ArrayAdapter<Medication>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        items!!
                    )

                    binding.spnrReminderMedicament.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return binding.root
    }

    private fun saveReminder() {
        val medication = binding.spnrReminderMedicament.selectedItem as Medication
        val startDate = binding.etReminderStartDate.text.toString()
        val endDate = binding.etReminderEndDate.text.toString()
        val intervalValue = binding.etReminderIntervalValue.text.toString().toInt()
        val intervalUnit = binding.spnrReminderIntervalUnit.selectedItem as String
        val notes = binding.tvmReminderNotes.text.toString()

        Log.d("ReminderFormFragment", "Medication: $medication")
        Log.d("ReminderFormFragment", "Start date: $startDate")
        Log.d("ReminderFormFragment", "End date: $endDate")
        Log.d("ReminderFormFragment", "Interval value: $intervalValue")
        Log.d("ReminderFormFragment", "Interval unit: $intervalUnit")
        Log.d("ReminderFormFragment", "Notes: $notes")


        val reminder = Reminder(
            medication = medication,
            startDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay(),
            endDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay(),
            intervalValue = intervalValue,
            intervalUnit = IntervalUnit.fromString(intervalUnit.uppercase()),
            notes = notes
        )

        lifecycleScope.launch {
            try {
                ReminderService.getInstance(requireContext()).save(reminder)

                binding.etReminderStartDate.setText("")
                binding.etReminderEndDate.setText("")
                binding.etReminderIntervalValue.setText("1")
                binding.spnrReminderMedicament.setSelection(0)
                binding.spnrReminderIntervalUnit.setSelection(0)
                binding.tvmReminderNotes.setText("")

                Toast.makeText(requireContext(), "Recordatorio guardado", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "No se pudo guardar el recordatorio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(field: EditText) {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            OnDateSetListener { view: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate =
                    year1.toString() + '/' + (monthOfYear + 1).toString().padStart(2, '0') + '/' + dayOfMonth.toString().padStart(2, '0')
                field.setText(selectedDate)
            }, year, month, day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}