package com.spring.springRestDemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.spring.springRestDemo.model.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

}
