package com.dani.veinminer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * VeinMiner (Dani) - minera o veio inteiro de minério / a árvore inteira
 * quando o jogador quebra um bloco agachado (Shift).
 *
 * Roda inteiramente no lado do servidor (que, no single player, é o
 * "servidor interno" do seu próprio jogo), por isso não precisa de tecla
 * própria nem de pacotes de rede.
 */
@Mod(VeinMiner.MOD_ID)
public final class VeinMiner {

    public static final String MOD_ID = "veinminer_dani";

    /**
     * Trava contra recursão: quando o mod quebra um bloco do veio, o Forge
     * dispara o evento de quebra de novo. Sem essa trava, cada bloco
     * quebrado iniciaria um novo veio, em cascata.
     */
    private static final ThreadLocal<Boolean> MINERANDO = ThreadLocal.withInitial(() -> false);

    public VeinMiner(FMLJavaModLoadingContext context) {
        // Forge 1.21.6+ / 26.x (EventBus 7): cada evento tem o seu próprio BUS.
        BlockEvent.BreakEvent.BUS.addListener(VeinMiner::aoQuebrarBloco);
    }

    private static void aoQuebrarBloco(BlockEvent.BreakEvent event) {
        if (MINERANDO.get()) return;

        if (!(event.getPlayer() instanceof ServerPlayer jogador)) return;
        if (!(event.getLevel() instanceof ServerLevel)) return;

        if (Config.EXIGIR_AGACHAR && !jogador.isShiftKeyDown()) return;
        if (jogador.isCreative() && !Config.FUNCIONAR_NO_CRIATIVO) return;

        BlockState origem = event.getState();
        Tipo tipo = tipoDoBloco(origem);
        if (tipo == Tipo.NENHUM) return;

        // Só minera em cadeia se a ferramenta na mão "pode" dropar o bloco
        // (ex.: picareta de pedra não minera diamante em cadeia).
        if (!jogador.hasCorrectToolForDrops(origem)) return;

        List<BlockPos> alvos = encontrarVeio(event.getLevel(), event.getPos(), origem, tipo);
        if (alvos.isEmpty()) return;

        MINERANDO.set(true);
        try {
            for (BlockPos pos : alvos) {
                if (!ferramentaAguenta(jogador)) break;
                // destroyBlock faz exatamente o que acontece quando o jogador
                // quebra um bloco: respeita Fortuna, Toque Suave, gasta
                // durabilidade, dá XP, conta estatística e respeita mods de
                // proteção (que podem cancelar o evento).
                jogador.gameMode.destroyBlock(pos);
            }
        } finally {
            MINERANDO.set(false);
        }
    }

    /** Busca em largura (BFS) pelos blocos iguais conectados ao bloco quebrado. */
    private static List<BlockPos> encontrarVeio(net.minecraft.world.level.LevelAccessor level,
                                                BlockPos inicio, BlockState origem, Tipo tipo) {
        List<BlockPos> resultado = new ArrayList<>();
        Set<BlockPos> visitados = new HashSet<>();
        Deque<BlockPos> fila = new ArrayDeque<>();

        visitados.add(inicio);
        fila.add(inicio);

        // O bloco de origem é quebrado pelo próprio jogo; contamos ele no limite.
        int limite = Config.MAX_BLOCOS - 1;

        while (!fila.isEmpty() && resultado.size() < limite) {
            BlockPos atual = fila.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos vizinho = atual.offset(dx, dy, dz);
                        if (!visitados.add(vizinho)) continue;
                        if (vizinho.distManhattan(inicio) > Config.DISTANCIA_MAXIMA) continue;

                        BlockState estado = level.getBlockState(vizinho);
                        if (mesmoGrupo(origem, estado, tipo)) {
                            resultado.add(vizinho);
                            fila.add(vizinho);
                            if (resultado.size() >= limite) return resultado;
                        }
                    }
                }
            }
        }
        return resultado;
    }

    private enum Tipo { MINERIO, TRONCO, NENHUM }

    private static Tipo tipoDoBloco(BlockState estado) {
        if (Config.MINERIOS && estado.is(Tags.Blocks.ORES)) return Tipo.MINERIO;
        if (Config.ARVORES && estado.is(BlockTags.LOGS)) return Tipo.TRONCO;
        return Tipo.NENHUM;
    }

    /**
     * Minério de ferro e minério de ferro de ardósia contam como o mesmo veio;
     * troncos só se ligam a troncos da mesma madeira.
     */
    private static boolean mesmoGrupo(BlockState origem, BlockState outro, Tipo tipo) {
        if (outro.isAir()) return false;
        Block a = origem.getBlock();
        Block b = outro.getBlock();
        if (a == b) return true;
        if (tipo == Tipo.MINERIO && outro.is(Tags.Blocks.ORES)) {
            return nomeBase(a).equals(nomeBase(b));
        }
        return false;
    }

    private static String nomeBase(Block bloco) {
        return BuiltInRegistries.BLOCK.getKey(bloco).getPath().replace("deepslate_", "");
    }

    /** Para antes de quebrar a ferramenta (deixa a durabilidade de segurança). */
    private static boolean ferramentaAguenta(ServerPlayer jogador) {
        if (jogador.isCreative()) return true;
        ItemStack ferramenta = jogador.getMainHandItem();
        if (!ferramenta.isDamageableItem()) return true;
        int restante = ferramenta.getMaxDamage() - ferramenta.getDamageValue();
        return restante > Config.DURABILIDADE_DE_SEGURANCA;
    }
}
