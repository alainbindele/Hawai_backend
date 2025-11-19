package com.cloudinfra.provisioning.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Bean
    public com.supabase.Supabase supabaseClient() {
        return new com.supabase.Supabase(supabaseUrl, supabaseKey);
    }
}
