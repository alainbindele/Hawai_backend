package com.cloudinfra.provisioning.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDataDTO {
    @NotBlank(message = "Conversation ID is required")
    private String conversationId;

    @NotNull(message = "Form data is required")
    private Map<String, String> data;
}
