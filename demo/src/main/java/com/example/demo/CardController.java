package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CardController {
    private List<Card> cards = new ArrayList<>();
    private long nextId = 1;

    @PostMapping("/cards")
    public Card createCard(@RequestBody Card card) {
        card.setId(nextId++);
        cards.add(card);
        return card;
    }

    @GetMapping("/cards")
    public List<Card> getAllCards() {
        return cards;
    }

    @GetMapping("/cards/{id}")
    public Card getCardById(@PathVariable long id) {
        for (Card card : cards) {
            if (card.getId() == id) {
                return card;
            }
        }
        return null;
    }

    @PutMapping("/cards/{id}")
    public Card updateCard(@PathVariable long id, @RequestBody Card updatedCard) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId() == id) {
                updatedCard.setId(id);
                cards.set(i, updatedCard);
                return updatedCard;
            }
        }
        return null;
    }

    @DeleteMapping("/cards/{id}")
    public String deleteCard(@PathVariable long id) {
        cards.removeIf(card -> card.getId() == id);
        return "Card deleted";
    }
}

class Card {
    private long id;
    private String cardNumber;
    private long accountId;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }
}