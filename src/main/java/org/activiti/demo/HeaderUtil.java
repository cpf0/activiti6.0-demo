package org.activiti.demo;

import org.springframework.http.HttpHeaders;

public class HeaderUtil {
    public static HttpHeaders createEntityDeletionAlert(String entityName, String modelId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(entityName,modelId);
        return  httpHeaders;
    }

    public static HttpHeaders createFailureAlert(String entityName, String modelId, String s) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(entityName,modelId);
        return  httpHeaders;
    }

    public static HttpHeaders createAlert(String s, String modelId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(s,modelId);
        return  httpHeaders;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String s) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(entityName,s);
        return  httpHeaders;
    }
}
