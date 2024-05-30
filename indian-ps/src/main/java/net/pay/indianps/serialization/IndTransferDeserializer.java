package net.pay.indianps.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import ru.jb.micro.planner.entity.ps.Transfer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class IndTransferDeserializer implements Deserializer<Transfer> {
    @Override
    public Transfer deserialize(String s, byte[] bytes) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        Transfer transfer = null;
        try {
            transfer = objectMapper.readValue(bytes, Transfer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return transfer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public Transfer deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public Transfer deserialize(String topic, Headers headers, ByteBuffer data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
