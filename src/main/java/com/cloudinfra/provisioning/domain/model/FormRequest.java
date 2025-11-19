package com.cloudinfra.provisioning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormRequest {
    private String conversationId;
    private String message;
    private List<FormField> fields;
}
