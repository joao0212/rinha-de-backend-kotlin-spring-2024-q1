package com.br.rinha.rinhadebackendkotlinspring2024q1.domains

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.*
import java.util.Date

data class Transacao(
    @get:NotNull
    @get:PositiveOrZero
    @get:Digits(integer = 1000000000, fraction = 0)
    val valor: Double,

    @get:NotNull
    @get:NotBlank
    @get:NotEmpty
    val tipo: String,

    @get:Size(min = 1, max = 10)
    @get:NotNull
    @get:NotBlank
    @get:NotEmpty
    val descricao: String,
    @JsonIgnore
    val idCliente: Int? = null,
    val realizadaEm: Date? = null
)
