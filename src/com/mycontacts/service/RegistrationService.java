package com.mycontacts.service;

import com.mycontacts.repository.UserRepository;
import com.mycontacts.user.FreeUser;
import com.mycontacts.user.PremiumUser;
import com.mycontacts.user.User;
import com.mycontacts.util.PasswordHasher;
import com.mycontacts.validation.Validators;

import java.util.Scanner;

// Handles user registration use case.
public class RegistrationService {
    private static final String FREE = "FREE";
    private static final String PREMIUM = "PREMIUM";

    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(Scanner sc) {
        System.out.println("\nRegister User");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        String email = readValidEmail(sc);
        String password = readValidPassword(sc);
        String type = readValidAccountType(sc);

        User user = PREMIUM.equals(type)
                ? new PremiumUser(name, email, PasswordHasher.sha256(password))
                : new FreeUser(name, email, PasswordHasher.sha256(password));

        userRepository.save(user);
        System.out.println("Registration successful!");
        System.out.println("Created: " + user);
    }

    private String readValidEmail(Scanner sc) {
        while (true) {
            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            if (!Validators.isValidEmail(email)) {
                System.out.println("Invalid email. Try again.");
            } else if (userRepository.existsByEmail(email)) {
                System.out.println("Email already registered. Try again.");
            } else {
                return email;
            }
        }
    }

    private String readValidPassword(Scanner sc) {
        while (true) {
            System.out.print("Password (min 8 chars, include letters & digits): ");
            String password = sc.nextLine();
            if (!Validators.isStrongPassword(password)) {
                System.out.println("Weak password. Try again.");
                continue;
            }

            System.out.print("Confirm Password: ");
            if (password.equals(sc.nextLine())) {
                return password;
            }
            System.out.println("Passwords do not match. Try again.");
        }
    }

    private String readValidAccountType(Scanner sc) {
        while (true) {
            System.out.print("Account Type [FREE/PREMIUM]: ");
            String type = sc.nextLine().trim().toUpperCase();
            if (FREE.equals(type) || PREMIUM.equals(type)) {
                return type;
            }
            System.out.println("Please type FREE or PREMIUM.");
        }
    }
}
