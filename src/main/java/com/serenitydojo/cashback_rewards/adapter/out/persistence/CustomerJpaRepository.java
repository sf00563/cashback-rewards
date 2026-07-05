package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
}
