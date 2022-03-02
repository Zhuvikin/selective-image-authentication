package ru.zhuvikin.auth.threebitquantization;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.zhuvikin.auth.threebitquantization.ThreeBitQuantization.quantizeFeatures;
import static ru.zhuvikin.auth.threebitquantization.ThreeBitQuantization.restoreFeatures;

public class ThreeBitQuantizationTest {

    private static final List<Double> FEATURES = Arrays.asList(0.1, 0.5, -2.2, 1.3, -0.1);
    private static final List<Double> RECEIVED_FEATURES = Arrays.asList(-0.1, 0.6, -2.1, 1.5, -0.1);
    private static final double DELTA = 0.5;

    @Test
    public void testQuantizeFeatures() {
        QuantizedData quantizedData = quantizeFeatures(FEATURES, DELTA);

        assertEquals(FEATURES.size(), quantizedData.getQuantizedFeatures().size());
        assertEquals(FEATURES.size(), quantizedData.getPerturbation().size());
    }

    @Test
    public void testRestoreFeatures() {
        QuantizedData quantizedData = quantizeFeatures(FEATURES, DELTA);

        assertEquals(FEATURES.size(), quantizedData.getQuantizedFeatures().size());
        assertEquals(FEATURES.size(), quantizedData.getPerturbation().size());

        List<Integer> restoredFeatures = restoreFeatures(RECEIVED_FEATURES, quantizedData.getPerturbation(), DELTA);

        assertEquals(quantizedData.getQuantizedFeatures(), restoredFeatures);
    }

}