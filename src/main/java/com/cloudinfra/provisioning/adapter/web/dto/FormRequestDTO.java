package com.cloudinfra.provisioning.adapter.web.dto;

import com.cloudinfra.provisioning.domain.model.FormField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormRequestDTO {
    private String conversationId;
    private String message;
    private List<FormField> fields;
}
