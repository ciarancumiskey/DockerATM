package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.apiobjects.ATMStatusResponse;
import cumiskey.ciaran.DockerATM.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DockerAtmApplicationTests {

	@Test
	public void testWithdrawal() {
		final TreeMap<Integer, Integer> atmNotes = new TreeMap<>();
		atmNotes.put(5, 20);
		atmNotes.put(10, 30);
		atmNotes.put(20, 30);
		atmNotes.put(50, 10);
		final AutomatedTellerMachine testATM = new AutomatedTellerMachine(1500, atmNotes);
		assertEquals(1500, testATM.getCashAvailable());

		final Map<Integer, Integer> withdrawal300 = testATM.withdrawCash(300);
		assertEquals(1200, testATM.getCashAvailable());
		int[] expectedATMNoteCounts = {20, 30, 30, 4};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		//The Map of the note denominations with the amount of each note should only have one entry, for €50 notes
		assertNull(withdrawal300.get(5));
		assertNull(withdrawal300.get(10));
		assertNull(withdrawal300.get(20));
		assertEquals(6, withdrawal300.get(50));

		//This should result in the ATM running out of €50 notes, and needing to make up the difference with 2 €20s and a €10
		final Map<Integer, Integer> withdrawal250 = testATM.withdrawCash(250);
		assertEquals(950, testATM.getCashAvailable());
		assertNull(withdrawal250.get(5));
		assertEquals(1, withdrawal250.get(10));
		assertEquals(2, withdrawal250.get(20));
		assertEquals(4, withdrawal250.get(50));

		//This should result in the ATM running out of €20 notes, and needing to make up the difference with €10s
		final Map<Integer, Integer> withdrawal600 = testATM.withdrawCash(600);
		assertEquals(350, testATM.getCashAvailable());
		assertNull(withdrawal600.get(5));
		assertEquals(4, withdrawal600.get(10));
		assertEquals(28, withdrawal600.get(20));
		assertNull(withdrawal600.get(50));

		//This should result in the ATM running out of €10 notes, and needing to make up the difference with €5s
		final Map<Integer, Integer> withdrawal260 = testATM.withdrawCash(260);
		assertEquals(90, testATM.getCashAvailable());
		assertEquals(2, withdrawal260.get(5));
		assertEquals(25, withdrawal260.get(10));
		assertNull(withdrawal260.get(20));
		assertNull(withdrawal260.get(50));

		//The ATM should reject this withdrawal as it doesn't have enough cash to process it.
		final Map<Integer, Integer> withdrawal100 = testATM.withdrawCash(100);
		//The ATM will still have €90 in it, because the withdrawal wasn't processed.
		assertEquals(90, testATM.getCashAvailable());
		expectedATMNoteCounts = new int[]{18, 0, 0, 0};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		assertNull(withdrawal100.get(5));
		assertNull(withdrawal100.get(10));
		assertNull(withdrawal100.get(20));
		assertNull(withdrawal100.get(50));
	}

	@Test
	public void testAddingCash() {
		final TreeMap<Integer, Integer> initialNotes = new TreeMap<>();
		initialNotes.put(5, 40);
		initialNotes.put(10, 40);
		initialNotes.put(20, 40);
		initialNotes.put(50, 20);
		final AutomatedTellerMachine testATM = new AutomatedTellerMachine(initialNotes);
		int[] expectedATMNoteCounts = new int[]{40, 40, 40, 20};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		assertEquals(2400, testATM.getCashAvailable());

		final TreeMap<Integer, Integer> additionalHundredEuroNotes = new TreeMap<>();
		additionalHundredEuroNotes.put(100, 10);
		testATM.addCash(additionalHundredEuroNotes);
		expectedATMNoteCounts = new int[]{40, 40, 40, 20, 10};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		assertEquals(3400, testATM.getCashAvailable());
	}

	@Test
	public void testCustomerWithdrawals() {
		final Customer customer = new Customer("1234", "0000", BigDecimal.valueOf(500.00), BigDecimal.valueOf(100));
		assertEquals("1234", customer.getAccountNumber());
		assertEquals(BigDecimal.valueOf(500.00), customer.getBalance());
		assertTrue(customer.withdraw(BigDecimal.valueOf(400)));
		assertEquals(BigDecimal.valueOf(100.00), customer.getBalance());
		assertTrue(customer.withdraw(BigDecimal.valueOf(150)));
		assertEquals(BigDecimal.valueOf(-50.00), customer.getBalance());
		assertFalse(customer.withdraw(BigDecimal.valueOf(100))); //this should exceed the overdraft, and thus return "false"
		assertEquals(BigDecimal.valueOf(-50.00), customer.getBalance()); //the balance should be unchanged, as the withdrawal was refused
	}

	@Test
	public void testATMStatus() {
		final TreeMap<Integer, Integer> atmInitialFunds = new TreeMap<>();
		atmInitialFunds.put(Integer.valueOf(10), Integer.valueOf(100));
		atmInitialFunds.put(Integer.valueOf(20), Integer.valueOf(50));
		atmInitialFunds.put(Integer.valueOf(50), Integer.valueOf(20));
		final AutomatedTellerMachine atm = new AutomatedTellerMachine(atmInitialFunds);
		assertEquals(3000, atm.getCashAvailable());
		final int[] expectedNoteCounts = new int[]{100, 50, 20};
		assertNotesMap(atm.getNotesAvailable(), expectedNoteCounts);

		final ATMStatusResponse atmStatusResponse = new ATMStatusResponse(atm);
		assertEquals(3000, atmStatusResponse.getFundsAvailable());
		assertNotesMap(atmStatusResponse.getNoteDenominationsAvailable(), expectedNoteCounts);
	}

	/**
	 * Iterates through a Map of the note denominations and their respective counts to verify that the specified
	 * amount of them remains.
	 * @param notesMap - the map of note denominations and the counts of each denomination to be checked.
	 * @param expectedNoteCounts - the expected counts of each note, arranged in order of the denomination's size
	 *                           (e.g. €5 is first, and the largest note is last)
	 * @return true if the note counts match what was expected, false if not.
	 */
	private void assertNotesMap(final Map<Integer, Integer> notesMap, final int[] expectedNoteCounts) {
		final Iterator<Map.Entry<Integer, Integer>> iteratoredNoteCounts = notesMap.entrySet().iterator();
		int index = 0;
		while(iteratoredNoteCounts.hasNext()) {
			final Map.Entry<Integer, Integer> noteCounts = iteratoredNoteCounts.next();
			final Integer expectedNoteCount = Integer.valueOf(expectedNoteCounts[index]);
			assertEquals(noteCounts.getValue(), expectedNoteCount);
			index++;
		}
	}
}
