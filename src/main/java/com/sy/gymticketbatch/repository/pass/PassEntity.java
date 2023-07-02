package com.sy.gymticketbatch.repository.pass;

import com.sy.gymticketbatch.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Table(name="pass")
@Entity
public class PassEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passSeq;

    private Integer packageSeq;

    private String userId;

    @Enumerated(EnumType.STRING)
    private PassStatus status;

    private Integer remainingCount;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private LocalDateTime expiredAt;
}
