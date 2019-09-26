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

package org.sagebionetworks.research.modules.psorcast.step.joint_photography

import android.view.TextureView
import androidx.annotation.NonNull
import android.view.View
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding
import org.sagebionetworks.research.modules.psorcast.R
import org.sagebionetworks.research.presentation.model.interfaces.StepView

// STUFF ADDED FOR CAMERAX
import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CaptureMode.MIN_LATENCY
import androidx.camera.core.ImageCapture.ImageCaptureError
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.srpm_show_joint_photography_step_fragment.camera_ui_container
import kotlinx.android.synthetic.main.srpm_show_joint_photography_step_fragment.view.capture_button
import kotlinx.android.synthetic.main.srpm_show_joint_photography_step_fragment.view_finder
import org.sagebionetworks.research.modules.psorcast.org.sagebionetworks.research.modules.psorcast.step.joint_photography.ANIMATION_FAST_MILLIS
import org.sagebionetworks.research.modules.psorcast.org.sagebionetworks.research.modules.psorcast.step.joint_photography.ANIMATION_SLOW_MILLIS
import org.sagebionetworks.research.modules.psorcast.org.sagebionetworks.research.modules.psorcast.step.joint_photography.AutoFitPreviewBuilder
import org.sagebionetworks.research.presentation.model.action.ActionType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

// This is an arbitrary number we are using to keep tab of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

private const val TAG = "JointPhotography"
private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"

fun createFile(baseFolder: File, format: String, extension: String): File {
    return File(baseFolder,
            SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
}


class ShowJointPhotographyStepFragment :
        ShowUIStepFragmentBase<JointPhotographyStepView, ShowJointPhotographyStepViewModel, UIStepViewBinding<JointPhotographyStepView>>() {

    companion object {
        @JvmStatic
        fun newInstance(@NonNull stepView: StepView): ShowJointPhotographyStepFragment {
            val fragment = ShowJointPhotographyStepFragment()
            val arguments = ShowStepFragmentBase.createArguments(stepView)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.srpm_show_joint_photography_step_fragment
    }

    override fun instantiateAndBindBinding(view: View?): UIStepViewBinding<JointPhotographyStepView> {
        return UIStepViewBinding(view)
    }

    // STUFF ADDED FOR CAMERAX

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = view as ConstraintLayout
        viewFinder = view_finder

        outputDirectory = context!!.filesDir

        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private lateinit var viewFinder: TextureView
    private lateinit var container: ConstraintLayout
    private lateinit var outputDirectory: File

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null

    private fun startCamera() {
        bindCameraUseCases()
        updateCameraUi()
    }

    private val imageSavedListener = object : ImageCapture.OnImageSavedListener {
        override fun onError(imageCaptureError: ImageCaptureError, message: String, cause: Throwable?) {
            Log.e(TAG, "Photo capture failed: $message")
            cause?.printStackTrace()
        }

        override fun onImageSaved(photoFile: File) {
            Log.d(TAG, "Photo capture succeeded: ${photoFile.absolutePath}")

            // Move to next step - display photo
            // Save the photo to the result
            showStepViewModel.jpResultBuilder.setPhotoAbsolutePath(photoFile.absolutePath)
            showStepViewModel.handleAction(ActionType.FORWARD)
        }
    }

    private fun updateCameraUi() {
        camera_ui_container.capture_button.setOnClickListener {
            imageCapture?.let {imageCapture ->
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

                imageCapture.takePicture(photoFile, imageSavedListener)

                // Flash the screen to demonstrate picture taking
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    container.postDelayed({
//                        container.foreground = ColorDrawable(Color.WHITE)
//                        container.postDelayed(
//                                { container.foreground = null }, ANIMATION_FAST_MILLIS)
//                    }, ANIMATION_SLOW_MILLIS)
//                }
            }
        }
    }

    @TargetApi(21)
    private fun bindCameraUseCases() {
        // Make sure that there are no other use cases bound to CameraX
        CameraX.unbindAll()

//        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
//        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = Rational(1,1)
        val viewFinderConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        preview = AutoFitPreviewBuilder.build(viewFinderConfig, viewFinder)

        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setCaptureMode(MIN_LATENCY)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        imageCapture = ImageCapture(imageCaptureConfig)

        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(context,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
}