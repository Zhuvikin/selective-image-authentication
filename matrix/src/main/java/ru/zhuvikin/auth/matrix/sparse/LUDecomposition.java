package ru.zhuvikin.auth.matrix.sparse;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class LUDecomposition {

    private int sourceWidth;
    private int sourceHeight;

    private List<Integer> rows;
    private List<Integer> columns;

    private Matrix left;
    private Matrix upper;

}
