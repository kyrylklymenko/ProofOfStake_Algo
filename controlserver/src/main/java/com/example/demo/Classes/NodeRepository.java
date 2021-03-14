package com.example.demo.Classes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends CrudRepository<Node, Integer> {
}
