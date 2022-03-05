package ru.zhuvikin.auth.code;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static ru.zhuvikin.auth.code.CodeCache.generateNewCode;

public class CodeGenerationTest {

    @Test
    public void testBigCodeGenerationSpeed() {
        Code code = generateNewCode(1024, 2048);

        assertEquals(CodeCache.of(1024, 2048), code);
    }

}
