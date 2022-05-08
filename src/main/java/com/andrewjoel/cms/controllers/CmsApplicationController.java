package com.andrewjoel.cms.controllers;

import com.andrewjoel.cms.models.hbm.HbmEntity;
import com.andrewjoel.cms.provider.CmsEntityTemplate;
import com.andrewjoel.cms.provider.EntityQueryTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class CmsApplicationController {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CmsEntityTemplate cmsEntityTemplate;

    @Autowired
    EntityQueryTemplate entityQueryTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping
    public String hello(@RequestBody final String body) throws JsonMappingException, JsonProcessingException {
        final HbmEntity model = cmsEntityTemplate.convertXmlToPojo("users");
        final Map<String, Object> values = objectMapper.readValue(body, new TypeReference<HashMap<String, Object>>() {
        });
        final Query query = entityQueryTemplate.prepareInsert(model, values);
        return query.getResultList().toString();
    }
}
