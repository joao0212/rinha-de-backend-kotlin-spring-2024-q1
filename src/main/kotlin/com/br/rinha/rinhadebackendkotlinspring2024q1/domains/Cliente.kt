package com.br.rinha.rinhadebackendkotlinspring2024q1.domains

data class Cliente(
    val id: Int,
    val limite: Int,
    var saldoInicial: Int
)
