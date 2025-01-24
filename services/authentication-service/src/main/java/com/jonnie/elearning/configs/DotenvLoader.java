package com.jonnie.elearning.configs;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvLoader {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}