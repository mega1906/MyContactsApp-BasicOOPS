package com.mycontacts.service.observer;

import com.mycontacts.contact.Contact;

import java.util.ArrayList;
import java.util.List;

// Subject that notifies observers when deletion happens.
public class ContactDeletionNotifier {
    private final List<ContactDeletionObserver> observers = new ArrayList<>();

    public void registerObserver(ContactDeletionObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void unregisterObserver(ContactDeletionObserver observer) {
        observers.remove(observer);
    }

    public void notifyDeleted(Contact contact) {
        for (ContactDeletionObserver observer : observers) {
            observer.onContactDeleted(contact);
        }
    }
}
