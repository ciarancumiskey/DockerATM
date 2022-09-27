package cumiskey.ciaran.DockerATM;

import org.apache.commons.compress.utils.Lists;
import org.testcontainers.shaded.com.google.common.collect.Maps;
import org.testng.annotations.Test;

import java.util.*;

@Test()
public class DockerAtmApplicationTests {

	@Test
	public void testWithdrawal() {
		final TreeMap<Integer, Integer> atmNotes = new TreeMap<>();
		atmNotes.put(5, 20);
		atmNotes.put(10, 30);
		atmNotes.put(20, 30);
		atmNotes.put(50, 10);
		final AutomatedTellerMachine testATM = new AutomatedTellerMachine(1500, atmNotes);
		assert(testATM.getCashAvailable() == 1500);

		final Map<Integer, Integer> withdrawal300 = testATM.withdrawCash(300);
		assert(testATM.getCashAvailable() == 1200);
		int[] expectedATMNoteCounts = {20, 30, 30, 4};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		int[] expectedWithdrawnNoteCounts = {0, 0, 0, 6};
		assertNotesMap(withdrawal300, expectedWithdrawnNoteCounts);

		//This should result in the ATM running out of €50 notes, and needing to make up the difference with 2 €20s and a €10
		final Map<Integer, Integer> withdrawal250 = testATM.withdrawCash(250);
		assert(testATM.getCashAvailable() == 950);
		expectedATMNoteCounts = new int[]{20, 29, 28, 0};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		expectedWithdrawnNoteCounts = new int[]{0, 1, 2, 4};
		assertNotesMap(withdrawal250, expectedWithdrawnNoteCounts);

		//This should result in the ATM running out of €20 notes, and needing to make up the difference with €10s
		final Map<Integer, Integer> withdrawal600 = testATM.withdrawCash(600);
		assert(testATM.getCashAvailable() == 350);
		expectedATMNoteCounts = new int[]{20, 25, 0, 0};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		expectedWithdrawnNoteCounts = new int[]{0, 4, 28, 0};
		assertNotesMap(withdrawal600, expectedWithdrawnNoteCounts);

		//This should result in the ATM running out of €10 notes, and needing to make up the difference with €5s
		final Map<Integer, Integer> withdrawal260 = testATM.withdrawCash(260);
		assert(testATM.getCashAvailable() == 90);
		expectedATMNoteCounts = new int[]{18, 0, 0, 0};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		expectedWithdrawnNoteCounts = new int[]{2, 25, 0, 0};
		assertNotesMap(withdrawal260, expectedWithdrawnNoteCounts);

		//The ATM should reject this withdrawal as it doesn't have enough cash to process it.
		final Map<Integer, Integer> withdrawal100 = testATM.withdrawCash(100);
		//The ATM will still have €90 in it, because the withdrawal wasn't processed.
		assert(testATM.getCashAvailable() == 90);
		expectedATMNoteCounts = new int[]{18, 0, 0, 0};
		assertNotesMap(testATM.getNotesAvailable(), expectedATMNoteCounts);
		expectedWithdrawnNoteCounts = new int[]{};
		assertNotesMap(withdrawal100, expectedWithdrawnNoteCounts);

	}

	/**
	 * Iterates through a Map of the note denominations and their respective counts to verify that the specified
	 * amount of them remains.
	 * @param notesMap - the map of note denominations and the counts of each denomination to be checked.
	 * @param expectedNoteCounts - the expected counts of each note, arranged in order of the denomination's size
	 *                           (e.g. €5 is first, and the largest note is last)
	 * @return true if the note counts match what was expected, false if not.
	 */
	private boolean assertNotesMap(final Map<Integer, Integer> notesMap, final int[] expectedNoteCounts) {
		final Iterator<Map.Entry<Integer, Integer>> iteratoredNoteCounts = notesMap.entrySet().iterator();
		int index = 0;
		while(iteratoredNoteCounts.hasNext()) {
			final Map.Entry<Integer, Integer> noteCounts = iteratoredNoteCounts.next();
			final Integer expectedNoteCount = Integer.valueOf(expectedNoteCounts[index]);
			if(!noteCounts.getValue().equals(expectedNoteCount)) {
				return false;
			}
		}
		return true;
	}
}
