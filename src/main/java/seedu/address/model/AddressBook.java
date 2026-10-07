package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 * Assigns an ID to every person added without one.
 */
public class AddressBook implements ReadOnlyAddressBook {

    public static final String MESSAGE_PERSON_ID_CHANGED = "A person's ID cannot be changed.";

    private final UniquePersonList persons = new UniquePersonList();
    private int nextPersonId = 1;

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     * Persons without an ID are assigned one.
     */
    public void setPersons(List<Person> persons) {
        requireNonNull(persons);
        int newNextPersonId = nextPersonId;
        List<Person> personsWithIds = new ArrayList<>();
        for (Person person : persons) {
            Person personWithId = withId(person, newNextPersonId);
            newNextPersonId = nextPersonIdAfter(personWithId.getId().orElseThrow(), newNextPersonId);
            personsWithIds.add(personWithId);
        }

        this.persons.setPersons(personsWithIds);
        nextPersonId = newNextPersonId;
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        nextPersonId = newData.getNextPersonId();
        setPersons(newData.getPersonList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     * If the person does not have an ID, it is assigned the next available one.
     */
    public void addPerson(Person p) {
        requireNonNull(p);
        Person personWithId = withId(p, nextPersonId);
        persons.add(personWithId);
        nextPersonId = nextPersonIdAfter(personWithId.getId().orElseThrow(), nextPersonId);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     * {@code editedPerson} must have the same ID as {@code target}.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);
        checkArgument(target.getId().equals(editedPerson.getId()), MESSAGE_PERSON_ID_CHANGED);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    /**
     * Returns {@code person} if it already has an ID, otherwise a copy of it with the ID {@code nextId}.
     */
    private static Person withId(Person person, int nextId) {
        if (person.getId().isPresent()) {
            return person;
        }

        return new Person(new PersonId(nextId), person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags());
    }

    /**
     * Returns the next available ID number, given that {@code assignedId} is now in use.
     */
    private static int nextPersonIdAfter(PersonId assignedId, int currentNextId) {
        return Math.max(currentNextId, assignedId.getValue() + 1);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("nextPersonId", nextPersonId)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public int getNextPersonId() {
        return nextPersonId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && nextPersonId == otherAddressBook.nextPersonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, nextPersonId);
    }
}
