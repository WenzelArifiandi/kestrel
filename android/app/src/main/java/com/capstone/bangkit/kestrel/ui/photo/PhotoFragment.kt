package com.capstone.bangkit.kestrel.ui.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.bangkit.kestrel.R
import com.capstone.bangkit.kestrel.databinding.FragmentPhotoBinding
import com.capstone.bangkit.kestrel.helper.DateHelper
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class PhotoFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.to_bottom_anim
        )
    }

    private var closed = false

    companion object {
        private const val IMAGE_MEAN = 0.0f
        private const val IMAGE_STD = 1.0f
        private const val PROBABILITY_MEAN = 0.0f
        private const val PROBABILITY_STD = 255.0f
    }

    private var tflite: Interpreter? = null
    private var inputImageBuffer: TensorImage? = null
    private var imageSizeX = 0
    private var imageSizeY = 0
    private var outputProbabilityBuffer: TensorBuffer? = null
    private var probabilityProcessor: TensorProcessor? = null
    private var bitmap: Bitmap? = null
    private var labels: List<String>? = null
    private var imageUri: Uri? = null

    private val REQUEST_ACTION_GET_CONTENT = 100
    private val REQUEST_IMAGE_CAPTURE = 101

    private val preProcessNormalizeOp: TensorOperator
        get() = NormalizeOp(IMAGE_MEAN, IMAGE_STD)
    private val postProcessNormalizeOp: TensorOperator
        get() = NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            onAddButtonClick()
        }

        binding.btnCapture.setOnClickListener {
            openCamera()
        }
        binding.btnGallery.setOnClickListener {
            openActionGetContent()
        }

        try {
            if (bitmap != null) {
                tflite = Interpreter(loadModel(requireActivity()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let { displayMessage(it) }
        }

        binding.btnProcess.setOnClickListener {
            processImage()
            showResult()
            saveData()
        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            val packageManager = activity?.packageManager
            if (packageManager != null) {
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun processImage() {
        try {
            val imageTensorIndex = 0
            val imageShape: IntArray =
                tflite?.getInputTensor(imageTensorIndex)!!.shape()
            imageSizeY = imageShape[1]
            imageSizeX = imageShape[2]
            val imageDataType: DataType = tflite?.getInputTensor(imageTensorIndex)!!.dataType()
            val probabilityTensorIndex = 0
            val probabilityShape: IntArray =
                tflite?.getOutputTensor(probabilityTensorIndex)!!.shape()
            val probabilityDataType: DataType =
                tflite?.getOutputTensor(probabilityTensorIndex)!!.dataType()
            inputImageBuffer = TensorImage(imageDataType)
            outputProbabilityBuffer =
                TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
            probabilityProcessor = TensorProcessor.Builder().add(postProcessNormalizeOp).build()
            inputImageBuffer = loadImage(bitmap)
            tflite?.run(inputImageBuffer!!.buffer, outputProbabilityBuffer!!.buffer.rewind())
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let { displayMessage(it) }
        }
    }

    private fun displayMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun openActionGetContent() {
        Intent().also { takeContentIntent ->
            takeContentIntent.type = "image/*"
            takeContentIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(takeContentIntent, REQUEST_ACTION_GET_CONTENT)
        }
    }

    private fun loadImage(bitmap: Bitmap?): TensorImage {
        inputImageBuffer?.load(bitmap)
        val cropSize = bitmap!!.width.coerceAtMost(bitmap.height)
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(preProcessNormalizeOp)
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    private fun loadModel(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd("fix_model_metadata.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun showResult() {
        try {
            labels = context?.let { FileUtil.loadLabels(it, "labels2.txt") }
            val labeledProbability: Map<String, Float> =
                TensorLabel(labels!!, probabilityProcessor!!.process(outputProbabilityBuffer))
                    .mapWithFloatValue
            val maxValueInMap = Collections.max(labeledProbability.values)
            for ((key, value) in labeledProbability) {
                if (value == maxValueInMap) {
                    binding.tvResult.text = key
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let { displayMessage(it) }
        }
    }

    private fun onAddButtonClick() {
        setVisibility(closed)
        setAnimation(closed)
        closed = !closed
    }

    private fun setAnimation(closed: Boolean) {
        if (!closed) {
            binding.btnCapture.startAnimation(fromBottom)
            binding.btnGallery.startAnimation(fromBottom)
            binding.btnAdd.startAnimation(rotateOpen)
        } else {
            binding.btnCapture.startAnimation(toBottom)
            binding.btnGallery.startAnimation(toBottom)
            binding.btnAdd.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(closed: Boolean) {
        if (!closed) {
            binding.btnCapture.visibility = View.VISIBLE
            binding.btnGallery.visibility = View.VISIBLE
        } else {
            binding.btnCapture.visibility = View.INVISIBLE
            binding.btnGallery.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_ACTION_GET_CONTENT) {
                imageUri = data?.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                    loadGlideImage(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    e.message?.let { displayMessage(it) }
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                try {
                    bitmap = data?.extras?.get("data") as Bitmap
                    loadGlideImage(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    e.message?.let { displayMessage(it) }
                }
            }
        }
    }

    private fun loadGlideImage(bitmap: Bitmap?) {
        Glide.with(requireActivity())
            .asBitmap()
            .load(bitmap)
            .apply(RequestOptions())
            .into(binding.photo)
    }

    private fun saveData() {
        val histories: MutableMap<String, Any> = HashMap()

        val alphabet = binding.tvResult.text.toString().toUpperCase(Locale.ROOT)
        val date = DateHelper.getCurrentDate()

        histories["alphabet"] = alphabet
        histories["date"] = date

        if (alphabet.isEmpty()) {
            displayMessage("Result is empty")
        } else {
            db.collection("histories")
                .add(histories)
                .addOnSuccessListener {
                    displayMessage("Data added successfully")
                }
                .addOnFailureListener {
                    displayMessage("Data failed to add")
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}