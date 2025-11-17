package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Card;
import com.example.demo.entity.Account;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cards")
@Transactional(readOnly = true)
public class CardController {
    
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        // Проверяем существование счета
        if (card.getAccount() == null || card.getAccount().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Account> account = accountRepository.findById(card.getAccount().getId());
        if (account.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        card.setAccount(account.get());
        Card savedCard = cardRepository.save(card);
        return ResponseEntity.ok(savedCard);
    }

    @GetMapping
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) {
        Optional<Card> card = cardRepository.findById(id);
        return card.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Card> updateCard(@PathVariable Long id, @RequestBody Card cardDetails) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            card.setCardNumber(cardDetails.getCardNumber());
            card.setCardHolderName(cardDetails.getCardHolderName());
            card.setExpiryDate(cardDetails.getExpiryDate());
            card.setCvv(cardDetails.getCvv());
            card.setCardType(cardDetails.getCardType());
            card.setIsActive(cardDetails.getIsActive());
            Card updatedCard = cardRepository.save(card);
            return ResponseEntity.ok(updatedCard);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteCard(@PathVariable Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return ResponseEntity.ok("Card deleted");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/account/{accountId}")
    public List<Card> getCardsByAccount(@PathVariable Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }
}