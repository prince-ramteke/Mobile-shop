package com.shopmanager.message.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessagePayload {

    // phone number / email
    private String recipient;

    // plain text message
    private String message;

    // optional PDF bytes
    private byte[] pdf;

    // optional filename
    private String fileName;
}