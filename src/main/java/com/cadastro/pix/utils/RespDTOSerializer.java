package com.cadastro.pix.utils;

import com.cadastro.pix.dto.account.SimpleAccountListWithUserDTO;
import com.cadastro.pix.dto.account.SimpleAccountWithPixDTO;
import com.cadastro.pix.dto.account.SimpleAccountWithUserDTO;
import com.cadastro.pix.dto.pixKey.PixKeyDTO;
import com.cadastro.pix.domain.RespDTO ;
import com.cadastro.pix.dto.pixKey.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.dto.pixKey.PixKeyWithAccountDTO;
import com.cadastro.pix.dto.user.UserDTO;
import com.cadastro.pix.dto.user.UserListDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class RespDTOSerializer extends JsonSerializer<RespDTO> {

    @Override
    public void serialize(RespDTO respDTO, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("httpStatus", respDTO.getHttpStatus().toString());

        if (respDTO.getMessage() != null) {
            gen.writeStringField("message", respDTO.getMessage());
        } else if (respDTO.getData() != null) {
            if (respDTO.getData() instanceof UserListDTO) {
                gen.writeObjectField("data", respDTO.getData());
            } else if (respDTO.getData() instanceof PixKeyListWithAccountAndUserDTO) {
                gen.writeObjectField("data", respDTO.getData());
            } else if (respDTO.getData() instanceof SimpleAccountListWithUserDTO) {
                gen.writeObjectField("data", respDTO.getData());
            } else {
                gen.writeObjectFieldStart("data");
                if (respDTO.getData() instanceof UserDTO) {
                    gen.writeObjectField("user", respDTO.getData());
                } else if (respDTO.getData() instanceof SimpleAccountWithPixDTO) {
                    gen.writeObjectField("account", respDTO.getData());
                } else if (respDTO.getData() instanceof SimpleAccountWithUserDTO) {
                    gen.writeObjectField("account", respDTO.getData());
                } else if (respDTO.getData() instanceof PixKeyDTO) {
                    gen.writeObjectField("pixKey", respDTO.getData());
                } else if (respDTO.getData() instanceof PixKeyWithAccountDTO) {
                    gen.writeObjectField("pixKey", respDTO.getData());
                }
                gen.writeEndObject();
            }
        }

        gen.writeEndObject();
    }

    private void serializeUserListDTO(UserListDTO userListDTO, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("users");
        serializerProvider.defaultSerializeValue(userListDTO.getUsers(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}