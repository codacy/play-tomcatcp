/*
 * Copyright (C) 2013,2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codacy.play.tomcatcp.metrics.dropwizard;

import com.codacy.play.tomcatcp.metrics.MetricsTracker;
import com.codacy.play.tomcatcp.metrics.PoolStats;
import com.codahale.metrics.*;

import java.util.concurrent.TimeUnit;

public final class CodaHaleMetricsTracker extends MetricsTracker {
    private static final String METRIC_CATEGORY = "pool";
    private static final String METRIC_NAME_WAIT = "Wait";
    private static final String METRIC_NAME_USAGE = "Usage";
    private static final String METRIC_NAME_TIMEOUT_RATE = "ConnectionTimeoutRate";
    private static final String METRIC_NAME_TOTAL_CONNECTIONS = "TotalConnections";
    private static final String METRIC_NAME_IDLE_CONNECTIONS = "IdleConnections";
    private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "ActiveConnections";
    private static final String METRIC_NAME_PENDING_CONNECTIONS = "PendingConnections";
    private final String poolName;
    private final Timer connectionObtainTimer;
    private final Histogram connectionUsage;
    private final Meter connectionTimeoutMeter;
    private final MetricRegistry registry;

    public CodaHaleMetricsTracker(final String poolName, final PoolStats poolStats, final MetricRegistry registry) {
        this.poolName = poolName;
        this.registry = registry;
        this.connectionObtainTimer = registry.timer(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_WAIT));
        this.connectionUsage = registry.histogram(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_USAGE));
        this.connectionTimeoutMeter = registry.meter(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_TIMEOUT_RATE));

        register(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_TOTAL_CONNECTIONS),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return poolStats.getTotalConnections();
                    }
                });

        register(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_IDLE_CONNECTIONS),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return poolStats.getIdleConnections();
                    }
                });

        register(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_ACTIVE_CONNECTIONS),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return poolStats.getActiveConnections();
                    }
                });

        register(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_PENDING_CONNECTIONS),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return poolStats.getPendingThreads();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        registry.remove(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_WAIT));
        registry.remove(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_USAGE));
        registry.remove(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_TIMEOUT_RATE));
        registry.remove(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_TOTAL_CONNECTIONS));
        registry.remove(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_IDLE_CONNECTIONS));
        registry.remove(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_ACTIVE_CONNECTIONS));
        registry.remove(MetricRegistry.name(poolName, METRIC_CATEGORY, METRIC_NAME_PENDING_CONNECTIONS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordConnectionAcquiredNanos(final long elapsedAcquiredNanos) {
        connectionObtainTimer.update(elapsedAcquiredNanos, TimeUnit.NANOSECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordConnectionUsageMillis(final long elapsedBorrowedMillis) {
        connectionUsage.update(elapsedBorrowedMillis);
    }

    @Override
    public void recordConnectionTimeout() {
        connectionTimeoutMeter.mark();
    }

    public Timer getConnectionAcquisitionTimer() {
        return connectionObtainTimer;
    }

    public Histogram getConnectionDurationHistogram() {
        return connectionUsage;
    }

    private <T extends Metric> void register(String name, T metric) {
        try {
            registry.register(name, metric);
        } catch (IllegalArgumentException e) {
            // Don't blow if metric already exists
        }
    }
}
