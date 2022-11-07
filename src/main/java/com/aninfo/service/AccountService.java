package com.aninfo.service;

import com.aninfo.exceptions.DepositInvalidSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.exceptions.InvalidTransactionTypeException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService service;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - sum);
        accountRepository.save(account);

        return account;
    }

   @Transactional
    public Account deposit(Long cbu, Double sum) {
        Double cash = 0.0;
        if (sum <= 0) {
            throw new DepositInvalidSumException("Cannot deposit negative sums");
        }else if(sum>=2000 && sum<=5000){
            cash = sum*0.1;
        }else if(sum>5000){
            cash = 500.0;
        }
        Account account = accountRepository.findAccountByCbu(cbu);
        account.setBalance(account.getBalance() + sum + cash);
        accountRepository.save(account);

        return account;
    }

    public Transaction newDeposit(Transaction transaction){
        Double cash = 0.0;
        Account aux = accountRepository.findAccountByCbu(transaction.getCbu());
        if(aux == null)
            throw new InvalidTransactionTypeException("Account doesnt exist");
        if(transaction.getValue()>=2000 && transaction.getValue()<=5000){
            cash = transaction.getValue()*0.1;
        }else if(transaction.getValue()>5000){
            cash = 500.0;
        }
        deposit(aux.getCbu(), transaction.getValue()+cash);
        return service.createDeposit(transaction);
    }

    public Transaction newWithdraw(Transaction transaction){
        Account aux = accountRepository.findAccountByCbu(transaction.getCbu());
        if(aux == null)
            throw new InvalidTransactionTypeException("Account doesnt exist");
        withdraw(aux.getCbu(), transaction.getValue());
        return service.createWithdraw(transaction);
    }

    public void delete(Long id){
        service.deleteTransaction(id);
    }

    public TransactionService getService(){
        return service;
    }
}
