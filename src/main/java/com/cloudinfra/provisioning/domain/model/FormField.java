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
public class FormField {
    private String name;
    private String label;
    private String type;
    private boolean required;
    private String description;
    private List<String> options;
    private String defaultValue;
    private String placeholder;
}
