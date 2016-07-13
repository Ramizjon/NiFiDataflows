package org.apache.nifi.reporting.datadog;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.apache.nifi.controller.status.ProcessorStatus;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.reporting.EventAccess;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.reporting.ReportingContext;
import org.apache.nifi.reporting.ReportingInitializationContext;
import org.apache.nifi.reporting.datadog.metrics.MetricsService;
import org.apache.nifi.util.MockPropertyValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class TestDataDogReportingTask {

    private ProcessGroupStatus status;
    final String reportingPeriod = "10";

    @Before
    public void setup(){
        status = new ProcessGroupStatus();
        status.setId("1234");
        status.setFlowFilesReceived(5);
        status.setBytesReceived(10000);
        status.setFlowFilesSent(10);
        status.setBytesSent(20000);
        status.setQueuedCount(100);
        status.setQueuedContentSize(1024L);
        status.setBytesRead(60000L);
        status.setBytesWritten(80000L);
        status.setActiveThreadCount(5);
        status.setInputCount(2);
        status.setOutputCount(4);

        // create a processor status
        ProcessorStatus procStatus = new ProcessorStatus();
        procStatus.setProcessingNanos(123456789);
        procStatus.setInputCount(2);
        procStatus.setOutputCount(4);
        procStatus.setName("sampleProcessor");
        Collection<ProcessorStatus> processorStatuses = new ArrayList<>();
        processorStatuses.add(procStatus);
        status.setProcessorStatus(processorStatuses);

        ProcessGroupStatus groupStatus = new ProcessGroupStatus();
        groupStatus.setProcessorStatus(processorStatuses);

        Collection<ProcessGroupStatus> groupStatuses = new ArrayList<>();
        groupStatuses.add(groupStatus);
        status.setProcessGroupStatus(groupStatuses);

    }

    @Test
    public void testOnTrigger() throws InitializationException, IOException {
        final ReportingContext context = Mockito.mock(ReportingContext.class);
        Mockito.when(context.getProperty(DataDogReportingTask.REPORTING_PERIOD))
                .thenReturn(new MockPropertyValue(reportingPeriod, null));
        EventAccess eventAccess = Mockito.mock(EventAccess.class);
        Mockito.when(eventAccess.getControllerStatus()).thenReturn(status);
        Mockito.when(context.getEventAccess()).thenReturn(eventAccess);

        final ComponentLog logger = Mockito.mock(ComponentLog.class);
        final ReportingInitializationContext initContext = Mockito.mock(ReportingInitializationContext.class);
        Mockito.when(initContext.getIdentifier()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(initContext.getLogger()).thenReturn(logger);

        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);

        DataDogReportingTask dataDogReportingTask = new TestableDataDogReportingTask();
        dataDogReportingTask.initialize(initContext);
        dataDogReportingTask.setup(configurationContext);
        dataDogReportingTask.onTrigger(context);
    }

    private class TestableDataDogReportingTask extends DataDogReportingTask {
        @Override
        protected MetricsService getMetricsService() {
            return new MetricsService();
        }

        @Override
        protected DDMetricRegistryBuilder getMetricRegistryBuilder() {
            return new DDMetricRegistryBuilder();
        }

        @Override
        protected MetricRegistry getMetricRegistry() {
            return new MetricRegistry();
        }

        @Override
        protected ConcurrentHashMap<String, AtomicDouble> getMetricsMap() {
            return new ConcurrentHashMap<>();
        }
    }

}
