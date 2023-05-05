package dk.itu.moapd.scootersharing.jonli.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.* // ktlint-disable no-wildcard-imports
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentCameraBinding
import dk.itu.moapd.scootersharing.jonli.utils.Utils.fixRotation
import dk.itu.moapd.scootersharing.jonli.viewmodels.CameraViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.CameraViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var viewModel: CameraViewModel

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var imageUri: Uri? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private var mPermissionGranted = false

    private val args: CameraFragmentArgs by navArgs()

    companion object {
        private val TAG = CameraFragment::class.qualifiedName
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewModelFactory = CameraViewModelFactory(args.scooterId)
        viewModel = ViewModelProvider(this, viewModelFactory)[CameraViewModel::class.java]

        setupListeners()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS,
            )
        }
        startCamera()

        binding.cameraSwitchButton.let {
            it.isEnabled = false
            it.setOnClickListener {
                cameraSelector = if (CameraSelector.DEFAULT_FRONT_CAMERA == cameraSelector) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }

                startCamera()
            }
        }
        outputDirectory = getOutputDirectory(args.scooterId)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionGranted = false
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_PERMISSIONS,
                )
            } else {
                mPermissionGranted = true
            }
        } else {
            mPermissionGranted = true
        }

        return view
    }

    private fun setupListeners() {
        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            mPermissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (mPermissionGranted) {
                startCamera()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        Log.e("Debug", "Starting camera...")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                )

                updateCameraSwitchButton(cameraProvider)
            } catch (ex: Exception) {
                Log.e(TAG, "Use case binding failed", ex)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            "profilePicture.jpg",
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imageUri = Uri.fromFile(photoFile)
                    imageUri?.let { imageUri ->
                        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                        var imageBitmap = BitmapFactory.decodeStream(inputStream)

                        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            imageBitmap = imageBitmap.fixRotation(90f)
                        } else if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                            imageBitmap = imageBitmap.fixRotation(270f)
                        }

                        val compressedBitmap = Bitmap.createScaledBitmap(imageBitmap, 480, 640, true)

                        val outputStream = ByteArrayOutputStream()
                        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        val imageBytes = outputStream.toByteArray()

                        viewModel.updateScooterPicture(imageBytes)
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            },
        )
    }

    private fun updateCameraSwitchButton(provider: ProcessCameraProvider) {
        try {
            binding.cameraSwitchButton.isEnabled =
                hasBackCamera(provider) && hasFrontCamera(provider)
        } catch (exception: CameraInfoUnavailableException) {
            binding.cameraSwitchButton.isEnabled = false
        }
    }

    private fun hasBackCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)

    private fun hasFrontCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)

    private fun getOutputDirectory(id: String): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name) + File.separator + id).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            requireActivity().filesDir
        }
    }
}
