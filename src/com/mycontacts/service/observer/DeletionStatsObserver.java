package com.mycontacts.service.observer;

import com.mycontacts.contact.Contact;

// Keeps runtime deletion count.
public class DeletionStatsObserver implements ContactDeletionObserver {
    private int deleteCount;

    @Override
    public void onContactDeleted(Contact contact) {
        deleteCount++;
        System.out.println("[Stats] deleted=" + deleteCount);
    }
}
