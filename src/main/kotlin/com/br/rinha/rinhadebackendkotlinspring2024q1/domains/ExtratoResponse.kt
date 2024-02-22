package com.br.rinha.rinhadebackendkotlinspring2024q1.domains

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

data class ExtratoResponse(
    var saldo: SaldoResponse? = null,
    var ultimasTransacoes: List<TransacaoDTO> = emptyList(),
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val mensagemErro: String? = null
)

data class SaldoResponse(
    val total: Int? = null,
    val dataExtrato: LocalDateTime = LocalDateTime.now(),
    val limite: Int? = null
)
