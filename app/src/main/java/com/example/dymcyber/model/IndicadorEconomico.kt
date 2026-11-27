package com.example.dymcyber.model

import com.google.gson.annotations.SerializedName

data class IndicadorItem(
    val codigo: String = "",
    val valor: Double = 0.0
)

data class MindicadorRespuesta(
    val dolar: IndicadorItem? = null,
    @SerializedName("libra_cobre")
    val libraCobre: IndicadorItem? = null,
    val uf: IndicadorItem? = null
)
