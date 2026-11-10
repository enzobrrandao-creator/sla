package com.dani.veinminer;

/**
 * Ajustes do mod. Para mudar, edite os valores abaixo e compile de novo.
 */
public final class Config {

    private Config() {}

    /** Máximo de blocos quebrados de uma vez (contando o primeiro). */
    public static final int MAX_BLOCOS = 64;

    /** Distância máxima (em blocos) a partir do bloco quebrado. */
    public static final int DISTANCIA_MAXIMA = 12;

    /** true = só funciona agachado (Shift). false = sempre funciona. */
    public static final boolean EXIGIR_AGACHAR = true;

    /** Minerar veios de minério. */
    public static final boolean MINERIOS = true;

    /** Derrubar árvores inteiras (troncos). */
    public static final boolean ARVORES = true;

    /** Funcionar também no modo criativo. */
    public static final boolean FUNCIONAR_NO_CRIATIVO = false;

    /** Para de minerar quando a ferramenta chega nessa durabilidade, para não quebrá-la. */
    public static final int DURABILIDADE_DE_SEGURANCA = 1;
}
