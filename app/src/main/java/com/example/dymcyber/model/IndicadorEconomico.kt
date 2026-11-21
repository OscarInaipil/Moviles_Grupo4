package com.example.dymcyber.model

import com.google.gson.annotations.SerializedName

data class IndicadorItem(
    val codigo: String = "",
    val nombre: String = "",
    @SerializedName("unidad_medida")
    val unidadMedida: String = "",
    val fecha: String = "",
    val valor: Double = 0.0
)

data class MindicadorRespuesta(
    val version: String = "",
    val autor: String = "",
    val fecha: String = "",
    val dolar: IndicadorItem? = null,
    @SerializedName("libra_cobre")
    val libraCobre: IndicadorItem? = null,
    val euro: IndicadorItem? = null,
    val uf: IndicadorItem? = null
)
