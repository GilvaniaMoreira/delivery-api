package com.deliverytech.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();

    private int status;

    private String error;

    private String message;

    private String path;

    private List<Map<String, String>> details;
}
