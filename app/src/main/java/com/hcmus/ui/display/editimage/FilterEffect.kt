package com.hcmus.ui.display.editimage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ja.burhanrashid52.photoeditor.PhotoEditor
import androidx.compose.ui.graphics.toArgb
import ja.burhanrashid52.photoeditor.CustomEffect
import ja.burhanrashid52.photoeditor.PhotoFilter

// Define FilterEffect data class
data class FilterEffect(
    val name: String,
    val effectType: String,  // either PhotoFilter or custom effect type
    val isCustom: Boolean = false,  // flag to distinguish between built-in and custom filters
    val parameters: Map<String, Any>? = null  // parameters for custom effects
)

// Define available filters with their effects and parameters
val filterEffects = listOf(
    FilterEffect("Brightness", "android.media.effect.effects.BrightnessEffect", isCustom = false),
    FilterEffect("Contrast", "android.media.effect.effects.ContrastEffect", isCustom = false),
    FilterEffect("Grayscale", "android.media.effect.effects.GrayscaleEffect", isCustom = false),
    FilterEffect("Sepia", "android.media.effect.effects.SepiaEffect", isCustom = false),
    FilterEffect("Vignette", "android.media.effect.effects.VignetteEffect", isCustom = false),
    FilterEffect("Negative", "android.media.effect.effects.NegativeEffect", isCustom = false),
    FilterEffect("Sharpen", "android.media.effect.effects.SharpenEffect", isCustom = false),
    FilterEffect("Duotone", "android.media.effect.effects.DuotoneEffect", isCustom = true, parameters = mapOf("first_color" to Color.Red.toArgb(), "second_color" to Color.Blue.toArgb())),
    FilterEffect("Temperature", "android.media.effect.effects.ColorTemperatureEffect", isCustom = false)
)

fun PhotoEditor?.applyEffect(effectType: String, params: Map<String, Any>? = null) {
    this?.let {
        if (effectType.isEmpty()) return  // Ensure that effectType is not empty

        if (params == null || params.isEmpty()) {
            when (effectType) {
                "android.media.effect.effects.BrightnessEffect" -> it.setFilterEffect(PhotoFilter.BRIGHTNESS)
                "android.media.effect.effects.ContrastEffect" -> it.setFilterEffect(PhotoFilter.CONTRAST)
                "android.media.effect.effects.GrayscaleEffect" -> it.setFilterEffect(PhotoFilter.GRAY_SCALE)
                "android.media.effect.effects.SepiaEffect" -> it.setFilterEffect(PhotoFilter.SEPIA)
                "android.media.effect.effects.VignetteEffect" -> it.setFilterEffect(PhotoFilter.VIGNETTE)
                "android.media.effect.effects.NegativeEffect" -> it.setFilterEffect(PhotoFilter.NEGATIVE)
                "android.media.effect.effects.SharpenEffect" -> it.setFilterEffect(PhotoFilter.SHARPEN)
                "android.media.effect.effects.DuotoneEffect" -> it.setFilterEffect(PhotoFilter.DUE_TONE)
                "android.media.effect.effects.ColorTemperatureEffect" -> it.setFilterEffect(PhotoFilter.TEMPERATURE)
            }
        } else {
            // Handle Custom Filters
            val customEffect = CustomEffect.Builder(effectType) // Using the effect type passed
            params.forEach { (key, value) ->
                customEffect.setParameter(key, value)
            }
            it.setFilterEffect(customEffect.build()) // Apply custom effect
        }
    }
}

@Composable
fun FilterMenu(
    photoEditor: PhotoEditor?,
    onClose: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filterEffects, key = { it.name }) { effect ->
            Button(
                onClick = {
                    // Apply the selected effect
                    if (effect.isCustom) {
                        photoEditor?.applyEffect(effect.effectType, effect.parameters)
                    } else {
                        // For built-in effects
                        photoEditor?.applyEffect(effect.effectType)
                    }
                },
                modifier = Modifier
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray, // Background color of the button
                        contentColor = Color.White  // Text color
                )
            ) {
                Text(text = effect.name)
            }
        }
    }
}
