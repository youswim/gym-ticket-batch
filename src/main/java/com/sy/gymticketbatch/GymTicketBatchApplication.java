package com.sy.gymticketbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GymTicketBatchApplication {

    private final JobBuilderFactory jobBuilderFactory; // Job 을 만들어주는 Builder

    private final StepBuilderFactory stepBuilderFactory; // Step을 만들어주는 Builder

    public GymTicketBatchApplication(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean // Step 선언
    public Step passStep() {
        return this.stepBuilderFactory.get("passStep") // Step의 이름. 현재는 메서드 이름과 동일하게 설정함
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Execute PassStep");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean // Step 선언
    public Job passJob() {
        return this.jobBuilderFactory.get("passJob") // Step의 이름. 현재는 메서드 이름과 동일하게 설정함
                .start(passStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GymTicketBatchApplication.class, args);
    }

}
