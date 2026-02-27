package com.mycontacts.main;

import com.mycontacts.repository.UserRepository;
import com.mycontacts.user.FreeUser;
import com.mycontacts.user.PremiumUser;
import com.mycontacts.user.User;
import com.mycontacts.util.PasswordHasher;
import com.mycontacts.validation.Validators;

import java.util.Scanner;

/**
 * UC-01: New user registration (console).
 * Prompts user for profile info, validates, hashes password and saves.
 * 
 * 
 * @author Developer
 * @version 1.0
 * 
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserRepository userRepo = new UserRepository();

        System.out.println("\t My Contacts App — Register");
        
        // Get inputs from user, validate them and save them in user repository
        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        String email;
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine().trim();
            if (!Validators.isValidEmail(email)) {
                System.out.println("Invalid email. Try again.");
                continue;
            }
            if (userRepo.existsByEmail(email)) {
                System.out.println("Email already registered. Try again.");
                continue;
            }
            break;
        }

        String password;
        while (true) {
            System.out.print("Password (min 8 chars, include letters & digits): ");
            password = sc.nextLine();
            if (!Validators.isStrongPassword(password)) {
                System.out.println("Weak password. Try again.");
                continue;
            }
            System.out.print("Confirm Password: ");
            String confirm = sc.nextLine();
            if (!password.equals(confirm)) {
                System.out.println("Passwords do not match. Try again.");
                continue;
            }
            break;
        }

        String type;
        while (true) {
            System.out.print("Account Type [FREE/PREMIUM]: ");
            type = sc.nextLine().trim().toUpperCase();
            if (type.equals("FREE") || type.equals("PREMIUM")) break;
            System.out.println("Please type FREE or PREMIUM.");
        }

        String hash = PasswordHasher.sha256(password);
        User user = type.equals("PREMIUM")
                ? new PremiumUser(name, email, hash)
                : new FreeUser(name, email, hash);

        userRepo.save(user);
        
        // Print success message and display the details
        System.out.println("Registration successful!");
        System.out.println("Created: " + user);
    }
}