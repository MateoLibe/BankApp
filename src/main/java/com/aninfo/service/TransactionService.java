package com.aninfo.service;

import com.aninfo.exceptions.DepositInvalidSumException;
import com.aninfo.exceptions.TransactionDoesntExistException;
import com.aninfo.exceptions.WithdrawInvalidSumException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createDeposit(Transaction transaction) {
        Double cash = transaction.getValue();
        if (transaction.getValue() <= 0) {
            throw new DepositInvalidSumException("Sum must be higher than 0");
        }else if(transaction.getValue() >= 2000 && transaction.getValue() <= 5000){
            cash *= 1.1;
        }else if(transaction.getValue() > 5000){
            cash += 500;
        }
        transaction.setValue(cash);
        return transactionRepository.save(transaction);
    }

    public Transaction createWithdraw(Transaction transaction) {
        if (transaction.getValue() <= 0) {
            throw new WithdrawInvalidSumException("Sum must be higher than 0");
        }
        Double cash = -transaction.getValue();
        transaction.setValue(cash);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getClientTransactions(Long cbu){
        return this.transactionRepository.findAllByCbu(cbu);
    }

    public Transaction getTransaction(Long id){
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);

        if (!transactionOptional.isPresent()) {
            throw new TransactionDoesntExistException("Transaction can't be found");
        }
        return transactionOptional.get();
    }

    public void deleteTransaction(Long id){
        transactionRepository.deleteById(id);
    }

    public List<Transaction> showTransactions(){
        return this.transactionRepository.findAll();
    }

}
