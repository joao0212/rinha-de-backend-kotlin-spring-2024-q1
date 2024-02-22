package com.br.rinha.rinhadebackendkotlinspring2024q1.controllers

import com.br.rinha.rinhadebackendkotlinspring2024q1.domains.ExtratoResponse
import com.br.rinha.rinhadebackendkotlinspring2024q1.domains.Transacao
import com.br.rinha.rinhadebackendkotlinspring2024q1.domains.TransacaoResponse
import com.br.rinha.rinhadebackendkotlinspring2024q1.exception.ClienteNaoEncontradoException
import com.br.rinha.rinhadebackendkotlinspring2024q1.exception.TransacaoNaoPermitidaException
import com.br.rinha.rinhadebackendkotlinspring2024q1.service.TransacaoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class ClienteController(
    private val transacaoService: TransacaoService
) {

    @PostMapping("/{id}/transacoes")
    suspend fun adicionarTransacao(
        @PathVariable("id") idCliente: Int,
        @Valid @RequestBody
        transacao: Transacao,
    ): ResponseEntity<TransacaoResponse> {
        val transacaoResponse: TransacaoResponse
        try {
            transacaoResponse = transacaoService.validarTransacao(idCliente, transacao)
        } catch (e: TransacaoNaoPermitidaException) {
            return ResponseEntity(TransacaoResponse(mensagemErro = "Transação não permitida"), HttpStatus.valueOf(422))
        } catch (e: ClienteNaoEncontradoException) {
            return ResponseEntity(TransacaoResponse(mensagemErro = "Cliente não encontrado"), HttpStatus.NOT_FOUND)
        }
        return transacaoResponse.let { ResponseEntity(TransacaoResponse(it.limite, it.saldo), HttpStatus.OK) }
    }

    @GetMapping("/{id}/extrato")
    suspend fun buscarExtrato(
        @PathVariable("id") idCliente: Int,
    ): ResponseEntity<ExtratoResponse> {
        val extratoResponse: ExtratoResponse?
        try {
            extratoResponse = transacaoService.extrato(idCliente)
        } catch (e: ClienteNaoEncontradoException) {
            return ResponseEntity(ExtratoResponse(mensagemErro = "Cliente não encontrado"), HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(extratoResponse, HttpStatus.OK)
    }
}
