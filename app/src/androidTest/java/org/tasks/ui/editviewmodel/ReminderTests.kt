package org.tasks.ui.editviewmodel

import org.tasks.data.entity.Task
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.tasks.data.entity.Alarm
import org.tasks.data.entity.Alarm.Companion.whenOverdue
import org.tasks.data.createDueDate
import org.tasks.injection.ProductionModule
import org.tasks.makers.TaskMaker.newTask
import org.tasks.time.DateTimeUtils2.currentTimeMillis

@UninstallModules(ProductionModule::class)
@HiltAndroidTest
class ReminderTests : BaseTaskEditViewModelTest() {
    @Test
    fun whenStartReminder() = runBlocking {
        val task = newTask()
        task.defaultReminders(Task.NOTIFY_AT_START)
        setup(task)

        viewModel.setStartDate(
            createDueDate(
                Task.URGENCY_SPECIFIC_DAY_TIME,
                currentTimeMillis()
            )
        )

        save()

        assertEquals(
            listOf(Alarm(1, 0, Alarm.TYPE_REL_START).apply { id = 1 }),
            alarmDao.getAlarms(task.id)
        )
    }

    @Test
    fun whenDueReminder() = runBlocking {
        val task = newTask()
        task.defaultReminders(Task.NOTIFY_AT_DEADLINE)
        setup(task)

        viewModel.setDueDate(
            createDueDate(
                Task.URGENCY_SPECIFIC_DAY_TIME,
                currentTimeMillis()
            )
        )

        save()

        assertEquals(
            listOf(Alarm(1, 0, Alarm.TYPE_REL_END).apply { id = 1 }),
            alarmDao.getAlarms(task.id)
        )
    }

    @Test
    fun whenOverDueReminder() = runBlocking {
        val task = newTask()
        task.defaultReminders(Task.NOTIFY_AFTER_DEADLINE)
        setup(task)

        viewModel.setDueDate(
            createDueDate(
                Task.URGENCY_SPECIFIC_DAY_TIME,
                currentTimeMillis()
            )
        )

        save()

        assertEquals(
            listOf(whenOverdue(1).apply { id = 1 }),
            alarmDao.getAlarms(task.id)
        )
    }

    @Test
    fun ringFiveTimes() = runBlocking {
        val task = newTask()
        setup(task)

        viewModel.ringFiveTimes = true

        save()

        assertTrue(taskDao.fetch(task.id)!!.isNotifyModeFive)
    }

    @Test
    fun ringNonstop() = runBlocking {
        val task = newTask()
        setup(task)

        viewModel.ringNonstop = true

        save()

        assertTrue(taskDao.fetch(task.id)!!.isNotifyModeNonstop)
    }

    @Test
    fun ringFiveTimesCantRingNonstop() = runBlocking {
        val task = newTask()
        setup(task)

        viewModel.ringNonstop = true
        viewModel.ringFiveTimes = true

        save()

        assertFalse(taskDao.fetch(task.id)!!.isNotifyModeNonstop)
        assertTrue(taskDao.fetch(task.id)!!.isNotifyModeFive)
    }

    @Test
    fun ringNonStopCantRingFiveTimes() = runBlocking {
        val task = newTask()
        setup(task)

        viewModel.ringFiveTimes = true
        viewModel.ringNonstop = true

        save()

        assertFalse(taskDao.fetch(task.id)!!.isNotifyModeFive)
        assertTrue(taskDao.fetch(task.id)!!.isNotifyModeNonstop)
    }
}