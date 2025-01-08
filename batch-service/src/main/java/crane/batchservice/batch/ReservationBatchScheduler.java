package crane.batchservice.batch;


import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Configuration
public class ReservationBatchScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    //프로그램 처음 실행 시 한번만 실행
    @EventListener(ApplicationReadyEvent.class)
    public void runInitJob(){
        String time = LocalDateTime.now().toString();

        try{
            Job job = jobRegistry.getJob("createReservationJob");
            JobParametersBuilder jobParam = new JobParametersBuilder().addString("time", time);
            jobLauncher.run(job, jobParam.toJobParameters());
        }catch(NoSuchJobException e){
            throw new RuntimeException("존재하지 않는 Job: " + e.getMessage(), e);
        }catch( JobInstanceAlreadyCompleteException |
                JobExecutionAlreadyRunningException |
                JobParametersInvalidException |
                JobRestartException e){
            throw new RuntimeException("배치 작업 실행 중 오류 발생: " + e);
        }
    }


    @Scheduled(cron = "0 0 23 * * ? ")
    public void runcreateReservationJob(){
        String time = LocalDateTime.now().toString();

        try{
            Job job = jobRegistry.getJob("createReservationNextWeekJob");
            JobParametersBuilder jobParam = new JobParametersBuilder().addString("time", time);
            jobLauncher.run(job, jobParam.toJobParameters());
        } catch (NoSuchJobException e) {
            throw new RuntimeException("존재하지 않는 Job: " + e.getMessage(), e);
        }catch( JobInstanceAlreadyCompleteException |
                JobExecutionAlreadyRunningException |
                JobParametersInvalidException |
                JobRestartException e ){
            throw new RuntimeException("작업중 오류 발생  : " + e);
        }
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void runEnsembleOpenJob()    {
        String time = LocalDateTime.now().toString();

        try{
            Job job = jobRegistry.getJob("openNextWeekEnsembleJob");
            JobParametersBuilder jobParam = new JobParametersBuilder().addString("time", time);
            jobLauncher.run(job, jobParam.toJobParameters());
        }catch (NoSuchJobException e ){
            throw new RuntimeException("존재하지 않는 Job: " + e.getMessage(), e);
        }catch (JobInstanceAlreadyCompleteException |
                JobExecutionAlreadyRunningException |
                JobParametersInvalidException |
                JobRestartException e){
            throw new RuntimeException("작업 중 오류 발생  : " + e);
        }
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void runOpenInstJob(){
        String time = LocalDateTime.now().toString();

        try{
            Job job = jobRegistry.getJob("openNextWeekInstJob");
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder().addString("time", time);
            jobLauncher.run(job, jobParametersBuilder.toJobParameters());
        }catch (NoSuchJobException e){
            throw new RuntimeException("존재하지 않는 Job: " + e.getMessage(), e);
        }catch(JobInstanceAlreadyCompleteException |
               JobExecutionAlreadyRunningException |
               JobParametersInvalidException |
               JobRestartException e)
        {
            throw new RuntimeException("작업중 오류 발생 : " + e);
        }
    }
}
