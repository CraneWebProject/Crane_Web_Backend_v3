package crane.batchservice.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ReservationBatchScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    //프로그램 처음 실행 시 한번만 실행
    @EventListener(ApplicationReadyEvent.class)
    public void runInitJob(){
        runJob("createReservationJob");
    }


    @Scheduled(cron = "0 0 23 * * ? ")
    public void runcreateReservationJob(){
        runJob("createReservationNextWeekJob");
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void runEnsembleOpenJob()    {
        runJob("openNextWeekEnsembleJob");
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void runOpenInstJob(){
        runJob("openNextWeekInstJob");
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void runDeleteReservationJob(){
        runJob("deleteReservationJob");
    }

    // 제로 오프셋: 실행 시각(now) 대신 실행 날짜(00:00 기준)를 JobParameter 로 써서 같은 날짜의 같은 잡은 같은 JobInstance 가 되게 한다.
    // 이미 완료됐거나 실행 중인 인스턴스는 Spring Batch 가 거부하므로 로그만 남기고 건너뛴다. FAILED 인스턴스는 같은 날짜로 재실행할 수 있다.
    private void runJob(String jobName){
        LocalDate runDate = LocalDate.now();

        try{
            Job job = jobRegistry.getJob(jobName);
            JobParametersBuilder jobParam = new JobParametersBuilder().addLocalDate("runDate", runDate);
            jobLauncher.run(job, jobParam.toJobParameters());
        }catch(JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException e){
            log.info("이미 완료됐거나 실행 중인 배치라 건너뜀 - job: {}, runDate: {}", jobName, runDate);
        }catch(NoSuchJobException e){
            throw new RuntimeException("존재하지 않는 Job: " + e.getMessage(), e);
        }catch(JobParametersInvalidException | JobRestartException e){
            throw new RuntimeException("배치 작업 실행 중 오류 발생: " + e);
        }
    }
}
