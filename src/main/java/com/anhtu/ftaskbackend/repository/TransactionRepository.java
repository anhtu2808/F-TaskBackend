package com.anhtu.ftaskbackend.repository;

import com.anhtu.ftaskbackend.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    Page<Transaction> findTransactionByUser_Id(Long userId, Pageable pageable);

}
