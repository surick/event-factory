package com.itibia.esl.eventfactory.repository;

import com.itibia.esl.eventfactory.model.PbxHost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Jin
 * @since 2020/9/8
 */
@Repository
public interface PbxHostRepository extends JpaRepository<PbxHost, String> {


}
