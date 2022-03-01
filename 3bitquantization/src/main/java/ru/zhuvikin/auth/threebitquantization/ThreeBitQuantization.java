package ru.zhuvikin.auth.threebitquantization;

import java.util.ArrayList;
import java.util.List;

public final class ThreeBitQuantization {

    public static QuantizedData quantizeFeatures(List<Double> features, double delta) {
        int featuresSize = features.size();

        List<Integer> quantizedFeatures = new ArrayList<>(featuresSize);
        List<Perturbation> perturbations = new ArrayList<>();
        for (int i = 0; i < featuresSize; i++) {
            perturbations.add(new Perturbation());
        }

        for (int j = 0; j < featuresSize; j++) {
            quantizedFeatures.set(j, (int) Math.floor(features.get(j) / delta));
        }

        for (int j = 0; j < featuresSize; j++) {
            int phi = (int) Math.floor(features.get(j) / delta);
            int b12 = (phi - 1) % 4;
            if (b12 < 0) b12 += 4;

            switch (b12) {
                case 0: // 00
                    perturbations.get(j).setBit0(false);
                    perturbations.get(j).setBit1(false);
                    break;
                case 1: // 01
                    perturbations.get(j).setBit0(false);
                    perturbations.get(j).setBit1(true);
                    break;
                case 2: // 10
                    perturbations.get(j).setBit0(true);
                    perturbations.get(j).setBit1(false);
                    break;
                case 3: // 11
                    perturbations.get(j).setBit0(true);
                    perturbations.get(j).setBit1(true);
                    break;
            }

            double bi = delta * (phi + 0.5d);
            perturbations.get(j).setBit2(features.get(j) >= bi);
        }

        return new QuantizedData(quantizedFeatures, perturbations);
    }

    public static List<Integer> restoreFeatures(QuantizedData dataToBeRestored, double delta) {
        //BitSequence restored = new BitSequence(Math.round(dataToBeRestored.getLength() / 4));

        return new ArrayList<>();
    }

}