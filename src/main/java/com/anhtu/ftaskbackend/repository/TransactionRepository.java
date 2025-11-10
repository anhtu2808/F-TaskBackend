package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Transaction;
import com.anhtu.ftaskbackend.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    Page<Transaction> findTransactionByUser_Id(Long userId, Pageable pageable);
    Page<Transaction> findByType(TransactionType transactionType, Pageable pageable);
    Double sumTransactionByType(TransactionType transactionType);
}
