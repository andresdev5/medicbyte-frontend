package ec.edu.espe.medicbyte.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.medicbyte.R
import ec.edu.espe.medicbyte.models.Reminder
import java.time.format.DateTimeFormatter

class RemindersListAdapter(private val reminders: List<Reminder>) :
        RecyclerView.Adapter<RemindersListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewReminderMedicationName: TextView = view.findViewById(R.id.tv_reminder_medication_name)
        val textViewReminderStartDate: TextView = view.findViewById(R.id.tv_reminder_start_date)
        val textViewReminderEndDate: TextView = view.findViewById(R.id.tv_reminder_end_date)
        val textViewReminderInterval: TextView = view.findViewById(R.id.tv_reminder_interval)
        val textViewReminderStatus: TextView = view.findViewById(R.id.tv_reminder_status)
        val textViewReminderNotes: TextView = view.findViewById(R.id.tv_reminder_notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminders_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.textViewReminderMedicationName.text = reminder.medication?.name ?: "<unamed>"
        holder.textViewReminderStartDate.text = reminder.startDate!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        holder.textViewReminderEndDate.text = reminder.endDate!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        holder.textViewReminderInterval.text = "${reminder.intervalValue} ${reminder.intervalUnit!!.toDisplayString()}"
        holder.textViewReminderStatus.text = reminder.status!!.toDisplayString()
        holder.textViewReminderNotes.text = reminder.notes ?: "<sin nota>"
    }

    override fun getItemCount(): Int = reminders.size
}