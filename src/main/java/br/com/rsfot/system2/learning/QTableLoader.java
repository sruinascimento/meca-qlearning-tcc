package br.com.rsfot.system2.learning;

import java.io.*;
import java.util.List;
import java.util.Map;

public class QTableLoader {
    private Map<List<Integer>, List<Double>> qTable;

    public void loadQTable(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            qTable = (Map<List<Integer>, List<Double>>) objectInputStream.readObject();
        }
    }

    public Map<List<Integer>, List<Double>> getQTable() {
        return qTable;
    }
}