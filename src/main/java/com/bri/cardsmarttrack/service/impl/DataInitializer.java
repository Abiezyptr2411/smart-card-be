package com.bri.cardsmarttrack.service.impl;

import com.bri.cardsmarttrack.entity.CreditCard;
import com.bri.cardsmarttrack.entity.Customer;
import com.bri.cardsmarttrack.entity.Subscription;
import com.bri.cardsmarttrack.entity.enums.*;
import com.bri.cardsmarttrack.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            log.info("Database already seeded with demo data");
            return;
        }

        log.info("Seeding demo data for BRI Card Smart Track...");

        // 1. Primary Demo Customer
        Customer budi = Customer.builder()
                .email("nasabah@bri.co.id")
                .password(passwordEncoder.encode("Password123!"))
                .fullName("Budi Pratama")
                .phoneNumber("+6281234567890")
                .build();

        // Card 1: BRI Touch Visa
        CreditCard card1 = CreditCard.builder()
                .cardName("BRI Touch Visa")
                .cardNumber("4000123456784321")
                .cardNetwork(CardNetwork.VISA)
                .creditLimit(new BigDecimal("25000000.00"))
                .availableLimit(new BigDecimal("18750000.00"))
                .expiryDate("10/28")
                .status(CardStatus.ACTIVE)
                .build();

        Subscription sub1 = Subscription.builder()
                .serviceName("Netflix Premium")
                .category(SubscriptionCategory.ENTERTAINMENT)
                .billingAmount(new BigDecimal("186000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(15))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://assets.nflxext.com/ffe/siteui/common/icons/monogram/netflix.png")
                .description("Paket Ultra HD 4K, 4 Perangkat")
                .build();

        Subscription sub2 = Subscription.builder()
                .serviceName("Spotify Family")
                .category(SubscriptionCategory.MUSIC)
                .billingAmount(new BigDecimal("86900.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(22))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://open.spotifycdn.com/cdn/images/favicon32.8e66b099.png")
                .description("Paket Keluarga hingga 6 Akun Premium")
                .build();

        Subscription sub3 = Subscription.builder()
                .serviceName("Google One 2TB")
                .category(SubscriptionCategory.CLOUD_STORAGE)
                .billingAmount(new BigDecimal("135000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://ssl.gstatic.com/images/branding/product/1x/google_one_48dp.png")
                .description("Penyimpanan Cloud 2TB & Google Photos")
                .build();

        card1.addSubscription(sub1);
        card1.addSubscription(sub2);
        card1.addSubscription(sub3);
        budi.addCreditCard(card1);

        // Card 2: BRI Wonderful Indonesia Mastercard
        CreditCard card2 = CreditCard.builder()
                .cardName("BRI Wonderful Indonesia Mastercard")
                .cardNumber("5200987654328765")
                .cardNetwork(CardNetwork.MASTERCARD)
                .creditLimit(new BigDecimal("50000000.00"))
                .availableLimit(new BigDecimal("43200000.00"))
                .expiryDate("04/29")
                .status(CardStatus.ACTIVE)
                .build();

        Subscription sub4 = Subscription.builder()
                .serviceName("Grab Unlimited")
                .category(SubscriptionCategory.TRANSPORTATION)
                .billingAmount(new BigDecimal("9000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(8))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://assets.grab.com/favicon.ico")
                .description("Voucher Diskon Ongkir & GrabFood")
                .build();

        Subscription sub5 = Subscription.builder()
                .serviceName("Disney+ Hotstar")
                .category(SubscriptionCategory.ENTERTAINMENT)
                .billingAmount(new BigDecimal("65000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(18))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://www.hotstar.com/assets/favicon-32x32.png")
                .description("Paket Bulanan Standard")
                .build();

        Subscription sub6 = Subscription.builder()
                .serviceName("iCloud+ 200GB")
                .category(SubscriptionCategory.CLOUD_STORAGE)
                .billingAmount(new BigDecimal("45000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(25))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://www.apple.com/favicon.ico")
                .description("Penyimpanan iCloud+ 200GB Family Sharing")
                .build();

        card2.addSubscription(sub4);
        card2.addSubscription(sub5);
        card2.addSubscription(sub6);
        budi.addCreditCard(card2);

        // Card 3: BRI JCB Platinum
        CreditCard card3 = CreditCard.builder()
                .cardName("BRI JCB Platinum")
                .cardNumber("3560112233441199")
                .cardNetwork(CardNetwork.JCB)
                .creditLimit(new BigDecimal("75000000.00"))
                .availableLimit(new BigDecimal("68500000.00"))
                .expiryDate("08/30")
                .status(CardStatus.ACTIVE)
                .build();

        Subscription sub7 = Subscription.builder()
                .serviceName("YouTube Premium Family")
                .category(SubscriptionCategory.ENTERTAINMENT)
                .billingAmount(new BigDecimal("99000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(12))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://www.youtube.com/s/desktop/99066669/img/favicon_32x32.png")
                .description("Bebas Iklan & YouTube Music Family")
                .build();

        Subscription sub8 = Subscription.builder()
                .serviceName("ChatGPT Plus")
                .category(SubscriptionCategory.PRODUCTIVITY)
                .billingAmount(new BigDecimal("330000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(20))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://chatgpt.com/favicon.ico")
                .description("Langganan OpenAI GPT-4o & Advanced Voice")
                .build();

        // Demo subscription that is already unsubscribed / inactive
        Subscription sub9 = Subscription.builder()
                .serviceName("Gramedia Digital Premium")
                .category(SubscriptionCategory.EDUCATION)
                .billingAmount(new BigDecimal("89000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().minusDays(5))
                .status(SubscriptionStatus.INACTIVE)
                .logoUrl("https://ebooks.gramedia.com/favicon.ico")
                .description("Paket Majalah & Buku Digital (Nonaktif)")
                .unsubscribedAt(LocalDateTime.now().minusDays(5))
                .build();

        card3.addSubscription(sub7);
        card3.addSubscription(sub8);
        card3.addSubscription(sub9);
        budi.addCreditCard(card3);

        customerRepository.save(budi);

        // 2. Secondary Customer (for testing cross-customer security: 403 Forbidden)
        Customer siti = Customer.builder()
                .email("siti@bri.co.id")
                .password(passwordEncoder.encode("Password123!"))
                .fullName("Siti Rahma")
                .phoneNumber("+6281987654321")
                .build();

        CreditCard card4 = CreditCard.builder()
                .cardName("BRI Easy Card Mastercard")
                .cardNumber("5100112233445566")
                .cardNetwork(CardNetwork.MASTERCARD)
                .creditLimit(new BigDecimal("15000000.00"))
                .availableLimit(new BigDecimal("14200000.00"))
                .expiryDate("06/28")
                .status(CardStatus.ACTIVE)
                .build();

        Subscription sub10 = Subscription.builder()
                .serviceName("Canva Pro")
                .category(SubscriptionCategory.PRODUCTIVITY)
                .billingAmount(new BigDecimal("95000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(14))
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl("https://www.canva.com/favicon.ico")
                .description("Canva Pro Design Subscription")
                .build();

        card4.addSubscription(sub10);
        siti.addCreditCard(card4);

        customerRepository.save(siti);

        log.info("Demo data seeding completed successfully! Seeded 2 customers, 4 credit cards, 10 subscriptions.");
    }
}
