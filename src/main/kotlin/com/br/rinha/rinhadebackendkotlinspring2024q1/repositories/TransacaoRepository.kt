package com.br.rinha.rinhadebackendkotlinspring2024q1.repositories

import com.br.rinha.rinhadebackendkotlinspring2024q1.config.ScyllaClusterConfig
import com.br.rinha.rinhadebackendkotlinspring2024q1.domains.Transacao
import com.datastax.driver.core.Session
import org.springframework.stereotype.Repository

@Repository
class TransacaoRepository(scyllaClusterConfig: ScyllaClusterConfig) {

    private lateinit var session: Session

    init {
        val cluster = scyllaClusterConfig.buildCluster()
        this.session = cluster.connect("rinha")
    }

    suspend fun adicionar(transacao: Transacao) {
        val ps = session.prepare(
            """
            INSERT INTO transacao (valor, tipo, descricao, cliente_id, realizada_em)
            VALUES (?,?,?,?,?)
            """
        )
        val bs = ps.bind(transacao.valor, transacao.tipo, transacao.descricao, transacao.idCliente, transacao.realizadaEm)
        session.execute(bs)
    }

    suspend fun getByIdClient(idClient: Int): List<Transacao>? {
        val ps = session.prepare(
            """
            SELECT * FROM transacao WHERE cliente_id = ? ORDER BY realizada_em desc LIMIT 10
            """
        )
        val bs = ps.bind(idClient)
        val rs = session.execute(bs)
        return rs?.map { row ->
            Transacao(row.getDouble("valor"), row.getString("tipo"), row.getString("descricao"), row.getInt("cliente_id"), row.getTimestamp("realizada_em"))
        }
    }
}
