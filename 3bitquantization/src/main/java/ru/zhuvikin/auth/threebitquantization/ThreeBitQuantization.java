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

    public static List<Integer> restoreFeatures(List<Double> features, List<Perturbation> extractedPerturbations, double delta) {
        List<Perturbation> estimatePerturbations = quantizeFeatures(features, delta).getPerturbation();
        int featuresSize = features.size();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < featuresSize; i++) {

            int bExtr = (extractedPerturbations.get(i).isBit1() ? 1 : 0) + 2 * (extractedPerturbations.get(i).isBit0() ? 1 : 0);
            int bCalc = (estimatePerturbations.get(i).isBit1() ? 1 : 0) + 2 * (estimatePerturbations.get(i).isBit0() ? 1 : 0);

            int rB = (bExtr + 1) % 4;
            if (rB < 0) rB += 4;
            if (rB > 3) rB -= 4;

            int lB = (bExtr - 1) % 4;
            if (lB < 0) lB += 4;
            if (lB > 3) lB -= 4;

            int a = 0;
            if (bCalc == lB) {
                a = 0;
            } else if (bCalc == rB) {
                a = 1;
            } else {
                a = 2;
            }

            int p3Extracted = extractedPerturbations.get(i).isBit2() ? 1 : 0;
            int p3Calculated = estimatePerturbations.get(i).isBit2() ? 1 : 0;

            double r;
            if (a == 0 && p3Extracted == 0) {
                r = features.get(i) + delta;
            } else if (a == 0 && p3Calculated == 1) {
                r = features.get(i) + delta;
            } else if (a == 1 && p3Extracted == 1) {
                r = features.get(i) - delta;
            } else if (a == 1 && p3Calculated == 0) {
                r = features.get(i) - delta;
            } else {
                r = features.get(i);
            }

            result.add((int) Math.floor(r / delta));
        }
        return result;
    }

}