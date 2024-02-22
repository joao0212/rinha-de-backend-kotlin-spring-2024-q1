package com.br.rinha.rinhadebackendkotlinspring2024q1.repositories

import com.br.rinha.rinhadebackendkotlinspring2024q1.config.ScyllaClusterConfig
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import org.springframework.stereotype.Repository

@Repository
class PessimisticLockRepository(scyllaClusterConfig: ScyllaClusterConfig) {

    private lateinit var session: Session

    init {
        val cluster = scyllaClusterConfig.buildCluster()
        this.session = cluster.connect("rinha")
    }

    suspend fun acquireWriteLock(clientId: Int): Boolean {
        val ps = session.prepare("SELECT * FROM locks WHERE cliente_id = ? AND lock_type = 'write'")
        val bs = ps.bind(clientId)
        val rs = session.execute(bs)
        val row: Row? = rs.one()
        return if (row == null) {
            val insert = session.prepare("INSERT INTO locks (cliente_id, lock_type) VALUES (?, 'write')")
            val result = insert.bind(clientId)
            session.execute(result)
            true
        } else {
            false
        }
    }

    suspend fun releaseLock(clientId: Int, lockType: String) {
        val ps = session.prepare("DELETE FROM locks WHERE cliente_id = ? AND lock_type = ?")
        val bs = ps.bind(clientId, lockType)
        session.execute(bs)
    }
}