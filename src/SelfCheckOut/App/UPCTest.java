package SelfCheckOut.App;

import static org.junit.Assert.*;
import SelfCheckOut.Exceptions.InvalidUPCException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UPCTest {
	
	UPC firstUPC;
	String firstCode;

	@Before
	public void setUp() throws Exception {
		firstCode = "123456789012";
		try {
			firstUPC = new UPC(firstCode);
		} catch (InvalidUPCException e) {
			fail("Invalid UPC");
		}
	}

	@After
	public void tearDown() throws Exception {
		firstUPC = null;
		firstCode = null;
	}
	
	@Test
	public void testGetCode() {
		String anotherCode = "999999999999";
		
		assertEquals(firstCode, firstUPC.getCode());		
		assertFalse(anotherCode.equals(firstUPC.getCode()));
		
	}
	
	@Test
	public void sameUPCs() throws InvalidUPCException{
		UPC secondUPC = new UPC(firstCode);
		assertTrue(firstUPC.equals(firstUPC));
		assertTrue(firstUPC.equals(secondUPC));		
	}
	
	@Test 
	public void twoDifferentUPCs() throws InvalidUPCException{
		UPC secondUPC = new UPC("123456789111");
		assertFalse(firstUPC.equals(secondUPC));	
	}

	@Test
	public void inputUPCWrongType() throws InvalidUPCException{
		Object obj = new Object();
		assertFalse(firstUPC.equals(obj));
	}
	
	@Test
	public void testHashCode() throws InvalidUPCException {
		UPC thirdUPC = new UPC("987654321012");
		assertEquals(thirdUPC.hashCode(), 2);
		assertFalse(thirdUPC.hashCode() == 3);
	}
}
