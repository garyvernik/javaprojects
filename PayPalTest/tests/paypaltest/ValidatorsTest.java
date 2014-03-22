package paypaltest;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class ValidatorsTest {
	
	@Test 
	public void testAprValidator() {
		assertTrue(Validators.getAprValidator().isValid(0.3));
		assertTrue(Validators.getAprValidator().isValid(0.000001));
		assertTrue(Validators.getAprValidator().isValid(1d));
		assertTrue(Validators.getAprValidator().isValid(50d));
		assertTrue(Validators.getAprValidator().isValid(5.566654));
		assertTrue(Validators.getAprValidator().isValid(100d));
		
		assertFalse(Validators.getAprValidator().isValid(0.0000001));
		assertFalse(Validators.getAprValidator().isValid(0d));
		assertFalse(Validators.getAprValidator().isValid(-1d));
		assertFalse(Validators.getAprValidator().isValid(101d));
		assertFalse(Validators.getAprValidator().isValid(103.6));
	}

	@Test 
	public void testTermValidator() {
		assertTrue(Validators.getTermValidator().isValid(1d));
		assertTrue(Validators.getTermValidator().isValid(5d));
		assertTrue(Validators.getTermValidator().isValid(999999d));
		assertTrue(Validators.getTermValidator().isValid(1000000d));
		assertTrue(Validators.getTermValidator().isValid(1000000.0000));
		
		assertFalse(Validators.getTermValidator().isValid(0d));
		assertFalse(Validators.getTermValidator().isValid(-1d));
		assertFalse(Validators.getTermValidator().isValid(1.2));
		assertFalse(Validators.getTermValidator().isValid(133.3333));
		assertFalse(Validators.getTermValidator().isValid(1000000.00001));
	}
	
}
