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

import static ru.zhuvikin.auth.watermarking.StenographyEmbedding.HWT_LEVELS;

public final class SelectiveImageAuthentication {

    public static BufferedImage watermark(String name,
                                          BufferedImage image,
                                          WatermarkingParameters parameters,
                                          RsaKeys.PrivateKey privateKey) {
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
        int informationBits = capacity - (int) Math.floor((double) capacity * eccCodeRate);
        int maximumNameLength = parameters.getMaximumNameLength();
        int nameBitsCount = maximumNameLength * StringEncoder.BITS_PER_SYMBOL;
        int featuresLength = (int) Math.floor((double) (informationBits - nameBitsCount - privateKeyLength) / 3.0d);

        if (featuresLength <= 0) {
            throw new RuntimeException("LDPC rate is too small for given capacity and DS length");
        }

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

        // Concatenate 3-bit quantization perturbations, name and signature
        List<Perturbation> perturbations = quantizedData.getPerturbation();
        BitSet data = new BitSet();
        for (int i = 0; i < featuresLength; i++) {
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
        }

        BitSet nameData = StringEncoder.encode(name, maximumNameLength);
        for (int i = 0; i < nameBitsCount; i++) {
            if (nameData.get(i)) {
                data.set(featuresLength * 3 + i);
            }
        }

        for (int i = 0; i < privateKeyLength; i++) {
            if (signature.get(i)) {
                data.set(featuresLength * 3 + nameBitsCount + i);
            }
        }

        // Encode with LDPC-code
        Code code = Code.of(capacity, capacity - informationBits);
        BitSet encoded = LdpcEncoder.encode(code, data, informationBits);

        // Embed by means of Haar Wavelet Transform
        return StenographyEmbedding.embed(image, encoded, capacity, gamma);
    }

    public static AuthenticationResult authenticate(BufferedImage image,
                                       WatermarkingParameters parameters,
                                       RsaKeys.PublicKey publicKey) {
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
        int informationBits = capacity - (int) Math.floor((double) capacity * eccCodeRate);
        int maximumNameLength = parameters.getMaximumNameLength();
        int nameBitsCount = maximumNameLength * StringEncoder.BITS_PER_SYMBOL;
        int featuresLength = (int) Math.floor((double) (informationBits - nameBitsCount - publicKeyLength) / 3.0d);

        double sigma = parameters.getSigma();
        double delta = parameters.getDelta();
        double gamma = parameters.getGamma();

        // Extract by means of Haar Wavelet Transform
        BitSet extracted = StenographyEmbedding.extract(image, capacity, gamma);

        // Decode with LDPC-code
        Code code = Code.of(capacity, capacity - informationBits);
        BitSet decoded = LdpcEncoder.decode(code, extracted, capacity);

        // Separate signature and 3-bit quantization perturbations
        List<Perturbation> perturbations = new ArrayList<>();

        for (int i = 0; i < featuresLength; i++) {
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
        }

        BitSet extractedName = new BitSet();
        for (int i = 0; i < nameBitsCount; i++) {
            if (decoded.get(featuresLength * 3 + i)) {
                extractedName.set(i);
            }
        }

        String name = StringEncoder.decode(extractedName, maximumNameLength);

        BitSet extractedSignature = new BitSet();
        for (int i = 0; i < publicKeyLength; i++) {
            if (decoded.get(featuresLength * 3 + nameBitsCount + i)) {
                extractedSignature.set(i);
            }
        }

        // Get features of authenticating image
        List<Double> features = FeaturesCalculator.features(image, sigma, featuresLength);

        // Restore feature vector with 3-bit quantization
        List<Integer> restoredFeatures = ThreeBitQuantization.restoreFeatures(features, perturbations, delta);

        // Verify signature
        boolean authentic = SignatureProvider.verify(restoredFeatures, publicKey, extractedSignature);

        AuthenticationResult authenticationResult = AuthenticationResult.builder()
                .authentic(authentic)
                .build();

        if (authentic) {
            authenticationResult.setName(name);
        }
        return authenticationResult;
    }

}