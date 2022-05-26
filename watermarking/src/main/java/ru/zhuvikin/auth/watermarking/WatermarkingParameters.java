package ru.zhuvikin.auth.watermarking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WatermarkingParameters {

    @Builder.Default
    private double eccCodeRate = 0.5;

    @Builder.Default
    private double sigma = 10;

    @Builder.Default
    private double gamma = 3;

    @Builder.Default
    private double delta = 10;

    @Builder.Default
    private int maximumNameLength = 32;

}
