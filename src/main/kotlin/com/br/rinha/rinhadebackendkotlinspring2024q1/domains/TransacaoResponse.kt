package com.br.rinha.rinhadebackendkotlinspring2024q1.domains

import com.fasterxml.jackson.annotation.JsonInclude

data class TransacaoResponse(
    val limite: Int? = null,
    val saldo: Int? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val mensagemErro: String? = null
)