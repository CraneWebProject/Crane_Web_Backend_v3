/*
package crane.boardservice.common.config;

import crane.boardservice.repository.BoardRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilder jobBuilder;

    @Autowired
    private StepBuilder stepBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BoardRepository boardRepository;

    public BatchConfig(JobBuilder jobBuilder, StepBuilder stepBuilder, JdbcTemplate jdbcTemplate, BoardRepository boardRepository) {
        this.jobBuilder = jobBuilder;
        this.stepBuilder = stepBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.boardRepository = boardRepository;
    }

    @Bean
    public Job myJob()
}
*/
