package com.example.spendwise.domain.factory;

public interface EntityFactory<T, D> {
    T create(D dto);
}