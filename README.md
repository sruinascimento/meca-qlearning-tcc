# Meca-Q-Learning Agent

Este projeto implementa um agente inteligente para o mundo de Wumpus, utilizando Q-Learning para tomada de decisões baseadas em percepções sensoriais. O agente é capaz de explorar ambientes simulados (cavernas) com o objetivo de encontrar o ouro e retornar à posição inicial, enquanto evita perigos como o Wumpus e buracos.

---

## 📋 Funcionalidades

1. **Treinamento do agente**: Usa Q-Learning para treinar uma tabela Q (Q-Table) com base em episódios simulados.
2. **Execução do agente**: Testa o agente em uma caverna previamente configurada, utilizando uma Q-Table existente.
3. **Sensores e atuadores**: Simula percepções do ambiente e toma decisões baseadas nas ações aprendidas.
4. **Relatórios de treinamento**: Gera relatórios CSV detalhados para análise de desempenho.

---

## 🚀 Como executar o projeto

Você pode executar o programa em dois modos principais:

### Modo Treino
No modo treino, o agente será treinado com parâmetros fornecidos e gerará uma Q-Table, além de um relatório de episódios.

**Comando de execução**:
```bash
djar meca-qlearning-1.0-SNAPSHOT-jar-with-dependencies.jar <alpha> <gamma> <epsilon> <epsilonDecay> <numberOfEpisodes> <caveName>
```

**Exemplos**
```bash
java -jar meca-qlearning-1.0-SNAPSHOT-jar-with-dependencies.jar 0.1 0.99 1.0 0.001 2000 FIRST_CAVE
```

```bash
java -jar meca-qlearning-1.0-SNAPSHOT-jar-with-dependencies.jar 0.1 0.99 1.0 0.001 2000 SECOND_CAVE
```



## 📜 Dependências
Java 21 ou superior.
Bibliotecas externas:
Inclusas no arquivo JAR -with-dependencies.

