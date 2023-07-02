package com.sy.gymticketbatch.job.pass;

import com.sy.gymticketbatch.repository.pass.PassEntity;
import com.sy.gymticketbatch.repository.pass.PassStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpirePassesJobConfig {

    private final int CHUNK_SIZE = 5;
    // 실제로 이렇게 작은 size는 사용하지 않음.
    // 연습이기 때문에 그냥 5개로 설정

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory emf;

    public ExpirePassesJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory emf) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.emf = emf;
    }

    @Bean
    public Job expirePassesJob() {
        return this.jobBuilderFactory.get("expirePassesJob") // expirePassesJob 라는 이름의 Job 생성
                .start(expirePassesStep()) // 해당 step 실행
                .build();
    }

    @Bean
    public Step expirePassesStep() {
        return this.stepBuilderFactory.get("expirePassesStep")
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE) // Input, Output 타입을 지정
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();

    }

@Bean
@StepScope
public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
    return new JpaCursorItemReaderBuilder<PassEntity>()
            .name("expirePassesItemReader")
            .entityManagerFactory(emf)
            .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
            .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
            .build();
}

    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    @Bean
    public JpaItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(emf)
                .build();
    }
}
