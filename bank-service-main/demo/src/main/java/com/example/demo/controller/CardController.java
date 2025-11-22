package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Card;
import com.example.demo.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/cards")
@Transactional(readOnly = true)
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping
    @Transactional
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        try {
            Card savedCard = cardService.createCard(card);
            return ResponseEntity.ok(savedCard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) {
        Optional<Card> card = cardService.getCardById(id);
        return card.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Card> updateCard(@PathVariable Long id, @RequestBody Card cardDetails) {
        try {
            Card updatedCard = cardService.updateCard(id, cardDetails);
            return ResponseEntity.ok(updatedCard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteCard(@PathVariable Long id) {
        try {
            cardService.deleteCard(id);
            return ResponseEntity.ok("Card deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account/{accountId}")
    public List<Card> getCardsByAccount(@PathVariable Long accountId) {
        return cardService.getCardsByAccount(accountId);
    }
}