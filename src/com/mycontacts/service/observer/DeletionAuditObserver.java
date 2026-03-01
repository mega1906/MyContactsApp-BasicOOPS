package com.mycontacts.service.observer;

import com.mycontacts.contact.Contact;

// Logs deletion events to console.
public class DeletionAuditObserver implements ContactDeletionObserver {
    @Override
    public void onContactDeleted(Contact contact) {
        System.out.println("[Audit] delete -> " + contact.getReferenceId());
    }
}
