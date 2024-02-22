package com.br.rinha.rinhadebackendkotlinspring2024q1.domains

import java.util.*

data class TransacaoDTO(
    val valor: Int,
    val tipo: String,
    val descricao: String,
    val realizadaEm: Date? = null,
)
