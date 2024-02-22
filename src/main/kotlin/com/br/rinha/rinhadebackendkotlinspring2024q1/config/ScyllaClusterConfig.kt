package com.br.rinha.rinhadebackendkotlinspring2024q1.config

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PoolingOptions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("scylla")
class ScyllaClusterConfig {

    lateinit var contractPoints: String
    lateinit var clusterName: String
    private val poolingOptions: PoolingOptions = PoolingOptions()

    fun buildCluster(): Cluster {
        return Cluster.builder()
            .withoutJMXReporting()
            .withClusterName(clusterName)
            .withPoolingOptions(poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 2, 25))
            .addContactPoint(contractPoints)
            .build()
    }
}
