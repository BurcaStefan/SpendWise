package com.example.spendwise.application.factory;

public interface EntityFactory<T, D> {
    T create(D dto);
}