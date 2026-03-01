package com.mycontacts.service.observer;

import com.mycontacts.contact.Contact;

// Observer interface for contact deletion events.
public interface ContactDeletionObserver {
    void onContactDeleted(Contact contact);
}
