package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.request.currencyconversion.NewCurrencyConversionRequestDto;
import com.bankhaven.egringotts.model.Account;
import com.bankhaven.egringotts.model.ConversionRate;
import com.bankhaven.egringotts.model.Currency;
import com.bankhaven.egringotts.model.Transaction;
import com.bankhaven.egringotts.model.enums.TransactionType;
import com.bankhaven.egringotts.repository.AccountRepository;
import com.bankhaven.egringotts.repository.ConversionRateRepository;
import com.bankhaven.egringotts.repository.CurrencyRepository;
import com.bankhaven.egringotts.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CurrencyConversionService {

    private final CurrencyRepository currencyRepository;
    private final ConversionRateRepository conversionRateRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    public CurrencyConversionService(CurrencyRepository currencyRepository, ConversionRateRepository conversionRateRepository, AccountService accountService, AccountRepository accountRepository, TransactionRepository transactionRepository, EmailService emailService) {
        this.currencyRepository = currencyRepository;
        this.conversionRateRepository = conversionRateRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void addCurrency(String currencyCode) {
        Currency currency = new Currency(currencyCode);
        currencyRepository.save(currency);
    }

    @Transactional
    public void addConversionRate(String fromCurrencyCode, String toCurrencyCode, double conversionRate, double fee) {
        Currency fromCurrency = currencyRepository.findById(fromCurrencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));
        Currency toCurrency = currencyRepository.findById(toCurrencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

        ConversionRate rate = new ConversionRate(fromCurrency, toCurrency, conversionRate, fee);
        conversionRateRepository.save(rate);
    }

    public String convertCurrency(NewCurrencyConversionRequestDto conversionRequestDto, String userId) {
        String accountNumber = conversionRequestDto.getAccountNumber();
        Account account = accountService.findAccountByAccountNumber(accountNumber, userId);
        Currency fromCurrency = currencyRepository.findById(conversionRequestDto.getFromCurrency())
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));
        Currency toCurrency = currencyRepository.findById(conversionRequestDto.getToCurrency())
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

        List<ConversionRate> conversionPath = findConversionPath(fromCurrency, toCurrency);
        if (conversionPath.isEmpty()) {
            throw new IllegalArgumentException("Conversion path not found");
        }

        // Debugging: Print the conversion path
        System.out.println("Conversion Path:");
        for (ConversionRate rate : conversionPath) {
            System.out.println("From " + rate.getFromCurrency().getCode() +
                    " to " + rate.getToCurrency().getCode() +
                    " Rate: " + rate.getConversionRate() +
                    " Fee: " + rate.getFee());
        }

        double conversionResult = calculateConversionResult(conversionPath, conversionRequestDto.getAmount());
        double processingFee = calculateProcessingFee(conversionPath, conversionRequestDto.getAmount());

        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(conversionRequestDto.getAmount())).subtract(BigDecimal.valueOf(processingFee)));
        accountRepository.save(account);

        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionType(TransactionType.CONVERSION);
        newTransaction.setDescription("Currency conversion from " + fromCurrency.getCode() + " to " + toCurrency.getCode());
        newTransaction.setSenderAccount(account);
        newTransaction.setAmount(BigDecimal.valueOf(conversionResult));

        transactionRepository.save(newTransaction);

        String userName = account.getUser().getFirstName() + " " + account.getUser().getLastName();
        String receipt = emailService.generateReceiptForConversion(newTransaction, conversionRequestDto, userName);
        emailService.sendReceiptEmail(account.getUser().getEmail(), "E-Gringotts Conversion Receipt", receipt);

        return "Conversion successful. A receipt has been sent to your email.";

    }

    private List<ConversionRate> findConversionPath(Currency fromCurrency, Currency toCurrency) {
        Queue<Currency> queue = new LinkedList<>();
        Map<Currency, Double> conversionRates = new HashMap<>();
        Map<Currency, ConversionRate> pathMap = new HashMap<>();
        queue.add(fromCurrency);
        conversionRates.put(fromCurrency, 1.0);

        while (!queue.isEmpty()) {
            Currency current = queue.poll();

            if (current.equals(toCurrency)) {
                List<ConversionRate> path = new LinkedList<>();
                while (pathMap.containsKey(current)) {
                    ConversionRate rate = pathMap.get(current);
                    path.add(0, rate);
                    current = rate.getFromCurrency();
                }
                return path;
            }

            List<ConversionRate> edges = conversionRateRepository.findByFromCurrency(current.getCode());
            for (ConversionRate edge : edges) {
                Currency nextCurrency = currencyRepository.findById(edge.getToCurrency().getCode())
                        .orElseThrow(() -> new IllegalArgumentException("Currency not found"));
                if (!conversionRates.containsKey(nextCurrency)) {
                    pathMap.put(nextCurrency, edge);
                    queue.add(nextCurrency);
                }
            }
        }

        return Collections.emptyList(); // Conversion path not found
    }

    private double calculateConversionResult(List<ConversionRate> path, double amount) {
        double result = amount;
        for (ConversionRate rate : path) {
            result *= rate.getConversionRate();
        }
        return result;
    }

    private double calculateProcessingFee(List<ConversionRate> path, double amount) {
        double totalFee = 0;

        for (ConversionRate rate : path) {
            totalFee += amount * rate.getFee();
        }

        return totalFee;
    }


}

