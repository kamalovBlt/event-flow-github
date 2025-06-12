package com.technokratos.repository;

import com.technokratos.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    @Query(value = "{'_id': ?0 }", fields = "{ 'userId': 1}")
    Optional<Location> findUserIdById(String id);

}
