package es.rafapuig.pmdm.compose.proyecto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Convierte un URI de imagen a una cadena Base64
 * @param context Contexto de la aplicación
 * @param uri URI de la imagen
 * @param maxSize Tamaño máximo de la imagen en píxeles (default: 800)
 * @param quality Calidad de compresión JPEG (0-100, default: 85)
 * @return String Base64 o null si hay error
 */
fun uriToBase64(
    context: Context,
    uri: Uri,
    maxSize: Int = 800,
    quality: Int = 85
): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // Redimensionar la imagen manteniendo la proporción
        val scaledBitmap = if (originalBitmap.width > maxSize || originalBitmap.height > maxSize) {
            val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
            val newWidth: Int
            val newHeight: Int

            if (originalBitmap.width > originalBitmap.height) {
                newWidth = maxSize
                newHeight = (maxSize / ratio).toInt()
            } else {
                newHeight = maxSize
                newWidth = (maxSize * ratio).toInt()
            }

            Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        } else {
            originalBitmap
        }

        // Comprimir y convertir a Base64
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val bytes = outputStream.toByteArray()

        // Limpiar recursos
        if (scaledBitmap != originalBitmap) {
            scaledBitmap.recycle()
        }
        originalBitmap.recycle()

        Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
