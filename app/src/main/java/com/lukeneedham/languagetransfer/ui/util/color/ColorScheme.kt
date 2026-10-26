package com.lukeneedham.languagetransfer.ui.util.color

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ColorScheme(val colors: List<Long>) : Parcelable