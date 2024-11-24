package br.com.rsfot.system2.learning;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionSelector {
    private Map<List<Integer>, List<Double>> qTable;
    private List<String> possibleActions;
    private Random random = new Random();


    public ActionSelector(Map<List<Integer>, List<Double>> qTable, List<String> possibleActions) {
        this.qTable = qTable;
        this.possibleActions = possibleActions;
    }

    public String chooseAction(List<Integer> state) {
        List<Double> actions = qTable.get(state);
        if (actions == null || actions.isEmpty()) {
            // Se não houver ações para o estado, escolher uma ação aleatória
            return possibleActions.get(random.nextInt(possibleActions.size()));
        }
        // Encontrar o valor máximo
        double maxValue = Collections.max(actions);
        // Coletar todos os índices com o valor máximo
        List<Integer> maxIndices = IntStream.range(0, actions.size())
                .filter(i -> actions.get(i) == maxValue)
                .boxed()
                .collect(Collectors.toList());
        // Selecionar aleatoriamente um dos índices máximos
        int randomMaxIndex = maxIndices.get(random.nextInt(maxIndices.size()));
        return possibleActions.get(randomMaxIndex);
    }
}