package ec.edu.espe.medicbyte.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(private val context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val medicationName = inputData.getString("medicationName")
        val medicationDose = inputData.getString("medicationDose")
        val medicationUnit = inputData.getString("medicationUnit")
        val reminderId = inputData.getInt("reminderId", 0)

        NotificationUtils.showNotification(
            context,
            "MedicByte",
            "Es hora de tomar tu medicina $medicationName, $medicationDose $medicationUnit", reminderId)

        return Result.success()
    }
}
