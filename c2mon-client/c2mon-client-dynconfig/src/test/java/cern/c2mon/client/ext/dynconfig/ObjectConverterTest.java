package cern.c2mon.client.ext.dynconfig;

import cern.c2mon.shared.common.datatag.address.OPCCommandHardwareAddress.COMMAND_TYPE;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectConverterTest {
    final Random random = new Random();

    @Test
    public void convertNonStringObjectToIntShouldThrowException() {
        assertThrows(UnsupportedOperationException.class, () -> ObjectConverter.convert(int.class, new int[]{}));
    }

    @Test
    public void convertStringToRandomObjShouldThrowException() {
        assertThrows(UnsupportedOperationException.class, () -> ObjectConverter.convert(Random.class, "Random"));
    }

    @Test
    public void convertNullShouldThrowException() {
        assertThrows(UnsupportedOperationException.class, () -> ObjectConverter.convert(Random.class, null));
    }

    @Test
    public void convertObjectToOwnClassShouldReturnObject() {
        convertAndAssert(random, random);
    }

    @Test
    public void convertStringToIntegerShouldReturnInt() {
        random.ints(10).forEach(i -> ObjectConverterTest.convertAndAssert(i, String.valueOf(i)));
    }
    @Test
    public void convertStringToLongShouldReturnInt() {
        random.longs(10).forEach(i -> ObjectConverterTest.convertAndAssert(i, String.valueOf(i)));
    }

    @Test
    public void convertStringToOPCCommandHardwareAddressShouldWorkForMethod() {
        convertAndAssert(COMMAND_TYPE.METHOD, "method");
    }

    @Test
    public void convertStringToOPCCommandHardwareAddressShouldWorkForCLASSIC() {
        convertAndAssert(COMMAND_TYPE.CLASSIC, "CLASSIC");
    }

    @Test
    public void convertStringToOPCCommandHardwareAddressShouldNotWorkForOtherStrings() {
        assertThrows(UnsupportedOperationException.class, () -> ObjectConverter.convert(COMMAND_TYPE.class, "asd"));
    }

    @Test
    public void toDataTypeShouldDefaultToObject() {
        Class<?> actual = ObjectConverter.convert(Class.class, "asd");
        assertEquals(Object.class, actual);
    }

    @Test
    public void toDataTypeShouldReturnDataTypeIfValid() {
        convertAndAssert(String.class, String.class.getName());
    }

    private static <T> void convertAndAssert(T expected, Object s) {
        Object actual = ObjectConverter.convert(expected.getClass(), s);
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected, actual);
    }
}