package ru.zhuvikin.auth.watermarking;

import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.ldpc.LdpcEncoder;
import ru.zhuvikin.auth.security.RsaKeys;
import ru.zhuvikin.auth.security.SignatureProvider;
import ru.zhuvikin.auth.threebitquantization.Perturbation;
import ru.zhuvikin.auth.threebitquantization.QuantizedData;
import ru.zhuvikin.auth.threebitquantization.ThreeBitQuantization;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static ru.zhuvikin.auth.watermarking.StenographyEmbedding.HWT_LEVELS;

public final class SelectiveImageAuthentication {

    private static final ConcurrentHashMap<Double, ConcurrentHashMap<Integer, Code>> CODES = new ConcurrentHashMap<>();

    public static BufferedImage watermark(BufferedImage image, WatermarkingParameters parameters, RsaKeys.PrivateKey privateKey) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width != height) {
            throw new IllegalArgumentException("Only rect images are currently supported");
        }
        int privateKeyLength = privateKey.getLength();

        int hwtDomainDimension = (int) Math.floor((double) width / Math.pow(2, HWT_LEVELS));
        int domainCapacity = (int) Math.pow(hwtDomainDimension, 2);
        int capacity = domainCapacity * 2;

        double eccCodeRate = parameters.getEccCodeRate();
        int blockLength = (int) Math.floor((double) capacity / eccCodeRate);
        int featuresLength = (int) Math.floor((double) (blockLength - privateKeyLength) / 3.0d);

        double sigma = parameters.getSigma();
        double delta = parameters.getDelta();
        double gamma = parameters.getGamma();

        // Get features
        List<Double> features = FeaturesCalculator.features(image, sigma, featuresLength);

        // Apply 3-bit quantization
        QuantizedData quantizedData = ThreeBitQuantization.quantizeFeatures(features, delta);

        // Sign quantized features
        List<Integer> quantizedFeatures = quantizedData.getQuantizedFeatures();
        BitSet signature = SignatureProvider.sign(quantizedFeatures, privateKey);

        // Concatenate signature and 3-bit quantization perturbations
        List<Perturbation> perturbations = quantizedData.getPerturbation();
        BitSet data = new BitSet();
        int dataLength = featuresLength + privateKeyLength;
        for (int i = 0; i < dataLength; i++) {
            if (i < featuresLength) {
                Perturbation perturbation = perturbations.get(i);
                if (perturbation.isBit0()) {
                    data.set(i * 3);
                }
                if (perturbation.isBit1()) {
                    data.set(i * 3 + 1);
                }
                if (perturbation.isBit2()) {
                    data.set(i * 3 + 2);
                }
            } else {
                if (signature.get(i - featuresLength)) {
                    data.set(i);
                }
            }
        }

        // Encode with LDPC-code
        Code code = getCode(capacity, eccCodeRate);

        BitSet encoded = LdpcEncoder.encode(code, data, dataLength);

        // Embed by means of Haar Wavelet Transform
        return StenographyEmbedding.embed(image, encoded, capacity, gamma);
    }

    public static boolean authenticate(BufferedImage image, WatermarkingParameters parameters, RsaKeys.PublicKey publicKey) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width != height) {
            throw new IllegalArgumentException("Only rect images are currently supported");
        }
        int publicKeyLength = publicKey.getLength();

        int hwtDomainDimension = (int) Math.floor((double) width / Math.pow(2, HWT_LEVELS));
        int domainCapacity = (int) Math.pow(hwtDomainDimension, 2);
        int capacity = domainCapacity * 2;

        double eccCodeRate = parameters.getEccCodeRate();
        int blockLength = (int) Math.floor((double) capacity / eccCodeRate);
        int featuresLength = (int) Math.floor((double) (blockLength - publicKeyLength) / 3.0d);

        double sigma = parameters.getSigma();
        double delta = parameters.getDelta();
        double gamma = parameters.getGamma();

        // todo: 1. Extract by means of Haar Wavelet Transform
        BitSet extracted = StenographyEmbedding.extract(image, capacity, gamma);

        // todo: 2. Decode with LDPC-code
        Code code = getCode(capacity, eccCodeRate);
        BitSet decoded = LdpcEncoder.decode(code, extracted, capacity);

        // todo: 3. Separate signature and 3-bit quantization perturbations
        List<Perturbation> perturbations = new ArrayList<>();
        BitSet extractedSignature = new BitSet();
        int dataLength = featuresLength + publicKeyLength;
        for (int i = 0; i < dataLength; i++) {
            if (i < featuresLength) {
                Perturbation perturbation = new Perturbation();
                if (decoded.get(i * 3)) {
                    perturbation.setBit0(true);
                }
                if (decoded.get(i * 3 + 1)) {
                    perturbation.setBit1(true);
                }
                if (decoded.get(i * 3 + 2)) {
                    perturbation.setBit2(true);
                }
                perturbations.add(perturbation);
            } else {
                if (decoded.get(i - featuresLength)) {
                    extractedSignature.set(i);
                }
            }
        }

        // todo: 4. Get features of authenticating image
        List<Double> features = FeaturesCalculator.features(image, sigma, featuresLength);

        // todo: 5. Restore feature vector with 3-bit quantization
        List<Integer> restoredFeatures = ThreeBitQuantization.restoreFeatures(features, perturbations, delta);

        // todo: 6. Verify signature
        return SignatureProvider.verify(restoredFeatures, publicKey, extractedSignature);
    }

    private static Code getCode(int capacity, double eccCodeRate) {
        int blockLength = (int) Math.floor((double) capacity / eccCodeRate);
        Code code;
        if (CODES.containsKey(eccCodeRate)) {
            ConcurrentHashMap<Integer, Code> rateCodes = CODES.get(eccCodeRate);
            if (rateCodes.containsKey(capacity)) {
                code = rateCodes.get(capacity);
            } else {
                code = new Code(blockLength, capacity);
                rateCodes.put(capacity, code);
            }
        } else {
            code = new Code(blockLength, capacity);
            CODES.put(eccCodeRate, new ConcurrentHashMap<>());
            CODES.get(eccCodeRate).put(capacity, code);
        }
        return code;
    }

}