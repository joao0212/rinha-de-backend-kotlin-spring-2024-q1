package com.br.rinha.rinhadebackendkotlinspring2024q1.service

import com.br.rinha.rinhadebackendkotlinspring2024q1.domains.*
import com.br.rinha.rinhadebackendkotlinspring2024q1.exception.ClienteNaoEncontradoException
import com.br.rinha.rinhadebackendkotlinspring2024q1.exception.TransacaoNaoPermitidaException
import com.br.rinha.rinhadebackendkotlinspring2024q1.repositories.ClienteRepository
import com.br.rinha.rinhadebackendkotlinspring2024q1.repositories.PessimisticLockRepository
import com.br.rinha.rinhadebackendkotlinspring2024q1.repositories.TransacaoRepository
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Service
class TransacaoService(
    private val transacaoRepository: TransacaoRepository,
    private val clienteRepository: ClienteRepository,
    private val pessimisticLockRepository: PessimisticLockRepository
) {

    companion object {
        private const val WRITE = "write"
    }

    suspend fun validarTransacao(idCliente: Int, transacao: Transacao) = realizar(idCliente, transacao)

    suspend fun realizar(idCliente: Int, transacao: Transacao): TransacaoResponse {
        val cliente = buscarCliente(idCliente)
        if (pessimisticLockRepository.acquireWriteLock(idCliente)) {
            coroutineScope {
                val saldo = async { validarTipo(cliente, transacao) }
                cliente.saldoInicial = requireNotNull(saldo.await().valor)
                launch { clienteRepository.atualizar(idCliente, cliente) }
                launch { transacaoRepository.adicionar(transacao.copy(idCliente = idCliente, realizadaEm = Date.from(Instant.now()))) }
            }
            pessimisticLockRepository.releaseLock(idCliente, WRITE)
        }
        return TransacaoResponse(cliente.limite, cliente.saldoInicial)
    }

    private suspend fun validarTipo(cliente: Cliente, transacao: Transacao): Saldo {
        return when (transacao.tipo) {
            "c" -> eCredito(cliente, transacao)
            "d" -> eDebito(cliente, transacao)
            else -> throw TransacaoNaoPermitidaException()
        }
    }

    private suspend fun eCredito(cliente: Cliente, transacao: Transacao) =
        Saldo().apply { this.valor = cliente.saldoInicial + transacao.valor.toInt() }

    private suspend fun eDebito(cliente: Cliente, transacao: Transacao): Saldo {
        return if ((cliente.saldoInicial + cliente.limite) > transacao.valor.toInt()) {
            Saldo().apply {
                this.valor = cliente.saldoInicial - transacao.valor.toInt()
            }
        } else {
            throw TransacaoNaoPermitidaException()
        }
    }

    suspend fun extrato(idCliente: Int): ExtratoResponse? {
        val cliente = buscarCliente(idCliente)
        return transacaoRepository.getByIdClient(idCliente)?.let { transacoes ->
            ExtratoResponse().apply {
                this.saldo = SaldoResponse(cliente.saldoInicial, LocalDateTime.now(), cliente.limite)
                this.ultimasTransacoes = transacoes.map {
                    TransacaoDTO(
                        it.valor.toInt(),
                        it.tipo,
                        it.descricao,
                        it.realizadaEm,
                    )
                }
            }
        }
    }

    private suspend fun buscarCliente(idCliente: Int) = clienteRepository.findById(idCliente)
        ?: throw ClienteNaoEncontradoException()
}
