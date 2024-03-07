package ec.edu.espe.medicbyte

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.navigation.NavigationView
import ec.edu.espe.medicbyte.databinding.ActivityMainBinding
import ec.edu.espe.medicbyte.models.IntervalUnit
import ec.edu.espe.medicbyte.services.ReminderService
import ec.edu.espe.medicbyte.utils.NotificationUtils
import ec.edu.espe.medicbyte.utils.NotificationWorker
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        binding.appBarMain.fab.setOnClickListener { _ ->
            val id = navView.checkedItem?.itemId

            if (id == R.id.nav_reminders) {
                navController.navigate(R.id.nav_reminder_form)
            } else if (id == R.id.nav_medications) {
                Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    setupReminders()
                }
            }, 0, 1000 * 5)
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_reminders, R.id.nav_medications
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private val addedReminders: MutableList<Int> = mutableListOf()

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }

    @SuppressLint("RestrictedApi")
    private fun setupReminders() {
        val context = this

        lifecycleScope.launch {
            Log.d("RemindersFragment", "retrieve reminders...")

            try {
                val reminders = ReminderService.getInstance(context).getAll()

                Log.d("RemindersMain", "reminders: $reminders")

                for (reminder in reminders) {
                    if (addedReminders.contains(reminder.id!!)) {
                        continue
                    }

                    val delay = 1000 * (when (reminder.intervalUnit) {
                        IntervalUnit.MINUTES -> reminder.intervalValue!! * 60
                        IntervalUnit.DAYS -> reminder.intervalValue!! * 60 * 60 * 24
                        IntervalUnit.HOURS -> reminder.intervalValue!! * 60 * 60
                        else -> reminder.intervalValue!! * 60
                    }).toLong()

                    addedReminders.add(reminder.id!!)

                    Timer().scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            NotificationUtils.showNotification(
                                context,
                                "MedicByte",
                                "Es hora de tomar tu medicina ${reminder.medication!!.name}, ${reminder.medication!!.dosage} ${reminder.medication!!.unit}",
                                reminder.id!!
                            )
                        }
                    }, delay, delay)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}