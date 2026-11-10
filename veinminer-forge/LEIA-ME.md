# VeinMiner (Dani) — Forge

Agache (Shift) e quebre um minério ou tronco: o mod quebra o veio inteiro ou a árvore toda.
Respeita Fortuna, Toque Suave e a durabilidade, e para antes de a ferramenta quebrar.

Configurado para **Minecraft 26.3 + Forge 66.1.5**. Outra versão do Forge também funciona: é só informar na hora de compilar.

## Como compilar pelo GitHub (sem instalar nada)

1. Crie uma conta em https://github.com (é grátis).
2. Clique em **New repository**, dê um nome (ex.: `veinminer`), marque **Public** e crie.
3. Na página do repositório, clique em **uploading an existing file**.
   Arraste **todo o conteúdo desta pasta**, incluindo a pasta `.github`, e clique em **Commit changes**.
   - Atenção: a pasta `.github` começa com ponto e fica oculta no Windows e no Mac.
     Se ela não subir, crie o arquivo pelo site: **Add file → Create new file**, com o nome
     `.github/workflows/compilar.yml`, e cole o conteúdo do arquivo.
4. Abra a aba **Actions**. Se o GitHub pedir, autorize os workflows.
5. Clique em **Compilar mod** → **Run workflow**, confira as versões e clique em **Run workflow**.
6. Espere uns 5 a 10 minutos até aparecer o ✅ verde. Clique na execução e, em **Artifacts**, baixe o `.zip`.
   O `.jar` do mod está dentro dele.
7. Coloque o `.jar` na pasta `mods` da sua instância Forge no Horizon.

Se aparecer um ❌ vermelho, clique na etapa que falhou, copie o erro e mande para o Claude corrigir.

## Como compilar no seu PC (alternativa)

1. Instale o JDK 25 (https://adoptium.net).
2. Baixe o MDK do Forge 66.1.5 para 26.3 em https://files.minecraftforge.net e descompacte.
3. Apague a pasta `src/main/java` do MDK e copie a `src/main/java` deste projeto no lugar.
   Substitua também o `src/main/resources/META-INF/mods.toml`.
4. Na pasta do MDK, rode `gradlew build` (Windows) ou `./gradlew build` (Mac/Linux).
5. O `.jar` sai em `build/libs`.

## Ajustes

Edite `src/main/java/com/dani/veinminer/Config.java` e compile de novo:
- `MAX_BLOCOS` (padrão 64)
- `EXIGIR_AGACHAR` (padrão true)
- `MINERIOS` e `ARVORES`
- `DISTANCIA_MAXIMA`
- `FUNCIONAR_NO_CRIATIVO`
- `DURABILIDADE_DE_SEGURANCA`
