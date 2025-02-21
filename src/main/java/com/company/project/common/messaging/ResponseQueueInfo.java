package com.company.project.common.messaging;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseQueueInfo implements Serializable {

    private String exchangeName;
    private String routingKey;

}
