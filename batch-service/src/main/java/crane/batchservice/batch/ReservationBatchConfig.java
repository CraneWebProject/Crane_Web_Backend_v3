package crane.batchservice.batch;

import crane.batchservice.client.ReservationClient;
import crane.batchservice.common.advice.ControllerAdvice;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.DuplicateFormatFlagsException;
@RequiredArgsConstructor
@Configuration
public class ReservationBatchConfig {

    private final ReservationClient reservationClient;

//    public ReservationBatchConfig(ReservationClient reservationClient, ControllerAdvice controllerAdvice, DataSourceTransactionManager transactionManager) {
//        this.reservationClient = reservationClient;
//        this.controllerAdvice = controllerAdvice;
//        this.transactionManager = transactionManager;
//    }

    //실행시 예약 1주일 치 생성
    @Bean
    public Job createReservationJob(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager) throws DuplicateFormatFlagsException {
        return new JobBuilder("createReservationJob", jobRepository)
                .start(createReservationStep(jobRepository, transactionManager))
                .build();
    }

    public Step createReservationStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager) {
        return new StepBuilder("createReservationStep", jobRepository)
                .tasklet(createReservationTasklet(), transactionManager)
                .build();
    }

    public Tasklet createReservationTasklet() {
        return((contribution, chunkContext) -> {
            System.out.println("***** Batch *****");
            reservationClient.initReservation();
            return RepeatStatus.FINISHED;
        });
    }


    //매일 저녁 11시 다음주 예약 생성
    @Bean
    public Job createReservationNextWeekJob(JobRepository jobRepository,
                                            PlatformTransactionManager transactionManager) throws DuplicateFormatFlagsException {
        Job job = new JobBuilder("createReservationNextWeekJob", jobRepository)
                .start(createReservationNextWeekStep(jobRepository, transactionManager))
                .build();

        return job;
    }

    public Step createReservationNextWeekStep(JobRepository jobRepository,
                                              PlatformTransactionManager transactionManager) {
        Step step = new StepBuilder("createReservationNextWeekStep", jobRepository)
                .tasklet(createReservationNextWeekTasklet(), transactionManager)
                .build();

        return step;
    }

    public Tasklet createReservationNextWeekTasklet() {
        return((contribution, chunkContext) -> {
            System.out.println("***** Batch *****");
            reservationClient.initNextWeekReservation();
            return RepeatStatus.FINISHED;
        });
    }


    //매일 밤 12시 다음주 합주 예약 오픈
    @Bean
    public Job openNextWeekEnsembleJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager)
        throws DuplicateFormatFlagsException{
        Job job = new JobBuilder("openNextWeekEnsembleJob", jobRepository)
                .start(openNextWeekEnsembleStep(jobRepository, platformTransactionManager))
                .build();
        return job;
    }

    public Step openNextWeekEnsembleStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        Step step = new StepBuilder("openNextWeekEnsembleStep", jobRepository)
                .tasklet(openNextWeekEnsembleTasklet(), platformTransactionManager)
                .build();

        return step;
    }


    public Tasklet openNextWeekEnsembleTasklet(){
        return(((contribution, chunkContext) -> {
            System.out.println("***** Batch *****");
            reservationClient.openEnsembleReservation();
            return RepeatStatus.FINISHED;
        }));
    }


    //매일 낮 12시 다음주 장비 예약 오픈
    @Bean
    public Job openNextWeekInstJob(JobRepository jobRepository,
                                   PlatformTransactionManager platformTransactionManager) throws DuplicateFormatFlagsException {
        return new JobBuilder("openNextWeekInstJob", jobRepository)
                .start(openNextWeekInstStep(jobRepository, platformTransactionManager))
                .build();
    }


    public Step openNextWeekInstStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager){
        return new StepBuilder("openNextWeekInstStep", jobRepository)
                .tasklet(openNextWeekInstTasklet(),transactionManager)
                .build();
    }

    public Tasklet openNextWeekInstTasklet(){
        return ((contribution, chunkContext) -> {
            System.out.println("***** Batch *****");
            reservationClient.openInstReservation();
            return RepeatStatus.FINISHED;
        });
    }

}
