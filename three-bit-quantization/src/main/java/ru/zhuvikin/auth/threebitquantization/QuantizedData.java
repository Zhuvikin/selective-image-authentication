package ru.zhuvikin.auth.threebitquantization;

import lombok.Getter;

import java.util.List;

@Getter
public class QuantizedData {

    private List<Integer> quantizedFeatures;
    private List<Perturbation> perturbation;

    QuantizedData(List<Integer> quantizedFeatures, List<Perturbation> perturbation) {
        this.quantizedFeatures = quantizedFeatures;
        this.perturbation = perturbation;
    }

}
