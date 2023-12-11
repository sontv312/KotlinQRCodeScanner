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

/**
 * A utility object containing extension functions and utility methods.
 */
object Extensions {

    /**
     * Generates a random API key.
     * @return A randomly generated API key.
     */
    fun generateKey(): String {
        val random = SecureRandom()
        val seed = ByteArray(32)
        random.nextBytes(seed)
        return Base64.getEncoder().encodeToString(seed)
    }

    /**
     * Converts a URI to a Bitmap.
     * @param context The context used to open the input stream.
     * @param uri The URI of the image.
     * @return The Bitmap representation of the image, or null if an error occurs.
     */
    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri!!)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Scans a QR code from a Bitmap.
     * @param bitmap The Bitmap containing the QR code.
     * @return The decoded text from the QR code, or null if the QR code is not found.
     */
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

    /**
     * Initializes ScanOptions for scanning QR codes.
     * @return An instance of [ScanOptions] configured for scanning QR codes.
     */
    fun initScan(): ScanOptions {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setBarcodeImageEnabled(true)
        return options
    }

}