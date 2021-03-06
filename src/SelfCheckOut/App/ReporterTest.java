/**
 * 
 */
package SelfCheckOut.App;

import java.util.Calendar;
import java.util.Vector;
import org.junit.Test;

import SelfCheckOut.Exceptions.InvalidTaxException;
import SelfCheckOut.Exceptions.InvalidUPCException;

import junit.framework.TestCase;

public class ReporterTest extends TestCase {
	
	/**
	 * Test the tax rate of an empty record. 
	 */
	@Test
	public void testNothingBetweenDates(){
		Reporter keeper = new Reporter();
		Calendar endDate = Calendar.getInstance();
		Calendar startDate = Calendar.getInstance();
		startDate.set(2011, 2, 20);
		Vector<Record> result = keeper.getRecordsByDate(startDate, endDate);
		Vector<Record> empty = new Vector<Record>();
		assertEquals(result, empty);
	}

	@Test
	public void testEverythingBetweenDates() {
		CheckOutCart cart = new CheckOutCart();
		CheckOutCart cart2 = new CheckOutCart();
		CheckOutCart cart3 = new CheckOutCart();
		
		UPC upc1 = null;
		try {
			upc1 = new UPC("786936224306");
		} catch (InvalidUPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UPC upc2 = null;
		try {
			upc2 = new UPC("717951000842");
		} catch (InvalidUPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UPC upc3 = null;
		try {
			upc3 = new UPC("085392132225");
		} catch (InvalidUPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		CategoryDB categoryHT = CategoryDB.getInstance();
		try {
			categoryHT.setTaxRateForCategory("Cereal", 1);
		} catch (InvalidTaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			categoryHT.setTaxRateForCategory("Drink", 2);
		} catch (InvalidTaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PackagedProduct pp1 = new PackagedProduct("Kellogg Cereal", upc1,
				3.52, 1.35, "Cereal");
		PackagedProduct pp2 = new PackagedProduct("Coca Cola (12 pack)",
				upc2, 4.20, 4, "Drink");
		PackagedProduct pp3 = new PackagedProduct("Coca Cola (12 pack)",
				upc3, 7.20, 4, "Drink");
		
		GroceryItem gi1 = new GroceryItem(pp1, pp1.getPrice(), pp1.getWeight());
		GroceryItem gi2 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
		GroceryItem gi3 = new GroceryItem(pp3, pp3.getPrice(), pp3.getWeight());
		
		
		cart.addItemToCart(gi1);
		cart.addItemToCart(gi2);
		cart.addItemToCart(gi3);
		
		cart2.addItemToCart(gi1);
		cart2.addItemToCart(gi2);
		
		cart3.addItemToCart(gi1);
		cart3.addItemToCart(gi3);
		
		TransactionManager tm = TransactionManager.getInstance();
		tm.addRecord(cart);
		tm.addRecord(cart2);
		tm.addRecord(cart3);

		Reporter keeper = new Reporter();
		Vector<Record> allRecords = tm.getAllReports();
		Calendar endDate = Calendar.getInstance();
		Calendar startDate = Calendar.getInstance();
		startDate.set(2011, 2, 20);
		Vector<Record> result = keeper.getRecordsByDate(startDate, endDate);


		assertEquals(result.get(0).getItems().equals(allRecords.get(0).getItems()), false);
	}
	/**
     * Test getGroceryItems that returns a nested arrayList of all groceryItems
     * inside a record. No hashing involved.
     */
	@Test
    public void testGetGroceryInfo1(){
            
        try {
            
            Reporter rep = new Reporter();
            CheckOutCart cart = new CheckOutCart();
            
            TransactionManager manager = TransactionManager.getInstance();
            manager.flush();
            
            Vector<GroceryTableElement> main = new Vector<GroceryTableElement>();
            GroceryTableElement row1 = new GroceryTableElement();
            GroceryTableElement row2 = new GroceryTableElement();

            
            UPC upc1 = new UPC("786936224306");
            UPC upc2 = new UPC("717951000842");
            
            CategoryDB categoryHT = CategoryDB.getInstance();
            categoryHT.setTaxRateForCategory("Cereal", 1);
            categoryHT.setTaxRateForCategory("Drink", 2);

            PackagedProduct pp2 = new PackagedProduct("Coca Cola (12 pack)",
                            upc2, 3.20, 4, "Drink");
            
            PackagedProduct pp1 = new PackagedProduct("Kellogg Cereal", upc1,
                            3.52, 1.35, "Cereal");
            
            GroceryItem gi1 = new GroceryItem(pp1, pp1.getPrice(), pp1.getWeight());
            GroceryItem gi2 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
            
            cart.addItemToCart(gi1);
            cart.addItemToCart(gi2);
                                    
            row1.setProductName(gi1.getInfo().getDescription());
            row1.setNumPurchases(1);
            row1.setProductCategory(gi1.getInfo().getCategory());
            row1.setTotalPrice(gi1.getPrice());
            row1.setPromotion(gi1.getPromotion());
            row1.setTotalTax((gi1.getFinalPrice() - gi1.getPrice()));
            row1.setWeight(gi1.getWeight());
            
            row2.setProductName(gi2.getInfo().getDescription());
            row2.setNumPurchases(1);
            row2.setProductCategory(gi2.getInfo().getCategory());
            row2.setTotalPrice(gi2.getPrice());
            row2.setPromotion(gi2.getPromotion());
            row2.setTotalTax((gi2.getFinalPrice() - gi2.getPrice()));
            row2.setWeight(gi2.getWeight());

            
            main.add(row1);
            main.add(row2);
            //add record to transaction manager.
            manager.addRecord(cart);
            
			String startDate = "20/02/2011";
			String endDate = "30/12/2012";
    		
            Vector <GroceryTableElement> expected = rep.getGroceryInfo(startDate, endDate);
            
            assertEquals(main.get(0).getProductName(), expected.get(0).getProductName());
            assertEquals(main.get(1).getProductName(), expected.get(1).getProductName());
            
        }
        
        catch (Exception e) {
            //should not reach here
            assertFalse(true);
        }
    }
    
	/**
	 * Test getGroceryItems that returns a Vector of all groceryItems
	 * inside a record. Test the update feature where it hashes.
	 */
	@Test
	public void testGetGroceryInfo2(){
	        
	    try {
	        
	        Reporter rep = new Reporter();
	        CheckOutCart cart = new CheckOutCart();
	        
	        TransactionManager manager = TransactionManager.getInstance();
	        manager.flush();
	        
	        Vector<GroceryTableElement> main = new Vector<GroceryTableElement>();
	        GroceryTableElement row1 = new GroceryTableElement();
	        GroceryTableElement row2 = new GroceryTableElement();
	
	        
	        UPC upc1 = new UPC("786936224306");
	        UPC upc2 = new UPC("717951000842");
	        
	        CategoryDB categoryHT = CategoryDB.getInstance();
	        categoryHT.setTaxRateForCategory("Cereal", 1);
	        categoryHT.setTaxRateForCategory("Drink", 2);
	
	        PackagedProduct pp2 = new PackagedProduct("Coca Cola (12 pack)",
	                        upc2, 3.20, 4, "Drink");
	        
	        PackagedProduct pp1 = new PackagedProduct("Kellogg Cereal", upc1,
	                        3.52, 1.35, "Cereal");
	        
	        
	        GroceryItem gi1 = new GroceryItem(pp1, pp1.getPrice(), pp1.getWeight());
	        GroceryItem gi2 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
	        GroceryItem gi3 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
	        
	        cart.addItemToCart(gi1);
	        cart.addItemToCart(gi2);
	        cart.addItemToCart(gi3);
	                                
	        row1.setProductName(gi1.getInfo().getDescription());
	        row1.setNumPurchases(1);
	        row1.setProductCategory(gi1.getInfo().getCategory());
	        row1.setTotalPrice(gi1.getPrice());
	        row1.setPromotion(gi1.getPromotion());
	        row1.setTotalTax((gi1.getFinalPrice() - gi1.getPrice()));
	        row1.setWeight(gi1.getWeight());
	        
	        row2.setProductName(gi2.getInfo().getDescription());
	        row2.setNumPurchases(2);
	        row2.setProductCategory(gi2.getInfo().getCategory());
	        row2.setTotalPrice(gi2.getPrice());
	        row2.setPromotion(gi2.getPromotion());
	        row2.setTotalTax((gi2.getFinalPrice() - gi2.getPrice()));
	        row2.setWeight(gi2.getWeight());
	
	        
	        main.add(row1);
	        main.add(row2);
	        //add record to transaction manager.
	        manager.addRecord(cart);
	        
			String startDate = "20/05/2012";
			String endDate = "24/12/2012";
			
	        Vector <GroceryTableElement> expected = rep.getGroceryInfo(startDate, endDate);
	        assertEquals(main.get(0).getNumPurchases(), expected.get(0).getNumPurchases());
	        assertEquals(main.get(1).getNumPurchases(), expected.get(1).getNumPurchases());
	        
	    }
	    
	    catch (Exception e) {
	        //should not reach here
	        assertFalse(true);
	    }
	}
	
	/**
	 * Test processing a date as a string representative
	 */
	@Test
	public void testProcessCalendar(){
		
		String date = "11/12/10";
		Calendar cal;
		Calendar res;
		cal = Calendar.getInstance();
		cal.set(2010, 11, 11);
		
		Reporter rep = new Reporter();
		res = rep.processCalendar(date);
		assertEquals(res.get(Calendar.YEAR), cal.get(Calendar.YEAR));
		assertEquals(res.get(Calendar.MONTH), cal.get(Calendar.MONTH));
	}
	
	/**
	 * Test getAllReports
	 */
	@Test
	public void testGetAllReports() {
		
		TransactionManager manager;
		manager = TransactionManager.getInstance();
		manager.flush();
		try {
            
            CheckOutCart cart = new CheckOutCart();
            CheckOutCart cart2 = new CheckOutCart();
            
            Vector<Record> main = new Vector<Record>();

            
            UPC upc1 = new UPC("786936224306");
            UPC upc2 = new UPC("717951000842");
            
            CategoryDB categoryHT = CategoryDB.getInstance();
            categoryHT.setTaxRateForCategory("Cereal", 1);
            categoryHT.setTaxRateForCategory("Drink", 2);

            PackagedProduct pp2 = new PackagedProduct("Coca Cola (12 pack)",
                            upc2, 3.20, 4, "Drink");
            
            PackagedProduct pp1 = new PackagedProduct("Kellogg Cereal", upc1,
                            3.52, 1.35, "Cereal");
            
            GroceryItem gi1 = new GroceryItem(pp1, pp1.getPrice(), pp1.getWeight());
            GroceryItem gi2 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
            GroceryItem gi3 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
            
            cart.addItemToCart(gi1);
            cart.addItemToCart(gi2);
            
            cart2.addItemToCart(gi1);
            cart2.addItemToCart(gi2);
            cart2.addItemToCart(gi3);
            
            Record r1 = new Record(cart, 0);
            Record r2 = new Record(cart, 1);
            
            manager.addRecord(cart);
            manager.addRecord(cart2);
            
            main.add(r1);
            main.add(r2);
            
            Vector<Record> expected = manager.getAllReports();
            
            assertEquals(main.elementAt(0).getPostTaxPrice(), expected.elementAt(0).getPostTaxPrice());
		}
		catch (Exception e) {
			assertFalse(true);
		}
	}
	/**
	 * 
	 */
	@Test
	public void testGetCategory() {
		
		TransactionManager manager;
		Reporter r = new Reporter();
		manager = TransactionManager.getInstance();
		manager.flush();
		try {
            
            CheckOutCart cart = new CheckOutCart();
            CheckOutCart cart2 = new CheckOutCart();
            
            
            UPC upc1 = new UPC("786936224306");
            UPC upc2 = new UPC("717951000842");
            
            CategoryDB categoryHT = CategoryDB.getInstance();
            categoryHT.setTaxRateForCategory("Cereal", 1);
            categoryHT.setTaxRateForCategory("Drink", 2);

            PackagedProduct pp2 = new PackagedProduct("Coca Cola (12 pack)",
                            upc2, 3.20, 4, "Drink");
            
            PackagedProduct pp1 = new PackagedProduct("Kellogg Cereal", upc1,
                            3.52, 1.35, "Cereal");
            
            GroceryItem gi1 = new GroceryItem(pp1, pp1.getPrice(), pp1.getWeight());
            GroceryItem gi2 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
            GroceryItem gi3 = new GroceryItem(pp2, pp2.getPrice(), pp2.getWeight());
            
            cart.addItemToCart(gi1);
            cart.addItemToCart(gi2);
            
            cart2.addItemToCart(gi1);
            cart2.addItemToCart(gi2);
            cart2.addItemToCart(gi3);
            
            manager.addRecord(cart);
            manager.addRecord(cart2);
            
            Vector<GroceryTableElement> result = r.getReportsByCategory("Drink", "1/1/2000", "10/12/2012");
            assertEquals(result.get(0).getProductName(), "Coca Cola (12 pack)");
            assertEquals(result.get(0).getProductCategory(), "Drink");
		}
		catch (Exception e) {
			assertFalse(true);
		}
	}
	
}

