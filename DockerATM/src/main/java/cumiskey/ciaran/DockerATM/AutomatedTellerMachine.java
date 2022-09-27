package cumiskey.ciaran.DockerATM;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutomatedTellerMachine {

  //The ATM can only dispense notes, which are all denominated in whole numbers.
  private int cashAvailable;

  //The key is the note's value (e.g. €5, €10), the value is the number of those notes in stock.
  //I picked a TreeMap as it allowed for entries to be sorted by keys, I am aware that their insertion operations are
  //now O(log N), but given that each entry represents a note and that there's only 7 kinds of Euro notes, I don't
  //foresee this having a massive impact on performance.
  private TreeMap<Integer, Integer> notesAvailable = new TreeMap<>(Comparator.reverseOrder());

  private static Logger logger = Logger.getLogger("AutomatedTellerMachine");

  public AutomatedTellerMachine(final int cashAvailable, final TreeMap<Integer, Integer> notesAvailable) {
    this.cashAvailable = cashAvailable;
    this.notesAvailable = notesAvailable;
  }

  public AutomatedTellerMachine(final TreeMap<Integer, Integer> notesAvailable) {
    this.notesAvailable = notesAvailable;
    for (final Map.Entry<Integer, Integer> noteValueAndCount: notesAvailable.entrySet()) {
      final int totalValueOfNotes = noteValueAndCount.getValue() * noteValueAndCount.getKey();
      this.cashAvailable += totalValueOfNotes;
    }
  }

  public int getCashAvailable() {
    return cashAvailable;
  }

  public void setCashAvailable(int cashAvailable) {
    this.cashAvailable = cashAvailable;
  }

  public TreeMap<Integer, Integer> getNotesAvailable() {
    return notesAvailable;
  }

  public void setNotesAvailable(TreeMap<Integer, Integer> notesAvailable) {
    this.notesAvailable = notesAvailable;
  }

  public void updateNoteCount(final Integer noteValue, final Integer newNoteCount) {
    this.notesAvailable.put(noteValue, newNoteCount);
  }

  protected void addCash(Map<Integer, Integer> addedNotes) {
    for(final Map.Entry<Integer, Integer> noteValueAndCount : notesAvailable.entrySet()) {
      final Integer noteDenomination = noteValueAndCount.getKey();
      final Integer currentNoteCount = this.notesAvailable.get(noteDenomination);
      if(currentNoteCount != null) {
        final int newNoteCount = currentNoteCount + noteValueAndCount.getValue();
        this.notesAvailable.put(noteDenomination, newNoteCount);
      } else {
        this.notesAvailable.put(noteDenomination, noteValueAndCount.getValue());
      }
      this.cashAvailable += noteDenomination * noteValueAndCount.getValue();
    }
  }

  protected Map<Integer, Integer> withdrawCash(final int withdrawalAmount) {
    //There's not enough cash in the machine to satisfy the withdrawal request.
    if(this.cashAvailable < withdrawalAmount) {
      logger.log(Level.SEVERE, "This ATM doesn't have enough cash to process this request.");
      return new HashMap<>();
    }
    final Map<Integer, Integer> withdrawnNotesWithCounts = new HashMap<>();

    //TODO: if a withdrawal causes the ATM to run out of a particular denomination, log it.
    //There's no point giving out €50s if the user only requested €40
    int amountLeftToWithdraw = withdrawalAmount;
    for(final Map.Entry<Integer, Integer> noteValueAndCount : this.notesAvailable.descendingMap().entrySet()) {
      if (noteValueAndCount.getKey() < withdrawalAmount && noteValueAndCount.getValue() > 0) {
        final int notesAvailable = noteValueAndCount.getValue();
        final int noteValue = noteValueAndCount.getKey();
        //Use int division, as it will round down to the last whole number (e.g. 260/50 => 5)
        final int numNotesRequested = amountLeftToWithdraw / noteValue;
        //e.g. if the user requested €100 and there's at least 2 €50s available, add that to the withdrawal
        if (numNotesRequested <= notesAvailable) {
          withdrawnNotesWithCounts.put(noteValue, numNotesRequested);
          final int totalNoteValue = noteValue * numNotesRequested;
          updateNoteCount(noteValue, (notesAvailable - numNotesRequested));
          amountLeftToWithdraw -= totalNoteValue;
          this.cashAvailable -= totalNoteValue;
        } else {
          /*  The user's withdrawal will cause the ATM to run out of that particular denomination,
              e.g. a €300 withdrawal for a machine with only 5 €50s left. */
          withdrawnNotesWithCounts.put(noteValue, notesAvailable);
          final int totalNoteValue = noteValue * notesAvailable;
          updateNoteCount(noteValue, 0);
          amountLeftToWithdraw -= totalNoteValue;
          this.cashAvailable -= totalNoteValue;
        }
        //The withdrawal is complete, return the notes
        if(amountLeftToWithdraw == 0) {
          return withdrawnNotesWithCounts;
        }
      }
    }
    //The ATM might have run out of notes that could process the withdrawal request,
    // e.g. a user requesting €40 when there are only €50s and less than 8 €5 notes left
    logger.log(Level.SEVERE, "This ATM doesn't have the right banknotes to process this request.");
    return new HashMap<>();
  }
}
