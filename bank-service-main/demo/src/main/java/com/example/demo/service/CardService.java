package com.example.demo.service;

import com.example.demo.entity.Card;
import com.example.demo.entity.Account;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Card createCard(Card card) {
        if (card.getAccount() == null || card.getAccount().getId() == null) {
            throw new IllegalArgumentException("Account is required");
        }

        Optional<Account> account = accountRepository.findById(card.getAccount().getId());
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }

        card.setAccount(account.get());
        return cardRepository.save(card);
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    public Card updateCard(Long id, Card cardDetails) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setCardNumber(cardDetails.getCardNumber());
        card.setCardHolderName(cardDetails.getCardHolderName());
        card.setExpiryDate(cardDetails.getExpiryDate());
        card.setCvv(cardDetails.getCvv());
        card.setCardType(cardDetails.getCardType());
        card.setIsActive(cardDetails.getIsActive());

        return cardRepository.save(card);
    }

    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new RuntimeException("Card not found");
        }
        cardRepository.deleteById(id);
    }

    public List<Card> getCardsByAccount(Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }
}