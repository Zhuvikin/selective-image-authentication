package ru.zhuvikin.auth.matrix.sparse;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class LUDecomposition {

    private Matrix left;
    private Matrix upper;

}
