package com.n26.transactionsTrack.utils;

import java.io.IOException;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomDoubleSerializer  extends JsonSerializer<Double>{
    @Override
    public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (null == value || 0.0 == value) {
            //write the word 'null' if there's no value available
            jgen.writeNumber(0.0);
        } else {
            final String pattern = ".##";
            //final String pattern = "###,###,##0.00";
            final DecimalFormat myFormatter = new DecimalFormat(pattern);
            final String output = myFormatter.format(value);
            jgen.writeNumber(output);
        }
    }
}
