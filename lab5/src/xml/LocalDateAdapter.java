package com.labwork.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * JAXB-адаптер для сериализации и десериализации LocalDate в XML.
 * Преобразует LocalDate в строку формата ISO и обратно.
 */

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public LocalDateAdapter() {} 
    
    @Override
    public LocalDate unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }
        return LocalDate.parse(v, FORMATTER);
    }
    
    @Override
    public String marshal(LocalDate v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.format(FORMATTER);
    }
}
