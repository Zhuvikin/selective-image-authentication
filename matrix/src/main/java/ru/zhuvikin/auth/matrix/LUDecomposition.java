package ru.zhuvikin.auth.matrix;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LUDecomposition {

    private int sourceWidth;
    private int sourceHeight;

    private List<Integer> rows;
    private List<Integer> columns;

    private final Matrix left;
    private final Matrix upper;

}
