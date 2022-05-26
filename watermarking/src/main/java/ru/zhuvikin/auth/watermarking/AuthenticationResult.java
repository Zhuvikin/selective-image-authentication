package ru.zhuvikin.auth.watermarking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResult {

    @Builder.Default
    private boolean authentic = false;

    @Builder.Default
    private String name = "";

}
