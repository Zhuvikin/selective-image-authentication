package ru.zhuvikin.auth.watermarking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WatermarkingParameters {

    int length;
    double sigma;
    double gamma;
    double delta;

}
