package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void addPerson_personsWithoutId_assignsIdsInOrder() {
        addressBook.addPerson(HOON);
        addressBook.addPerson(IDA);

        assertEquals(new PersonId("C1"), addressBook.getPersonList().get(0).getId().get());
        assertEquals(new PersonId("C2"), addressBook.getPersonList().get(1).getId().get());
        assertEquals(3, addressBook.getNextPersonId());
    }

    @Test
    public void addPerson_personWithId_keepsIdAndAdvancesCounter() {
        addressBook.addPerson(GEORGE); // C7
        addressBook.addPerson(HOON);

        assertEquals(GEORGE, addressBook.getPersonList().get(0));
        assertEquals(new PersonId("C8"), addressBook.getPersonList().get(1).getId().get());
    }

    @Test
    public void addPerson_afterRemovingHighestId_doesNotReuseId() {
        addressBook.addPerson(HOON); // C1
        addressBook.addPerson(IDA); // C2
        addressBook.removePerson(addressBook.getPersonList().get(1));
        addressBook.addPerson(AMY);

        assertEquals(new PersonId("C3"), addressBook.getPersonList().get(1).getId().get());
    }

    @Test
    public void addPerson_duplicatePerson_doesNotAdvanceCounter() {
        addressBook.addPerson(HOON);
        assertThrows(DuplicatePersonException.class, () -> addressBook.addPerson(HOON));
        assertEquals(2, addressBook.getNextPersonId());
    }

    @Test
    public void constructor_copy_keepsNextPersonId() {
        addressBook.addPerson(HOON); // C1
        addressBook.addPerson(IDA); // C2
        addressBook.removePerson(addressBook.getPersonList().get(1));

        AddressBook copy = new AddressBook(addressBook);
        assertEquals(3, copy.getNextPersonId());
        assertEquals(addressBook, copy);
    }

    @Test
    public void setPerson_differentId_throwsIllegalArgumentException() {
        addressBook.addPerson(ALICE);
        Person aliceWithDifferentId = new PersonBuilder(ALICE).withId("C99").build();
        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_PERSON_ID_CHANGED, () ->
                addressBook.setPerson(ALICE, aliceWithDifferentId));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook typicalAddressBook = getTypicalAddressBook();

        // same values -> returns true
        AddressBook typicalAddressBookCopy = getTypicalAddressBook();
        assertTrue(typicalAddressBook.equals(typicalAddressBookCopy));
        assertEquals(typicalAddressBook.hashCode(), typicalAddressBookCopy.hashCode());

        // same object -> returns true
        assertTrue(typicalAddressBook.equals(typicalAddressBook));

        // null -> returns false
        assertFalse(typicalAddressBook.equals(null));

        // different type -> returns false
        assertFalse(typicalAddressBook.equals(5));

        // different persons -> returns false
        assertFalse(typicalAddressBook.equals(addressBook));
    }

    @Test
    public void equals_differentNextPersonId_returnsFalse() {
        AddressBook otherAddressBook = new AddressBook();
        otherAddressBook.addPerson(HOON);
        otherAddressBook.removePerson(otherAddressBook.getPersonList().get(0));

        // same persons (none), different counter
        assertNotEquals(addressBook, otherAddressBook);
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", nextPersonId=" + addressBook.getNextPersonId() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public int getNextPersonId() {
            return 1;
        }
    }

}
