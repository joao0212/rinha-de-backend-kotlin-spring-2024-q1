package com.br.rinha.rinhadebackendkotlinspring2024q1.repositories

import com.br.rinha.rinhadebackendkotlinspring2024q1.config.ScyllaClusterConfig
import com.br.rinha.rinhadebackendkotlinspring2024q1.domains.Cliente
import com.datastax.driver.core.Session
import org.springframework.stereotype.Repository

@Repository
class ClienteRepository(scyllaClusterConfig: ScyllaClusterConfig) {

    private lateinit var session: Session

    init {
        val cluster = scyllaClusterConfig.buildCluster()
        this.session = cluster.connect("rinha")
    }

    suspend fun findById(idCliente: Int): Cliente? {
        val ps = session.prepare("SELECT * FROM cliente WHERE id = ?")
        val bs = ps.bind(idCliente)
        val rs = session.execute(bs)
        val row = rs.one()
        return row?.let {
            Cliente(row.getInt("id"), row.getInt("limite"), row.getInt("saldo_inicial"))
        }
    }

    suspend fun atualizar(clienteId: Int, cliente: Cliente) {
        val ps = session.prepare(
            """ 
            UPDATE cliente SET saldo_inicial = ? WHERE id = ?
            """.trimIndent(),
        )
        val bs = ps.bind(cliente.saldoInicial, clienteId)
        session.execute(bs)
    }
}
