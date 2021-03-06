package org.apache.nifi.reporting.datadog;

import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang3.RandomStringUtils;
import org.coursera.metrics.datadog.DatadogReporter;
import org.coursera.metrics.datadog.transport.UdpTransport;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class configures MetricRegistry (passed outside or created from scratch) with Datadog support
 */
public class DDMetricRegistryBuilder {


    private MetricRegistry metricRegistry = null;
    private String name = null;
    private List<String> tags = Arrays.asList();
    private DatadogReporter datadogReporter;

    public DDMetricRegistryBuilder setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
        return this;
    }

    public DDMetricRegistryBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public DDMetricRegistryBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public DatadogReporter getDatadogReporter() {
        return datadogReporter;
    }

    public MetricRegistry build() throws IOException {
        if(metricRegistry == null)
            metricRegistry = new MetricRegistry();

        if(name==null) {
            name = RandomStringUtils.randomAlphanumeric(8);
        }
        datadogReporter = createDatadogReporter(this.metricRegistry);
        return this.metricRegistry;
    }

    //create DataDog reporter
    private DatadogReporter createDatadogReporter(MetricRegistry metricRegistry) throws IOException {
        UdpTransport udpTransport = new UdpTransport.Builder().build();
        DatadogReporter reporter =
                DatadogReporter.forRegistry(metricRegistry)
                        .withHost(InetAddress.getLocalHost().getHostName())
                        .withTransport(udpTransport)
                        .withTags(tags)
                        .build();
        return reporter;
    }
}