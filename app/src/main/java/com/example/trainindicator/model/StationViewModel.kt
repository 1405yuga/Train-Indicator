package com.example.trainindicator.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trainindicator.R
import com.example.trainindicator.constants.ProjectConstants
import com.google.firebase.firestore.DocumentSnapshot

class StationViewModel : ViewModel() {

    private var _stationsList = MutableLiveData(listOf<DocumentSnapshot>())
    val stationsList: LiveData<List<DocumentSnapshot>> = _stationsList

    val updateStationList: (List<DocumentSnapshot>) -> (Unit) = {
        _stationsList.value = it
    }

    private fun addText(bitmap: Bitmap, stationCode: String): Bitmap {
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f // Set your desired text size
            typeface = Typeface.DEFAULT_BOLD
        }

        // Calculate the position to center the text
        val textX = (resultBitmap.width - textPaint.measureText(stationCode)) / 2
        val textY = resultBitmap.height / 2.5

        canvas.drawText(stationCode, textX, textY.toFloat(), textPaint)

        return resultBitmap
    }

    fun createMarkerIcon(stationCode: String?, status: String?, context: Context): Bitmap? {
        val height = 150
        val width = 150
        var image = R.drawable.red_marker
        if (status == ProjectConstants.FAST) image = R.drawable.blue_marker
        val bitmap = BitmapFactory.decodeResource(context.resources, image)
        val bitmapWithText =
            addText(Bitmap.createScaledBitmap(bitmap, width, height, false), stationCode.toString())
        return bitmapWithText
    }
}