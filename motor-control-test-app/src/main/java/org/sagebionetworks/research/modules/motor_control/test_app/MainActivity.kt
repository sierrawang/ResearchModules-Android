/*
 * BSD 3-Clause License
 *
 * Copyright 2019  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.modules.motor_control.test_app

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.tapping
import kotlinx.android.synthetic.main.activity_main.tremor
import kotlinx.android.synthetic.main.activity_main.walk_30_seconds
import kotlinx.android.synthetic.main.activity_main.walk_and_balance
import org.sagebionetworks.research.domain.repository.TaskRepository
import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskActivity
import org.sagebionetworks.research.modules.motor_control.MotorControlTask.TAPPING
import org.sagebionetworks.research.modules.motor_control.MotorControlTask.TREMOR
import org.sagebionetworks.research.modules.motor_control.MotorControlTask.WALK_30_SECONDS
import org.sagebionetworks.research.modules.motor_control.MotorControlTask.WALK_AND_BALANCE
import org.sagebionetworks.research.modules.psorcast.test_app.R.layout
import org.sagebionetworks.research.presentation.model.TaskView
import java.util.UUID
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        tapping.setOnClickListener {
            launchTask(TAPPING, UUID.randomUUID())
        }
        tremor.setOnClickListener {
            launchTask(TREMOR, UUID.randomUUID())
        }
        walk_and_balance.setOnClickListener {
            launchTask(WALK_AND_BALANCE, UUID.randomUUID())
        }
        walk_30_seconds.setOnClickListener {
            launchTask(WALK_30_SECONDS, UUID.randomUUID())
        }
    }

    private fun launchTask(taskIdentifier: String,
            taskRunUUID: UUID?) {
        val taskInfoView = taskRepository.getTaskInfo(taskIdentifier).blockingGet()

        //TODO: mapper
        val taskView = TaskView.builder().setIdentifier(taskInfoView.identifier).build()

        val intent = PerformTaskActivity.createIntent(applicationContext, taskView, taskRunUUID)
        this.startActivity(intent)
    }
}
