package ru.zhuvikin.auth.watermarking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WatermarkingParameters {

    @Builder.Default
    private double eccCodeRate = 2;

    @Builder.Default
    private double sigma = 2;

    @Builder.Default
    private double gamma = 3;

    @Builder.Default
    private double delta = 0.5;

}
