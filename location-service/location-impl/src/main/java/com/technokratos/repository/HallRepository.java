package com.technokratos.repository;

import com.technokratos.model.Hall;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallRepository extends MongoRepository<Hall, String> {
}
