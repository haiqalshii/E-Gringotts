package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.request.currencyconversion.NewCurrencyConversionRequestDto;
import com.bankhaven.egringotts.model.Transaction;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendReceiptEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendUserCreationMessage(String to, String userDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject("Welcome to E-Gringotts!");

        // Construct the email body with user details
        StringBuilder emailContent = new StringBuilder();
        emailContent.append(String.format(
                "Dear %s,\n\n" +
                        "🌟✨ Congratulations! You have successfully registered for E-Gringotts, the enchanted realm of financial wizardry! ✨🌟\n\n" +
                        "🔮✨ Your journey into the magical world of banking begins here! ✨🔮\n\n" +
                        "Here are the details of your registration:\n\n" +
                        "🧙‍♂️ **User Details:**\n%s\n\n" +
                        "🪄 If any of the details happen to be wrong, please contact us! 🪄\n\n" +
                        "Welcome to E-Gringotts, where dreams take flight on the wings of magic!\n\n" +
                        "Yours magically,\n" +
                        "The E-Gringotts Goblin",
                to, userDetails));

        message.setText(emailContent.toString());

        // Send the email
        emailSender.send(message);
    }

    public void sendAccountCreationMessage(String to, String accountDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject("E-Gringotts Account Creation Successful");

        // Construct the email body with user and account details
        StringBuilder emailContent = new StringBuilder();
        emailContent.append(String.format(
                "Dear %s,\n\n" +
                        "🌟✨ Abracadabra! You have successfully created an account for E-Gringotts, the enchanted realm of financial wizardry! ✨🌟\n\n" +
                        "Your journey with us has officially commenced, marking the beginning of a prosperous and enchanted financial future.\n\n" +
                        "Here are the details of your account:\n\n" +
                        "🔑 **Account Details:**\n%s\n\n" +
                        "As a valued member of E-Gringotts, your account serves as a key to an array of financial marvels and opportunities! 🌟\n\n" +
                        "May your vault be ever abundant and your financial endeavors spellbindingly successful! 🪄\n\n" +
                        "Yours faithfully,\n" +
                        "The E-Gringotts Goblin",
                to, accountDetails));

        message.setText(emailContent.toString());

        // Send the email
        emailSender.send(message);
    }

    public String generateReceiptForTransfer(Transaction transaction, String senderName, String receiverName) {
        return String.format(
                "🔮 E-GRINGOTTS RECEIPT 🔮\n\n" +
                        "✨ Transaction ID: %s\n" +
                        "📅 Date: %s\n" +
                        "🔄 From: %s\n" +
                        "🔄 To: %s\n" +
                        "💰 Amount: %s\n" +
                        "🏷️ Transaction Type: %s\n" +
                        "📂 Transaction Category: %s\n" +
                        "📝 Description: %s\n\n" +
                        "Thank you for using E-GRINGOTTS. Your magical transfer has been successfully completed. ✨\n\n" +
                        "For any inquiries or further assistance, owl us at support@egringotts.com. 🦉\n\n" +
                        "May your galleons multiply like Fizzing Whizbees! 💫",
                transaction.getId(),
                transaction.getDate(),
                senderName,
                receiverName,
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTransactionCategory(),
                transaction.getDescription()
        );
    }

    public String generateReceiptForDeposit(Transaction transaction) {
        return String.format(
                "🔮 E-GRINGOTTS RECEIPT 🔮\n\n" +
                        "✨ Transaction ID: %s\n" +
                        "📅 Date: %s\n" +
                        "🏦 To Account Number: %s\n" +
                        "💰 Account Balance: %s\n" +
                        "💸 Amount: %s\n" +
                        "🏷️ Transaction Type: %s\n" +
                        "📝 Description: %s\n\n" +
                        "Thank you for using E-GRINGOTTS. Your magical deposit has been successfully completed. ✨\n\n" +
                        "For any inquiries or further assistance, owl us at support@egringotts.com. 🦉\n\n" +
                        "May your galleons multiply like Fizzing Whizbees! 💫",
                transaction.getId(),
                transaction.getDate(),
                transaction.getReceiverAccount().getAccountNumber(),
                transaction.getReceiverAccount().getBalance(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getDescription()
        );
    }

    public String generateReceiptForWithdraw(Transaction transaction) {
        return String.format(
                "🔮 E-GRINGOTTS RECEIPT 🔮\n\n" +
                        "✨ Transaction ID: %s\n" +
                        "📅 Date: %s\n" +
                        "🏦 From Account Number: %s\n" +
                        "💰 Account Balance: %s\n" +
                        "💸 Amount: %s\n" +
                        "🏷️ Transaction Type: %s\n" +
                        "📝 Description: %s\n\n" +
                        "Thank you for using E-GRINGOTTS. Your magical withdrawal has been successfully completed. ✨\n\n" +
                        "For any inquiries or further assistance, owl us at support@egringotts.com. 🦉\n\n" +
                        "May your galleons multiply like Fizzing Whizbees! 💫",
                transaction.getId(),
                transaction.getDate(),
                transaction.getSenderAccount().getAccountNumber(),
                transaction.getSenderAccount().getBalance(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getDescription()
        );
    }

    public String generateReceiptForConversion(Transaction transaction, NewCurrencyConversionRequestDto currencyConversionRequestDto, String userName) {
        return String.format(
                "🔮 E-GRINGOTTS RECEIPT 🔮\n\n" +
                        "✨ Transaction ID: %s\n" +
                        "📅 Date: %s\n" +
                        "👤 User: %s\n" +
                        "💱 From Currency: %s\n" +
                        "💱 To Currency: %s\n" +
                        "💰 Converted Amount: %s\n" +
                        "🏷️ Transaction Type: %s\n" +
                        "📝 Description: %s\n\n" +
                        "Thank you for using E-GRINGOTTS. Your magical conversion has been successfully completed. ✨\n\n" +
                        "For any inquiries or further assistance, owl us at support@egringotts.com. 🦉\n\n" +
                        "May your galleons multiply like Fizzing Whizbees! 💫",
                transaction.getId(),
                transaction.getDate(),
                userName,
                currencyConversionRequestDto.getFromCurrency(),
                currencyConversionRequestDto.getToCurrency(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getDescription()
        );
    }
}
