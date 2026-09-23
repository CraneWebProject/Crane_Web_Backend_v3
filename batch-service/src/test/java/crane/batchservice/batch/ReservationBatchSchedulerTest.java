package crane.batchservice.batch;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 같은 날 두 번 실행되면 같은 JobParameters(= 같은 JobInstance)로 요청되고,
// Spring Batch 가 두 번째 실행을 거부하면 예외 없이 건너뛰는지 확인한다.
class ReservationBatchSchedulerTest {

    private final JobLauncher jobLauncher = mock(JobLauncher.class);
    private final JobRegistry jobRegistry = mock(JobRegistry.class);
    private final ReservationBatchScheduler scheduler = new ReservationBatchScheduler(jobLauncher, jobRegistry);

    @Test
    void sameDayRunUsesSameParametersAndSkipsWhenAlreadyComplete() throws Exception {
        Job job = mock(Job.class);
        when(jobRegistry.getJob("createReservationNextWeekJob")).thenReturn(job);
        when(jobLauncher.run(eq(job), any(JobParameters.class)))
                .thenReturn(null)
                .thenThrow(new JobInstanceAlreadyCompleteException("already complete"));

        scheduler.runcreateReservationJob();
        assertDoesNotThrow(scheduler::runcreateReservationJob);

        ArgumentCaptor<JobParameters> params = ArgumentCaptor.forClass(JobParameters.class);
        verify(jobLauncher, times(2)).run(eq(job), params.capture());
        assertThat(params.getAllValues().get(0).getLocalDate("runDate")).isEqualTo(LocalDate.now());
        assertThat(params.getAllValues().get(1)).isEqualTo(params.getAllValues().get(0));
    }
}
