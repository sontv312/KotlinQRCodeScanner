package com.example.qrcodescanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.ScanOptions
import java.io.FileNotFoundException
import java.security.SecureRandom
import java.util.Base64
import java.util.EnumMap

object Extensions {

    //khỏi tạo apiKey
    fun generateKey(): String {
        val random = SecureRandom()
        val seed = ByteArray(32)
        random.nextBytes(seed)
        return Base64.getEncoder().encodeToString(seed)
    }

    //chuyển uri ảnh thành bitmap
    // ô cứ ghi là convert uri sang bitmap là đc ô thầy nó ko hỏi thêm đâu do đây là câu lệnh mạc đinh r ok
    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri!!)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    //code này của zxing // chỗ này là nó đang convert cái bitmap sang binarybitmap sau do la thu vien zxing no se doc cai binarybitmap de tra ve 1 result
    fun scanQRCode(bitmap: Bitmap): String? {
        val multiFormatReader = MultiFormatReader()

        val hints: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)
        hints[DecodeHintType.TRY_HARDER] = true

        val source = RGBLuminanceSource(
            bitmap.width,
            bitmap.height,
            IntArray(bitmap.width * bitmap.height).apply {
                bitmap.getPixels(this, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            })
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = multiFormatReader.decode(binaryBitmap, hints) //tra ve kq
            return result.text
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    //khởi tạo ScanOptions
    fun initScan(): ScanOptions {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setBarcodeImageEnabled(true)
        return options
    }

}