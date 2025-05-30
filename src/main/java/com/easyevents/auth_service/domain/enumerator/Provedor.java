package com.easyevents.auth_service.domain.enumerator;

public enum Provedor {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    GITHUB("github"),
    LOCAL("local");

    private final String nome;

    Provedor(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static Provedor fromString(String nome) {
        for (Provedor provedor : Provedor.values()) {
            if (provedor.nome.equalsIgnoreCase(nome)) {
                return provedor;
            }
        }
        throw new IllegalArgumentException("Provedor não suportado: " + nome);
    }
}
