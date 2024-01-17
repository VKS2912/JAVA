package com.example.rayzi.agora.token;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
