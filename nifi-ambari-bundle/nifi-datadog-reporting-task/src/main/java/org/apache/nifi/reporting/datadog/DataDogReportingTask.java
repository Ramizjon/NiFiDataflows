package org.apache.nifi.reporting.datadog;


import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.yammer.metrics.core.VirtualMachineMetrics;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.apache.nifi.controller.status.ProcessorStatus;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.reporting.AbstractReportingTask;
import org.apache.nifi.reporting.ReportingContext;
import org.apache.nifi.reporting.datadog.api.MetricsBuilder;
import org.apache.nifi.reporting.datadog.metrics.MetricNames;
import org.apache.nifi.reporting.datadog.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.client.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Tags({"reporting", "datadog", "metrics"})
@CapabilityDescription("Publishes metrics from NiFi to datadog")
public class DataDogReportingTask extends AbstractReportingTask {


    private MetricsService metricsService = new MetricsService();
    private DDMetricRegistryBuilder ddMetricRegistryBuilder = new DDMetricRegistryBuilder();
    private MetricRegistry metricRegistry = new MetricRegistry();
    private ConcurrentHashMap<String, Counter> metricsMap = new ConcurrentHashMap<>();

    @OnScheduled
    public void setup(final ConfigurationContext context) throws IOException {
        ddMetricRegistryBuilder.setMetricRegistry(metricRegistry)
                .setName("nifi_metrics")
                .build();
    }

    @Override
    public void onTrigger(ReportingContext context) {
        final ProcessGroupStatus status = context.getEventAccess().getControllerStatus();
        final List<ProcessorStatus> processorStatuses = new ArrayList<>();
        populateProcessorStatuses(status, processorStatuses);

        for (final ProcessorStatus processorStatus : processorStatuses) {
            Map<String, String> statusMetrics = metricsService.getProcessorMetrics(processorStatus);
            for (Map.Entry<String, String> entry : statusMetrics.entrySet()) {
                String metricName = "nifi." + processorStatus.getName() + "." + entry.getKey();
                if (!metricsMap.containsKey(metricName)){
                    Counter counter = metricRegistry.counter(metricName);
                    metricsMap.put(metricName, counter);
                }
                metricsMap.get(metricName).inc(Long.parseLong(entry.getValue()));
            }
        }
    }

    private void populateProcessorStatuses(final ProcessGroupStatus groupStatus, final List<ProcessorStatus> statuses) {
        statuses.addAll(groupStatus.getProcessorStatus());
        for (final ProcessGroupStatus childGroupStatus : groupStatus.getProcessGroupStatus()) {
            populateProcessorStatuses(childGroupStatus, statuses);
        }
    }
}
